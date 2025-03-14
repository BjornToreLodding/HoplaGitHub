using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;


namespace HoplaBackend.Controllers;
[Route("userreports")]
[ApiController]
public class UserReportsController : ControllerBase
{
    private readonly AppDbContext _context;

    public UserReportsController(AppDbContext context)
    {
        _context = context;
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
            
            EntityName = request.EntityName,
            Message = request.Message,
            CreatedAt = DateTime.UtcNow
        };

        _context.UserReports.Add(report);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Report created successfully." });
    }
    [HttpGet("all")]
    public async Task<IActionResult> GetReports()
    {
        var reports = await _context.UserReports
            .Include(r => r.User)  // Hvis du vil hente brukernavn og informasjon også
            .Select(r => new
            {
                name = r.User.Name,
                tabell = r.EntityName,
                message = r.Message,
                created = r.CreatedAt
            })
            .ToListAsync();

        return Ok(reports);
    }
}
