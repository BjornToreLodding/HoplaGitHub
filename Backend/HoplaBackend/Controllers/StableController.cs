using Microsoft.AspNetCore.Mvc;
using HoplaBackend.DTOs;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace HoplaBackend.Controllers;

[Route("stables")]
[ApiController]
public class StableController : ControllerBase
{
    private readonly AppDbContext _context;

    public StableController(AppDbContext context)
    {
        _context = context;
    }

    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateStable([FromBody] CreateStableRequest request)
    {
        if (request == null || string.IsNullOrEmpty(request.Name))
        {
            return BadRequest("Stable cannot be empty when creating it.");
        }

        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        // Sjekk om brukeren finnes i databasen
        var user = await _context.Users.FindAsync(parsedUserId);
        if (user == null)
        {
            return NotFound(new { message = "Brukeren ble ikke funnet." });
        }

        var stable = new Stable
        {
            Name = request.Name,
            Description = request.Description,
            PictureUrl = request.PictureUrl,
            Latitude = request.Latitude,
            Longitude = request.Longitude,
            PrivateGroup = request.PrivateGroup
        };

        _context.Stables.Add(stable);
        await _context.SaveChangesAsync(); // Nå har stable.Id en verdi

        var stableUser = new StableUser
        {
            UserId = parsedUserId, // Bruker riktig bruker-ID
            StableId = stable.Id,  // Bruker stable.Id etter lagring
            IsOwner = true,
            IsAdmin = true,
            User = user,           // Knytter riktig User-objekt
            Stable = stable        // Knytter riktig Stable-objekt
        };

        _context.StableUsers.Add(stableUser);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Stable created successfully.", StableId = stable.Id });
    }

}

/*
    [HttpGet("{stableId}")] // Returnerer meldinger mellom to brukere eller siste melding per bruker
    public async Task<IActionResult> GetMessagesBetweenUsers(
        Guid stableId,
        [FromQuery] Guid? userid) // id er optional, men hvis spesifisert så returneres alle meldinger som user har sendt til stableId
    {
    }
    */
