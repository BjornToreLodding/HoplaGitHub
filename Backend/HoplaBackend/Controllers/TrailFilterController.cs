// Controller: TrailFilterController.cs
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models;
using HoplaBackend.Data;
using System.Text.Json; // <-- Endre til ditt prosjekt

[ApiController]
[Route("admin/trailfilters")]
public class TrailFilterController : ControllerBase
{
    private readonly AppDbContext _context;

    public TrailFilterController(AppDbContext context)
    {
        _context = context;
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
    
    [HttpGet("{trailId}")]
    public async Task<IActionResult> GetTrailFilters(Guid trailId)
    {
        var definitions = await _context.TrailFilterDefinitions
            .Where(d => d.IsActive)
            .OrderBy(d => d.Order)
            .ToListAsync();

        var values = await _context.TrailFilterValues
            .Where(v => v.TrailId == trailId)
            .ToListAsync();

        var result = definitions.Select(def =>
        {
            var val = values.FirstOrDefault(v => v.FilterDefinitionId == def.Id);

            return new
            {
                def.Id,
                def.Name,
                def.DisplayName,
                Type = def.Type.ToString(),
                Options = string.IsNullOrEmpty(def.OptionsJson) ? new List<string>() : System.Text.Json.JsonSerializer.Deserialize<List<string>>(def.OptionsJson!),
                Value = val?.Value,
                DefaultValue = def.DefaultValue
            };
        });

        return Ok(result);
    }


    [HttpPost("{trailId}/filters")]
    public async Task<IActionResult> SetTrailFilters(Guid trailId, [FromBody] List<TrailFilterInput> inputs)
    {
        var existingValues = await _context.TrailFilterValues
            .Where(v => v.TrailId == trailId)
            .ToListAsync();

        foreach (var input in inputs)
        {
            var existing = existingValues.FirstOrDefault(v => v.FilterDefinitionId == input.FilterDefinitionId);

            if (existing != null)
            {
                existing.Value = input.Value;
            }
            else
            {
                _context.TrailFilterValues.Add(new TrailFilterValue
                {
                    Id = Guid.NewGuid(),
                    TrailId = trailId,
                    FilterDefinitionId = input.FilterDefinitionId,
                    Value = input.Value
                });
            }
        }

        await _context.SaveChangesAsync();
        return Ok();
    }

    public class TrailFilterInput
    {
        public Guid FilterDefinitionId { get; set; }
        public string Value { get; set; } = string.Empty;
    }
}
