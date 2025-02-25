
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
    // Endpoint som returnerer alt som registrerer alle registrerte data i settingstabellen.

    [HttpGet("all")]
    public async Task<ActionResult<List<ListSettings>>> GetAllSystemSettings()
    {
        if (!await _context.SystemSettings.AnyAsync())
        {
            return NotFound(new { message = "No SystemSettings found." });
        }

        var systemSettings = await _context.SystemSettings
            //.OrderBy(s => s.Id) //For sortering på Id
            .OrderBy(s => s.Key) //For sortering på Key
            .Select(s => new ListSettings
            {
                Key = s.Key,
                Value = s.Value,
                Type = s.Type
            })
            .ToListAsync();

        return Ok(systemSettings);
    }
    

    // Endpoint som sjekker verdien på en innstilling. Brukes til opplasting av adminportalen.
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

    // Endpoint som endrer en lagret innstilling, hvis dette skal endres i adminportalen.
    [HttpPut("{key}")]
    public IActionResult UpdateSetting(string key, [FromBody] UpdateSettingRequest request)
    {
        try
        {
            if (request == null || string.IsNullOrEmpty(request.Value))
                return BadRequest("Verdien kan ikke være tom.");

            _settingService.UpdateSetting(key, request.Value);
            return Ok(new { message = "Innstilling oppdatert", key, value = request.Value });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }


}

public class SystemSettingDto
{
}