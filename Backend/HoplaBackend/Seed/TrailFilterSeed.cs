// TrailFilterSeeder.cs
using System.Text.Json;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models;
using HoplaBackend.Data; // <-- Endre til ditt namespace

public static class TrailFilterSeeder
{
    public static async Task SyncDefinitionsAsync(AppDbContext context)
    {
        var predefined = GetPredefinedDefinitions();

        foreach (var seed in predefined)
        {
            var existing = await context.TrailFilterDefinitions
                .FirstOrDefaultAsync(x => x.Name == seed.Name);

            if (existing == null)
            {
                context.TrailFilterDefinitions.Add(seed);
            }
            else
            {
                existing.DisplayName = seed.DisplayName;
                existing.DefaultValue = seed.DefaultValue;
                existing.OptionsJson = seed.OptionsJson;
                existing.Order = seed.Order;
                existing.IsActive = seed.IsActive;
            }
        }

        await context.SaveChangesAsync();
    }

    private static List<TrailFilterDefinition> GetPredefinedDefinitions()
    {
        return new List<TrailFilterDefinition>
        {
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780001"),
                Name = "SurfaceType",
                DisplayName = "Underlag",
                Type = TrailFilterType.MultiEnum,
                DefaultValue = "Gravel",
                OptionsJson = JsonSerializer.Serialize(new[] { "Gravel", "Sand", "Asphalt", "Dirt" }),
                IsActive = true,
                Order = 1
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780002"),
                Name = "Difficulty",
                DisplayName = "Vanskelighetsgrad",
                Type = TrailFilterType.Enum,
                DefaultValue = "Easy",
                OptionsJson = JsonSerializer.Serialize(new[] { "Easy", "Medium", "Hard" }),
                IsActive = true,
                Order = 2
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780003"),
                Name = "WinterAccessible",
                DisplayName = "Åpen om vinteren",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 3
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780004"),
                Name = "HasBridge",
                DisplayName = "Har bro over elv",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 4
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780005"),
                Name = "StrollerFriendly",
                DisplayName = "Tilrettelagt for vogn",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 5
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780006"),
                Name = "TrafficLevel",
                DisplayName = "Biltrafikk",
                Type = TrailFilterType.Enum,
                DefaultValue = "Lite",
                OptionsJson = JsonSerializer.Serialize(new[] { "Ingen", "Lite", "Middels", "Mye" }),
                IsActive = true,
                Order = 6
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780007"),
                Name = "CrowdLevel",
                DisplayName = "Folk langs veien",
                Type = TrailFilterType.Enum,
                DefaultValue = "Noe",
                OptionsJson = JsonSerializer.Serialize(new[] { "Sjelden", "Noe", "Mye" }),
                IsActive = true,
                Order = 7
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780008"),
                Name = "SuitableForChildren",
                DisplayName = "Egner seg for barnevogn",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 8
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780009"),
                Name = "ForestArea",
                DisplayName = "Skogsområde",
                Type = TrailFilterType.Bool,
                DefaultValue = "true",
                IsActive = true,
                Order = 9
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780010"),
                Name = "SwimmingSpot",
                DisplayName = "Mulighet for bading",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 10
            },
            new()
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780011"),
                Name = "Insects",
                DisplayName = "Mengde innsekter",
                Type = TrailFilterType.Int,
                DefaultValue = "0",
                IsActive = true,
                Order = 11
            }
        };
    }
}
