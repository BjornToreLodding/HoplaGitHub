using Microsoft.AspNetCore.Mvc;
using HoplaBackend.DTOs;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using HoplaBackend.Helpers;
using System.Text.RegularExpressions;

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
    [Authorize]
    [HttpGet("all")] 
    public async Task<IActionResult> GetAllStables([FromQuery] string? search, double latitude, double longitude, int pageSize = 10, int pageNumber = 1)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        if (latitude == 0 || longitude == 0)
            return BadRequest(new {message = "latitude og longitude må oppgis i querystring"});
        // Trinn 1: Hent stables fra databasen (kun det nødvendige først)
        var stables = await _context.Stables
            .Where(s => string.IsNullOrEmpty(search) || s.Name.Contains(search))
            .ToListAsync(); // Henter alle stables som matcher søket

        // Trinn 2: Gå over til minnet og beregn avstand
        
        var result = stables
            .Select(s => new
            {
                StableId = s.Id,
                StableName = s.Name,
                Distance = DistanceCalc.SimplePytagoras(latitude, longitude, s.Latitude, s.Longitude),
                Member = _context.StableUsers.Any(su => su.StableId == s.Id && su.UserId == parsedUserId),
                PictureUrl = FixPictureUrl(s.PictureUrl)
            })
            .OrderBy(s => s.Distance)
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToList();

        return Ok(result);
    }
   private string FixPictureUrl(string? originalUrl)
{
    if (string.IsNullOrWhiteSpace(originalUrl))
        return "https://hopla.imgix.net/default.jpg";

    try
    {
        var filename = Path.GetFileNameWithoutExtension(originalUrl);
        var extension = Path.GetExtension(originalUrl);

        // Finn de 4 siste sifrene (kan være f.eks. 0086)
        var match = Regex.Match(filename, @"(\d{4})(?=\.|$)");
        if (!match.Success) return "https://hopla.imgix.net/" + originalUrl + "?h=140&w394&crop";;

        var originalNumberStr = match.Groups[1].Value;
        if (!int.TryParse(originalNumberStr, out int originalNumber))
            return "https://hopla.imgix.net/" + originalUrl + "?h=140&w394&crop";

        // Regn ut nytt tall basert på kun 50 tilgjengelige bilder
        int reduced = (originalNumber-1) % 50 + 1;
        int finalNumber = reduced ;/// 10 * 10 + 1; // alltid ende på 1

        string newNumberStr = finalNumber.ToString("D4");

        // Bytt ut tallet i filnavnet
        string newFilename = Regex.Replace(filename, @"\d{4}(?=\.|$)", newNumberStr);

        return $"https://hopla.imgix.net/{newFilename}{extension}?h=140&w394&crop";
    }
    catch
    {
        return "https://hopla.imgix.net/default.jpg";
    }
}



    [Authorize]
    [HttpGet("{stableId}")] 
    public async Task<IActionResult> GetStable(Guid stableId) 
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        var stable = await _context.Stables
        .Include(s => s.StableUsers)
        .Where(s => s.Id == stableId)
        .Select(s => new
        {
            s.Id,
            s.Name,
            s.Description,
            s.PictureUrl,
            IsMember = s.StableUsers.Any(su => su.UserId == parsedUserId)
        })
        .FirstOrDefaultAsync();

    if (stable == null)
    {
        return NotFound();
    }

    return Ok(stable);
}

}
