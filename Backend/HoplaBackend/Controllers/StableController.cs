using Microsoft.AspNetCore.Mvc;
using HoplaBackend.DTOs;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using HoplaBackend.Helpers;
using System.Text.RegularExpressions;
using HoplaBackend.Services;
using Org.BouncyCastle.Bcpg;

namespace HoplaBackend.Controllers;

[Route("stables")]
[ApiController]
public class StableController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly ImageUploadService _imageUploadService;
    private readonly Authentication _authentication;

    public StableController(AppDbContext context, ImageUploadService imageUploadService, Authentication authentication)
    {
        _context = context;
        _imageUploadService = imageUploadService;
        _authentication = authentication;
    }

    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateStable([FromForm] CreateStableForm request)
    {
        if (request == null || string.IsNullOrEmpty(request.Name))
            return BadRequest("Stable cannot be empty when creating it.");

        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });

        var user = await _context.Users.FindAsync(parsedUserId);
        if (user == null)
            return NotFound(new { message = "Brukeren ble ikke funnet." });

        // Last opp bildet (hvis det finnes)
        string? pictureUrl = null;
        if (request.Image != null)
        {
            pictureUrl = await _imageUploadService.UploadImageAsync(request.Image);
        }

        var stable = new Stable
        {
            Name = request.Name,
            Description = request.Description,
            PictureUrl = pictureUrl,
            Latitude = request.Latitude,
            Longitude = request.Longitude,
            PrivateGroup = request.PrivateGroup
        };

        _context.Stables.Add(stable);
        await _context.SaveChangesAsync();

        var stableUser = new StableUser
        {
            UserId = parsedUserId,
            StableId = stable.Id,
            IsOwner = true,
            IsAdmin = true,
            User = user,
            Stable = stable
        };

        _context.StableUsers.Add(stableUser);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Stable created successfully.", StableId = stable.Id });
    }


    /* Gammelt Backup før bildeopplasting
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
    */

    [Authorize]
    [HttpGet("all")] 
    public async Task<IActionResult> GetAllStables([FromQuery] string? search, Guid? userId, double latitude, double longitude, int pageSize = 10, int pageNumber = 1)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        if (latitude == 0 || longitude == 0)
            return BadRequest(new {message = "latitude og longitude må oppgis i querystring"});
        
        // Trinn 1: Hent stables fra databasen (kun det nødvendige først og hvis)
        var stablesQuery = _context.Stables
            .Where(s => string.IsNullOrEmpty(search) || s.Name.Contains(search));

        // Filtrer på medlemskap hvis userId er oppgitt
        if (userId.HasValue)
        {
            stablesQuery = stablesQuery
                .Where(s => _context.StableUsers.Any(su => su.StableId == s.Id && su.UserId == userId.Value));
        }

        var stables = await stablesQuery.ToListAsync();

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

    //Midlertidig fiks da det kun er bilder for stall 1-50. For stall 51-899 så brukes bilde 1-50
    //0071 får 0021
    //0235 får 0035
    //0888 får 0038
    // altså modlo NNNN % 50, men med noen justeringer.
    private static string FixPictureUrl(string? originalUrl)
    {
        if (string.IsNullOrWhiteSpace(originalUrl))
            return "https://hopla.imgix.net/default.jpg";

        try
        {
            var filename = Path.GetFileNameWithoutExtension(originalUrl);
            var extension = Path.GetExtension(originalUrl);
    
            // Hvis filnavnet inneholder a-f, behold som det er
            if (Regex.IsMatch(filename, @"[a-f]", RegexOptions.IgnoreCase))
            return $"https://hopla.imgix.net/{originalUrl}?h=140&w394&crop";

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
                PictureUrl = FixPictureUrl(s.PictureUrl),
                IsMember = s.StableUsers.Any(su => su.UserId == parsedUserId)
            })
            .FirstOrDefaultAsync();

        if (stable == null)
        {
            return NotFound();
        }

        return Ok(stable);
    }
    [Authorize]
    [HttpPost("join")]
    public async Task<IActionResult> JoinStable([FromBody] AddStableUserDto dto)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        // Sjekk at stallen finnes
        var stableExists = await _context.Stables.AnyAsync(s => s.Id == dto.StableId);
        if (!stableExists)
            return NotFound(new { message = "Stall ikke funnet" });

        // Sjekk om bruker allerede er medlem
        var existing = await _context.StableUsers.FirstOrDefaultAsync(su =>
            su.UserId == userId && su.StableId == dto.StableId);

        if (existing != null)
            return BadRequest(new { message = "Du er allerede medlem av denne stallen" });

        // Legg til medlemskap
        var stableUser = new StableUser
        {
            UserId = userId,
            StableId = dto.StableId
        };

        _context.StableUsers.Add(stableUser);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Du er nå medlem av stallen" });
    }

    [Authorize]
    [HttpDelete("leave")]
    public async Task<IActionResult> LeaveStable([FromBody] AddStableUserDto dto)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        // Sjekk at stallen finnes
        var stableExists = await _context.Stables.AnyAsync(s => s.Id == dto.StableId);
        if (!stableExists)
            return NotFound(new { message = "Stall ikke funnet" });

        // Finn medlemskapet
        var stableUser = await _context.StableUsers.FirstOrDefaultAsync(su =>
            su.UserId == userId && su.StableId == dto.StableId);

        if (stableUser == null)
            return NotFound(new { message = "Du er ikke medlem av denne stallen" });

        _context.StableUsers.Remove(stableUser);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Du har forlatt stallen" });
    }


}
