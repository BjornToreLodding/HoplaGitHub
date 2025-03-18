using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Services;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;


namespace HoplaBackend.Controllers;
[Route("userreports")]
[ApiController]
public class UserReportsController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly EntityService _entityService; // 🔹 Legg til EntityService

    // ✅ Dependency Injection av AppDbContext og EntityService
    public UserReportsController(AppDbContext context, EntityService entityService)
    {
        _context = context;
        _entityService = entityService; // 🔹 Sett EntityService i konstruktøren
    }

    // POST api/userreports/create
    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateReport([FromBody] CreateUserReportRequest request)
    {
        // Hent brukerens ID fra tokenet
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        if (request == null || string.IsNullOrEmpty(request.Message))
        {
            return BadRequest("Message cannot be empty.");
        }

        var report = new UserReport
        {
            Id = Guid.NewGuid(),
            UserId = parsedUserId,
            EntityId = request.EntityId,
            Category = request.Category,
            EntityName = request.EntityName,
            Message = request.Message
        };

        _context.UserReports.Add(report);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Report created successfully." });
    }
    [HttpGet("all")]
    public async Task<IActionResult> GetReports()
    {
        var reports = await _context.UserReports
            .Include(r => r.User)
            .ToListAsync();

        // Hent alle entityId'ene gruppert etter tabell
        // Ikke beste løsningen, men har virkelig forsøkt bedre løsning med DbFactory, men da kræsjer programmet, så går tilbake til denne her.
        var horseIds = reports.Where(r => r.EntityName == "Horses").Select(r => r.EntityId).ToList();
        var trailIds = reports.Where(r => r.EntityName == "Trails").Select(r => r.EntityId).ToList();
        var userIds = reports.Where(r => r.EntityName == "Users").Select(r => r.EntityId).ToList();

        // Hent alle navnene i én spørring per tabell
        var horseNames = await _context.Horses
            .Where(h => horseIds.Contains(h.Id))
            .ToDictionaryAsync(h => h.Id, h => h.Name);

        var trailNames = await _context.Trails
            .Where(t => trailIds.Contains(t.Id))
            .ToDictionaryAsync(t => t.Id, t => t.Name);

        var userNames = await _context.Users
            .Where(u => userIds.Contains(u.Id))
            .ToDictionaryAsync(u => u.Id, u => u.Name);

        // Bygg responsen uten ekstra databasekall
        var reportData = reports.Select(r => new
        {
            name = r.User?.Name ?? "Ukjent",
            tabell = r.EntityName,
            entityId = r.EntityId,
            reportedName = r.EntityName switch
            {
                "Horses" => horseNames.GetValueOrDefault(r.EntityId, "Ukjent"),
                "Trails" => trailNames.GetValueOrDefault(r.EntityId, "Ukjent"),
                "Users" => userNames.GetValueOrDefault(r.EntityId, "Ukjent"),
                _ => "Ukjent"
            },
            message = r.Message,
            created = r.CreatedAt
        }).ToList();

        return Ok(reportData);
    }



}

/*
using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Models;
using HoplaBackend.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;


namespace HoplaBackend.Controllers;
[Route("userreports")]
[ApiController]
public class UserReportsController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly EntityService _entityService;

    public UserReportsController(AppDbContext context, EntityService entityService)
    {
        _context = context;
        _entityService = entityService;
    }


    // POST api/userreports/create
    [HttpPost("create")]
    public async Task<IActionResult> CreateReport([FromBody] CreateUserReportRequest request)
    {
        if (request == null || string.IsNullOrEmpty(request.Message))
        {
            return BadRequest("Message cannot be empty.");
        }

        var report = new UserReport
        {
            Id = Guid.NewGuid(),
            UserId = request.UserId,
            EntityId = request.EntityId,
            Status = request.Status,
            Category = request.Category,
            EntityName = request.EntityName,
            Message = request.Message,
            CreatedAt = DateTime.UtcNow
        };

        _context.UserReports.Add(report);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Report created successfully." });
    }

    // GET api/userreports/all
 [HttpGet("all")]
public async Task<IActionResult> GetReports()
{
    var reports = await _context.UserReports
        .Include(r => r.User)
        .ToListAsync();

    var resultList = new List<object>();

    foreach (var r in reports)
    {
        resultList.Add(new
        {
            name = r.User.Name,
            tabell = r.EntityName,
            entityId = r.EntityId,
            entityName = await _entityService.GetEntityNameAsync(r.EntityName, r.EntityId), 
            message = r.Message,
            created = r.CreatedAt
        });
    }

    return Ok(resultList);
}

}

*/