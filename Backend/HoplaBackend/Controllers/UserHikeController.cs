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
using System.Text.Json.Serialization;
using System.Globalization;

namespace HoplaBackend.Controllers;

[Route("userhikes")]
[ApiController]
public class UserHikeController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;
    private readonly UserHikeService _userHikeService;
    private readonly ImageUploadService _imageUploadService;
    public UserHikeController(Authentication authentication, AppDbContext context, UserHikeService userHikeService, ImageUploadService imageUploadService)
    {
        _authentication = authentication;
        _context = context;
        _userHikeService = userHikeService;
        _imageUploadService = imageUploadService;
    }


    [HttpGet("coordinates/{userHikeId}")]
    public async Task<IActionResult> GetUserHikeCoordinates(Guid userHikeId, [FromQuery] int? maxPoints = null)
    {
        var details = await _context.UserHikeDetails
            .FirstOrDefaultAsync(d => d.UserHikeId == userHikeId);

        if (details == null || string.IsNullOrEmpty(details.CoordinatesCsv))
        {
            return NotFound(new { Message = "No coordinates found for this UserHike." });
        }

        // Hent kun Lat/Lng (ignorer OffsetTenths)
        var coordinates = CoordinateHelper.ParseLatLngOnly(details.CoordinatesCsv);

        if (maxPoints.HasValue)
        {
            coordinates = CoordinateHelper.DownsampleCoordinates(coordinates, maxPoints.Value);
        }

        // Returner liste av lat/lng
        return Ok(coordinates.Select(c => new { lat = c.Lat, lng = c.Lng }));
    }

    [HttpGet("coordinates/mock/{userHikeId}")]
    public async Task<IActionResult> GetMockUserHikeCoordinates(Guid userHikeId, [FromQuery] int points = 500)
    {
        var userHike = await _context.UserHikes
            .Include(u => u.Trail) // Viktig å inkludere Trail
            .Include(u => u.UserHikeDetail) // Og UserHikeDetail
            .FirstOrDefaultAsync(u => u.Id == userHikeId);

        if (userHike == null)
        {
            return NotFound(new { Message = "No UserHike found for this ID." });
        }

        double lat = 0;
        double lng = 0;
        double distance = 2.0; // Default fallback

        // Prøv å hente fra UserHikeDetail først
        if (userHike.UserHikeDetail != null && 
            (userHike.UserHikeDetail.LatMean != 0 || userHike.UserHikeDetail.LongMean != 0))
        {
            lat = userHike.UserHikeDetail.LatMean;
            lng = userHike.UserHikeDetail.LongMean;
        }
        // Hvis ikke: prøv å hente fra Trail
        else if (userHike.Trail != null)
        {
            lat = userHike.Trail.LatMean;
            lng = userHike.Trail.LongMean;
            distance = userHike.Trail.Distance;
        }

        // Hvis fortsatt 0, fallback til midt i Norge
        if (lat == 0 && lng == 0)
        {
            lat = 60.0;
            lng = 10.0;
        }

        // Generer mock koordinater
        var allCoords = MockHelper.GenerateCircularTrail(lat, lng, distance);
        var downsampled = CoordinateHelper.DownsampleCoordinates(allCoords, points);

        return Ok(downsampled.Select(c => new { lat = c.Lat, lng = c.Lng }));
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
                size,
                
                
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
    public async Task<IActionResult> CreateUserHike([FromForm] CreateUserHikeForm request)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        if (!await _context.Users.AnyAsync(u => u.Id == userId))
            return Unauthorized(new { message = "Bruker ikke funnet" });

        var hikeId = Guid.NewGuid();
        var startMs = new DateTimeOffset(request.StartedAt).ToUnixTimeMilliseconds();

        if (string.IsNullOrEmpty(request.Coordinates))
            return BadRequest(new { message = "Coordinates mangler." });

        // Deserialize Coordinates
        List<CoordinateInput> coordinateList;
        try
        {
            var serializerOptions = new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true,
            };
            coordinateList = JsonSerializer.Deserialize<List<CoordinateInput>>(request.Coordinates, serializerOptions);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = "Ugyldig koordinat-data", error = ex.Message });
        }

        // Parse Distance og Duration, godta både punktum og komma
        string distanceInput = request.Distance?.Replace(',', '.');
        string durationInput = request.Duration?.Replace(',', '.');

        if (!double.TryParse(distanceInput, NumberStyles.Float, CultureInfo.InvariantCulture, out double distance))
            return BadRequest(new { message = "Ugyldig tallformat for Distance" });

        if (!double.TryParse(durationInput, NumberStyles.Float, CultureInfo.InvariantCulture, out double duration))
            return BadRequest(new { message = "Ugyldig tallformat for Duration" });

        // Konverter koordinater
        var convertedCoords = coordinateList.Select(c =>
        {
            var offset = (int)c.Timestamp; // millisekunder
            return (offset, c.Lat, c.Long);
        }).ToList();

        // Laste opp bilde hvis det finnes
        string? pictureUrl = null;
        if (request.Image != null)
        {
            var fileName = await _imageUploadService.UploadImageAsync(request.Image);
            pictureUrl = fileName;
        }

        var details = new UserHikeDetail
        {
            UserHikeId = hikeId,
            Description = request.Description,
            Coordinates = convertedCoords
            // Her kan du lagre pictureUrl senere hvis ønskelig
        };

        var hike = new UserHike
        {
            Id = hikeId,
            UserId = userId,
            Title = request.Title,
            Comment = request.Description,
            TrailId = request.TrailId,
            HorseId = request.HorseId,
            StartedAt = request.StartedAt,
            CreatedAt = DateTime.UtcNow,
            PictureUrl = pictureUrl,
            Distance = distance,
            Duration = duration
            // Her kan du også lagre pictureUrl hvis ønskelig
        };

        _context.UserHikes.Add(hike);
        _context.UserHikeDetails.Add(details);

        await _context.SaveChangesAsync();

        return Ok(new { hike.Id });
    }

    [Authorize]
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateUserHike(Guid id, [FromForm] UpdateUserHikeForm request)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        var hike = await _context.UserHikes.FirstOrDefaultAsync(h => h.Id == id);
        //var hikeDetail = await _context.UserHikeDetails.FirstOrDefaultAsync(hd => hd.UserHikeId == id);
        if (hike == null) // || hikeDetail == null)
            return NotFound("Fant ikke turen.");

        // Sjekk at brukeren eier turen
        if (hike.UserId != userId)
            return Unauthorized(new { message = "Du kan ikke endre andre sine turer" });

        // Bare oppdater felter som faktisk er sendt inn
        if (!string.IsNullOrWhiteSpace(request.Title))
            hike.Title = request.Title;

        if (request.Image != null)
        {
            var fileName = await _imageUploadService.UploadImageAsync(request.Image);
            hike.PictureUrl = fileName;
        }

        if (!string.IsNullOrWhiteSpace(request.Description))
            hike.Comment = request.Description;

        if (request.HorseId.HasValue)
        {
            var horseExists = await _context.Horses.AnyAsync(h => h.Id == request.HorseId.Value);
            if (!horseExists)
                return BadRequest(new { message = "Ugyldig HorseId" });

            hike.HorseId = request.HorseId.Value;
        }

        if (request.TrailId.HasValue)
        {
            var trailExists = await _context.Trails.AnyAsync(t => t.Id == request.TrailId.Value);
            if (!trailExists)
                return BadRequest(new { message = "Ugyldig TrailId" });

            hike.TrailId = request.TrailId.Value;
        }

        await _context.SaveChangesAsync();
        return Ok(new { Message = "Turen er oppdatert." });
    }


}