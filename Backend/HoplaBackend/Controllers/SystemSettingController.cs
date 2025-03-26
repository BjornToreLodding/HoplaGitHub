
using HoplaBackend.Data;
using HoplaBackend.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;

[ApiController]
[Route("admin")]
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

    [HttpGet("settings/all")]
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
    [HttpGet("settings/{key}")]
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
    [HttpPut("settings/{key}")]
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

    [HttpGet("all")]
    public async Task<IActionResult> GetAll()
    {
        var filters = await _context.TrailFilterDefinitions
            .Where(f => f.IsActive)
            .OrderBy(f => f.Order)
            .ToListAsync(); // <- Nå henter vi fra databasen

        var result = filters.Select(f => new
        {
            f.Id,
            f.Name,
            f.DisplayName,
            Type = f.Type.ToString(),
            Options = string.IsNullOrEmpty(f.OptionsJson)
                ? new List<string>()
                : JsonSerializer.Deserialize<List<string>>(f.OptionsJson!)!,
            DefaultValue = f.Type == TrailFilterType.MultiEnum
                ? (object)(f.DefaultValue?.Split(',') ?? new string[0])
                : f.DefaultValue
        }).ToList();

        return Ok(result);

    }

}

public class SystemSettingDto
{
}