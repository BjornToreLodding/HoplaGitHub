using HoplaBackend.Data;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;


namespace HoplaBackend.Controllers;

//[Authorize(Roles = "Admin")]
[ApiController]
[Route("admin/stats")]
public class AdminStatsController : ControllerBase
{
    private readonly AppDbContext _context;

    public AdminStatsController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet("newusersbymonth")]
    public async Task<IActionResult> GetNewUsersByMonth()
    {
        var result = await _context.Users
            .GroupBy(u => new { u.CreatedAt.Year, u.CreatedAt.Month })
            .Select(g => new
            {
                Year = g.Key.Year,
                Month = g.Key.Month,
                Count = g.Count()
            })
            .OrderBy(g => g.Year)
            .ThenBy(g => g.Month)
            .ToListAsync();

        return Ok(result);
    
    }
    
}
