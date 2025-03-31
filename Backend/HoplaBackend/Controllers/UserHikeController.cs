using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.DTOs;
using HoplaBackend.Services;
using System.IO;
using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;
using HoplaBackend.Helpers;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using System.Text.Json;
using Org.BouncyCastle.Bcpg;

namespace HoplaBackend.Controllers;

[Route("userhikes")]
[ApiController]
public class UserHikeController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;
    private readonly UserHikeService _userHikeService;

    public UserHikeController(Authentication authentication, AppDbContext context, UserHikeService userHikeService)
    {
        _authentication = authentication;
        _context = context;
        _userHikeService = userHikeService;
    }

    [Authorize]
    [HttpGet("user")]
    public async Task<ActionResult<List<TrailDto>>> GetUserHikes(
        [FromQuery] Guid? userId, 
        [FromQuery] int? pageNumber, 
        [FromQuery] int? pageSize)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }   
        bool paging = pageNumber.HasValue && pageSize.HasValue;
            if (paging && (pageNumber < 1 || pageSize < 1))
                return BadRequest("pageNumber og pageSize må være større enn 0.");
    
        int page = pageNumber ?? 1; // Standard til 1 hvis null
        int size = pageSize ?? 10;  // Standard til 10 hvis null

        if (!userId.HasValue) // Henter innlogget brukers turer (userid fra Token)
        {
            var userHikes = await _userHikeService.GetUserHikes(parsedUserId, page,size);
            return Ok(new
            {
                userHikes,
                page,
                size
            }); 
        }
        else // Hvis userid er spesifisert i query
        {
            // legg inn sjekk på om dem er venner?
            var userHikes = await _userHikeService.GetUserHikes(userId.Value, page,size);
            return Ok(new
            {
                userHikes,
                page,
                size
            }); 
        }
        
    }

    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateUserHike([FromBody] CreateUserHikeDto dto)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        var hikeId = Guid.NewGuid();
        var startMs = new DateTimeOffset(dto.StartedAt).ToUnixTimeMilliseconds();

        var convertedCoords = dto.Coordinates.Select(c =>
        {
            var offset = (int)((c.Timestamp - startMs) / 100); // 1/10 sekunder
            return (offset, c.Lat, c.Lng);
        }).ToList();

        var details = new UserHikeDetail
        {
            UserHikeId = hikeId,
            Description = dto.Description,
            Coordinates = convertedCoords
        };

        var hike = new UserHike
        {
            Id = hikeId,
            UserId = userId,
            TrailId = dto.TrailId,
            HorseId = dto.HorseId,
            StartedAt = dto.StartedAt,
            CreatedAt = DateTime.UtcNow, 
            Distance = dto.Distance,
            Duration = dto.Duration
        };

        _context.UserHikes.Add(hike);
        _context.UserHikeDetails.Add(details);
        await _context.SaveChangesAsync();

        return Ok(new { hike.Id });
    }


    [Authorize]
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateUserHike(Guid id, [FromBody] UpdateUserHikeDto dto)
    {
        
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        

        var hike = await _context.UserHikes.FirstOrDefaultAsync(h => h.Id == id);


        var hikeDetail = await _context.UserHikeDetails.FirstOrDefaultAsync(hd => hd.UserHikeId == id);
        if (hike == null || hikeDetail == null)
            return NotFound("Fant ikke turen.");
        
        // Sjekk at brukeren eier turen
        if (hike.UserId != userId)
            return Unauthorized(new {message = "Du kan ikke endre andre sine turer"});

        hike.Title = dto.Title ?? hike.Title;
        hike.PictureUrl = dto.PictureUrl ?? hike.PictureUrl;
        hikeDetail.Description = dto.Description ?? hikeDetail.Description;

        await _context.SaveChangesAsync();
        return Ok(new { Message = "Turen er oppdatert." });
    }

}