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
[Authorize(Roles = "Admin")]
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
    public async Task<IActionResult> GetReports() //[FromQuery] string status)
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
