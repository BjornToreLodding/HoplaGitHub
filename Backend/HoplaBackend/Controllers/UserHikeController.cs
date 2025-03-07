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
}