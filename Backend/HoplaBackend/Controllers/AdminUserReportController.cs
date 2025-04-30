using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Services;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;


namespace HoplaBackend.Controllers;
[Route("admin/userreports")]
//[Authorize(Roles = "Admin")]
[ApiController]
public class AdminUserReportsController : ControllerBase
{
    private readonly AppDbContext _context;
    
    public 
    AdminUserReportsController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet("reports")]
    public async Task<IActionResult> GetReports([FromQuery] string status)
    {
        Console.WriteLine("reports endpoint");
        // Del opp statusene (hvis flere) og fjern tomme elementer
        var statusList = string.IsNullOrWhiteSpace(status)
            ? new List<string>()
            : status.Split(',', StringSplitOptions.RemoveEmptyEntries)
                    .Select(s => s.Trim().ToLower())
                    .ToList();

        // Hent rapporter med valgte statuser, eller alle hvis ingen status er valgt
        var reportsQuery = _context.UserReports
            .Include(r => r.User)
            .AsQueryable();

        if (statusList.Any())
        {
            reportsQuery = reportsQuery.Where(r => statusList.Contains(r.Status.ToLower()));
        }

        var reports = await reportsQuery.ToListAsync();

        // Hent alle entityId'ene gruppert etter tabell
        var horseIds = reports.Where(r => r.EntityName == "Horses").Select(r => r.EntityId).ToList();
        var trailIds = reports.Where(r => r.EntityName == "Trails").Select(r => r.EntityId).ToList();
        var userIds = reports.Where(r => r.EntityName == "Users").Select(r => r.EntityId).ToList();

        // Hent navn fra relaterte tabeller
        var horseNames = await _context.Horses
            .Where(h => horseIds.Contains(h.Id))
            .ToDictionaryAsync(h => h.Id, h => h.Name);

        var trailNames = await _context.Trails
            .Where(t => trailIds.Contains(t.Id))
            .ToDictionaryAsync(t => t.Id, t => t.Name);

        var userNames = await _context.Users
            .Where(u => userIds.Contains(u.Id))
            .ToDictionaryAsync(u => u.Id, u => u.Name);

        // Bygg respons
        var reportData = reports.Select(r => new
        {
            id = r.Id,
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
            feedback = r.FeedBack,
            created = r.CreatedAt,
            status = r.Status
        }).ToList();

        return Ok(reportData);
    }

    // PUT: /admin/userreports/{id}
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateReportStatus(Guid id, [FromBody] ReportUpdateDto dto)
    {
        Console.WriteLine("userreports put");
        Console.WriteLine(dto.Status);
        Console.WriteLine(dto.Feedback);
        var report = await _context.UserReports.FindAsync(id);
        if (report == null)
        {
            return NotFound(new { message = "Rapport ikke funnet." });
        }

        report.Status = dto.Status;
        report.FeedBack = dto.Feedback;
        if (dto.Status == "Resolved") {
            report.Resolved = DateTime.UtcNow;
        }
        if (dto.Status == "inProgress") {
            report.InProgress = DateTime.UtcNow;
        }
        
        await _context.SaveChangesAsync();

        return Ok(new { message = "Rapport oppdatert." });
    }
}



