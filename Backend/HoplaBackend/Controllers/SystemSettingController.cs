
using HoplaBackend.Data;
using HoplaBackend.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;


[ApiController]
[Route("admin/settings")]
public class SettingsController : ControllerBase
{
    private readonly SystemSettingService _settingService;
    private readonly AppDbContext _context;

    public SettingsController(SystemSettingService settingService, AppDbContext context)
    {
        _settingService = settingService;
        _context = context;
    }

    // Endpoint som henter alle systeminnstillinger
    [HttpGet("all")]
    public async Task<ActionResult<List<ListSettings>>> GetAllSystemSettings()
    {
        if (!await _context.SystemSettings.AnyAsync())
        {
            return NotFound(new { message = "No SystemSettings found." });
        }

        var systemSettings = await _context.SystemSettings
            .OrderBy(s => s.Key) 
            .Select(s => new ListSettings
            {
                Key = s.Key,
                Value = s.Value,
                Type = s.Type
            })
            .ToListAsync();

        return Ok(systemSettings);
    }

    // Endpoint som henter én setting
    [HttpGet("{key}")]
    public IActionResult GetSetting(string key)
    {
        try
        {
            var setting = _settingService.GetSetting(key);
            if (setting == null) return NotFound();

            return Ok(setting);
        }
        catch (Exception ex)
        {
            return BadRequest(ex.Message);
        }
    }
    
    [HttpPost("refresh-cache")]
    public async Task<IActionResult> RefreshSettingsCache()
    {
        await _settingService.RefreshCacheAsync();
        return Ok(new { message = "SystemSettings cache refreshed." });
    }

    // Endpoint som oppdaterer en setting
    [HttpPut("{key}")]
    public async Task<IActionResult> UpdateSetting(string key, [FromBody] UpdateSettingRequest request)
    {
        try
        {
            if (request == null || string.IsNullOrEmpty(request.Value))
                return BadRequest("Verdien kan ikke være tom.");

            _settingService.UpdateSetting(key, request.Value);

            // 🚀 Refresh cachen ETTER oppdatering
            await _settingService.RefreshCacheAsync();

            return Ok(new { message = "Innstilling oppdatert og cache refreshed", key, value = request.Value });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}
