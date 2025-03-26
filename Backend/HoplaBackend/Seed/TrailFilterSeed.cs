using HoplaBackend.Data;
using Microsoft.EntityFrameworkCore;

public static class TrailFilterSeeder
{
    public static async Task SeedAsync(AppDbContext context)
    {
        if (await context.TrailFilterDefinitions.AnyAsync()) return;

        var filters = new List<TrailFilterDefinition>
        {
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780001"),
                Name = "SurfaceType1",
                DisplayName = "Underlag",
                Type = TrailFilterType.MultiEnum,
                DefaultValue = "Gravel",
                OptionsJson = "[\"Gravel\",\"Sand\",\"Asphalt\",\"Dirt\"]",
                IsActive = true,
                Order = 1
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780002"),
                Name = "Difficulty1",
                DisplayName = "Vanskelighetsgrad",
                Type = TrailFilterType.Enum,
                DefaultValue = "Easy",
                OptionsJson = "[\"Easy\",\"Medium\",\"Hard\"]",
                IsActive = true,
                Order = 2
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780003"),
                Name = "SurfaceType2",
                DisplayName = "Underlag2",
                Type = TrailFilterType.MultiEnum,
                DefaultValue = "Gravel",
                OptionsJson = "[\"Gravel\",\"Sand\",\"Asphalt\",\"Dirt\"]",
                IsActive = true,
                Order = 3
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780004"),
                Name = "Difficulty2",
                DisplayName = "Vanskelighetsgrad2",
                Type = TrailFilterType.Enum,
                DefaultValue = "Easy",
                OptionsJson = "[\"Easy\",\"Medium\",\"Hard\"]",
                IsActive = true,
                Order = 4
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780005"),
                Name = "WinterAccessible",
                DisplayName = "Åpen om vinteren",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 5
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780006"),
                Name = "HasBridge",
                DisplayName = "Har bro over elv",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 6
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780007"),
                Name = "StrollerFriendly",
                DisplayName = "Tilrettelagt for vogn",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 7
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780008"),
                Name = "TrafficLevel",
                DisplayName = "Biltrafikk",
                Type = TrailFilterType.Enum,
                DefaultValue = "Lite",
                OptionsJson = "[\"Ingen\",\"Lite\",\"Middels\",\"Mye\"]",
                IsActive = true,
                Order = 8
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780009"),
                Name = "CrowdLevel",
                DisplayName = "Folk langs veien",
                Type = TrailFilterType.Enum,
                DefaultValue = "Noe",
                OptionsJson = "[\"Sjelden\",\"Noe\",\"Mye\"]",
                IsActive = true,
                Order = 9
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780010"),
                Name = "SuitableForChildren",
                DisplayName = "Egner seg for barnevogn",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 10
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780011"),
                Name = "ForestArea",
                DisplayName = "Skogsområde",
                Type = TrailFilterType.Bool,
                DefaultValue = "true",
                IsActive = true,
                Order = 11
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780012"),
                Name = "SwimmingSpot",
                DisplayName = "Mulighet for bading",
                Type = TrailFilterType.Bool,
                DefaultValue = "false",
                IsActive = true,
                Order = 12
            },
            new TrailFilterDefinition
            {
                Id = Guid.Parse("12345678-0000-0000-0101-123456780013"),
                Name = "Insects",
                DisplayName = "Mengde innsekter",
                Type = TrailFilterType.Int,
                DefaultValue = "0",
                IsActive = true,
                Order = 13
            }
        };

        context.TrailFilterDefinitions.AddRange(filters);
        await context.SaveChangesAsync();
    }
}
