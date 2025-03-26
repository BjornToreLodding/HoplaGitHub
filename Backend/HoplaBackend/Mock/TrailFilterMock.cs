// TrailFilterMock.cs
using HoplaBackend.Data;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models; // <-- bytt ut med riktig namespace

public static class TrailFilterMock
{
    public static async Task CreateTrailFilterValuesMock(AppDbContext context)
    {
        var trailData = new Dictionary<string, List<(string filterName, string value)>>
        {
            /*
            ("SurfaceType", "Gravel,Dirt"),     // Underlag (Gravel, Sand, Asphalt, Dirt)
            ("Difficulty", "Easy"),             // Vanskelighetsgrad (Easy, Medium, Hard)
            ("WinterAccessible", "true"),       // Åpen om vinteren
            ("HasBridge", "true"),              // Har bro over elv
            ("StrollerFriendly", "true"),       // Tilrettelagt for vogn
            ("TrafficLevel", "Lite"),           // Biltrafikk (Ingen, Lite, Middels, Mye)
            ("CrowdLevel", "Noe"),              // Folk langs veien (Sjelden, Noe, Mye)
            ("SuitableForChildren", "true"),    // Egner seg for barnevogn
            ("ForestArea", "true"),             // Skogsområde
            ("SwimmingSpot", "false"),          // Mulighet for bading
            ("Insects", "3")                    // Mengde innsekter (0–9)
            */

            ["0001"] = new()
            {
                ("HasBridge", "true"),           // Har bro over elv
                ("StrollerFriendly", "true"),    // Tilrettelagt for vogn
                ("Difficulty", "Easy"),          // Vanskelighetsgrad
                ("SurfaceType", "Gravel,Dirt")   // Underlag
            },
            ["0002"] = new()
            {
                ("HasBridge", "false"),
                ("StrollerFriendly", "false"),
                ("Difficulty", "Medium"),
                ("SurfaceType", "Sand")
            },
            ["0003"] = new()
            {
                ("HasBridge", "true"),
                ("StrollerFriendly", "false"),
                ("Difficulty", "Hard"),
                ("SurfaceType", "Asphalt")
            },
            ["0004"] = new()
            {
                ("HasBridge", "true"),
                ("StrollerFriendly", "false"),
                ("Difficulty", "Hard"),
                ("SurfaceType", "Asphalt")
            },
            ["0005"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Gravel") },
            ["0006"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Sand") },
            ["0007"] = new() { ("Difficulty", "Hard"),   ("SurfaceType", "Dirt") },
            ["0008"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Gravel,Sand") },
            ["0009"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Asphalt") },
            ["0010"] = new() { ("Difficulty", "Hard"),   ("SurfaceType", "Dirt,Sand") },
            ["0011"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Asphalt,Gravel") },
            ["0012"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Gravel") },
            ["0013"] = new() { ("Difficulty", "Hard"),   ("SurfaceType", "Sand") },
            ["0014"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Dirt") },
            ["0015"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Gravel,Asphalt") },
            ["0016"] = new() { ("Difficulty", "Hard"),   ("SurfaceType", "Sand,Asphalt") },
            ["0017"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Gravel,Dirt") },
            ["0018"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Sand") },
            ["0019"] = new() { ("Difficulty", "Hard"),   ("SurfaceType", "Asphalt,Dirt") },
            ["0020"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Gravel,Asphalt") },
            ["0021"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Dirt,Gravel") },
            ["0022"] = new() { ("Difficulty", "Hard"),   ("SurfaceType", "Gravel,Sand,Asphalt") },
            ["0023"] = new() { ("Difficulty", "Easy"),   ("SurfaceType", "Dirt") },
            ["0024"] = new() { ("Difficulty", "Medium"), ("SurfaceType", "Asphalt") },

            
        };

        foreach (var (suffix, filters) in trailData)
        {
            Console.WriteLine($"❌ Foreach1 '{suffix}' ");
            var trailId = Guid.Parse($"12345678-0000-0000-0021-12345678{suffix}");

            foreach (var (filterName, value) in filters)
            {
                var definition = await context.TrailFilterDefinitions
                    .FirstOrDefaultAsync(d => d.Name == filterName);
                Console.WriteLine($"❌ Foreach2 '{definition.Name}' ");

                if (definition == null)
                {
                    Console.WriteLine($"❌ Filterdefinisjon '{filterName}' ble ikke funnet i databasen!");
                    continue;
                }


                var exists = await context.TrailFilterValues.AnyAsync(v =>
                    v.TrailId == trailId && v.FilterDefinitionId == definition.Id);

                if (!exists)
                {
                    Console.WriteLine($"❌ Eksisterer '{trailId}' TrailId");
                    context.TrailFilterValues.Add(new TrailFilterValue
                    {
                        Id = Guid.NewGuid(),
                        TrailId = trailId,
                        FilterDefinitionId = definition.Id,
                        Value = value
                    });
                }
            }
        }

        await context.SaveChangesAsync();
    }
}