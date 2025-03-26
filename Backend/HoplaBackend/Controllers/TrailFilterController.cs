// Controller: TrailFilterController.cs
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models;
using HoplaBackend.Data;
using System.Text.Json; // <-- Endre til ditt prosjekt

[ApiController]
[Route("trailfilters")]
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


    [HttpPost("{trailId}")]
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

    [HttpPost("admin/trailfilters/definitions/create")]
public async Task<IActionResult> CreateTrailFilterDefinition([FromBody] TrailFilterDefinitionDto dto) 
{
    var existingCount = await _context.TrailFilterDefinitions.CountAsync();

    if (!Enum.TryParse<TrailFilterType>(dto.Type, true, out var parsedType))
    {
        return BadRequest($"Ugyldig Type-verdi: {dto.Type}");
    }

    // --- Rens og valider alternativer ---
    string? optionsJson = null;
    List<string>? trimmedAlternatives = null;

    if (parsedType == TrailFilterType.Enum || parsedType == TrailFilterType.MultiEnum)
    {
        trimmedAlternatives = dto.Alternatives?
            .Select(a => a?.Trim())
            .Where(a => !string.IsNullOrWhiteSpace(a))
            .Distinct()
            .ToList();

        if (trimmedAlternatives == null || trimmedAlternatives.Count < 2)
        {
            return BadRequest("Alternativer må inneholde minst to unike verdier.");
        }

        optionsJson = System.Text.Json.JsonSerializer.Serialize(trimmedAlternatives);
    }

    // --- Defaultverdi-håndtering ---
    string? defaultValue = null;

    if (dto.DefaultValue != null)
    {
        if (parsedType == TrailFilterType.MultiEnum && dto.DefaultValue is JsonElement json && json.ValueKind == JsonValueKind.Array)
        {
            var values = json.EnumerateArray()
                             .Select(x => x.GetString()?.Trim())
                             .Where(x => !string.IsNullOrWhiteSpace(x));
            defaultValue = string.Join(",", values).Trim();
        }
        else
        {
            defaultValue = dto.DefaultValue.ToString()?.Trim();
        }
    }

    var definition = new TrailFilterDefinition
    {
        Name = $"Custom{existingCount + 1}",
        DisplayName = dto.DisplayName,
        Type = parsedType,
        OptionsJson = optionsJson,
        DefaultValue = defaultValue,
        IsActive = dto.IsActive,
        Order = existingCount + 1
    };

    _context.TrailFilterDefinitions.Add(definition);
    await _context.SaveChangesAsync();

    return Ok(definition);
}


    public class TrailFilterInput
    {
        public Guid FilterDefinitionId { get; set; }
        public string Value { get; set; } = string.Empty;
    }
}
