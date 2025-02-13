using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.Models;
using System.IO;

namespace MyApp.Controllers;

[Route("trail")]
[ApiController]
public class TrailController : ControllerBase
{
    private readonly AppDbContext _context;

    public TrailController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet("closest")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude)
    {
        var trails = await _context.Trails
            .Include(t => t.Ride)
            .ToListAsync(); 

        List<object> validTrails = new List<object>();
        int excludedCount = 0;
        string logFilePath = "logs/trail_errors.log"; // Loggfilplassering
        string? logDirectory = Path.GetDirectoryName(logFilePath);

        // Sørg for at loggmappen finnes
        if (!string.IsNullOrEmpty(logDirectory))
        {
        // Sørg for at loggmappen finnes
             Directory.CreateDirectory(logDirectory);
        }
        using (StreamWriter logFile = new StreamWriter(logFilePath, true))
        {
            foreach (var t in trails)
            {
                // Sjekk om Ride eller koordinater er null
                if (t.Ride == null || !t.Ride.LatMean.HasValue || !t.Ride.LongMean.HasValue)
                {
                    excludedCount++;
                    string errorMessage = $"Trail '{t.Name}' (ID: {t.Id}) har ugyldige koordinater eller mangler Ride-data.";

                    Console.WriteLine("Warning: " + errorMessage);
                    logFile.WriteLine($"{DateTime.UtcNow}: {errorMessage}"); // Skriv til loggfil

                    continue; // Hopper over denne trailen og går til neste
                }

                // Beregn avstand og legg til listen over gyldige trails
                validTrails.Add(new
                {
                    t.Id,
                    t.Name,
                    t.Beskrivelse,
                    Distance = GetDistance(latitude, longitude, t.Ride.LatMean.Value, t.Ride.LongMean.Value)
                });
            }
        }

        if (excludedCount > 0)
        {
            Console.WriteLine($"Warning: {excludedCount} trails ble utelatt på grunn av manglende data. Se loggfil for detaljer.");
        }

        // Returner de sorterte og gyldige trailsene
        var sortedTrails = validTrails.OrderBy(t => ((dynamic)t).Distance).ToList();
        return Ok(sortedTrails);
    }
    /*
    [HttpGet("closest")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude)
    {
        // Hent dataene fra databasen, men gjør IKKE beregninger i SQL
        var trails = await _context.Trails
            .Include(t => t.Ride)
            .Where(t => t.Ride != null && t.Ride.LatMean.HasValue && t.Ride.LongMean.HasValue) // Fjerner null-verdier
            .ToListAsync(); // Flytter dataene til minnet (C#)

        // Beregn avstand i minnet med LINQ
        var sortedTrails = trails
            .Select(t => new
            {
                t.Id,
                t.Name,
                t.Beskrivelse,
                //RideId = t.RideId,
                Distance = GetDistance(latitude, longitude, t.Ride.LatMean.Value, t.Ride.LongMean.Value) // Nå fungerer det
            })
            .OrderBy(t => t.Distance) // Sortering skjer i minnet, ikke i SQL
            .ToList();

        return Ok(sortedTrails);
    }
    */



    // Beregner avstand mellom to koordinater med Haversine-formelen
    private static double GetDistance(double lat1, double lon1, double lat2, double lon2)
    {
        var R = 6371.0; // Jordens radius i km
        var dLat = (lat2 - lat1) * Math.PI / 180.0;
        var dLon = (lon2 - lon1) * Math.PI / 180.0;
        var a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                Math.Cos(lat1 * Math.PI / 180.0) * Math.Cos(lat2 * Math.PI / 180.0) *
                Math.Sin(dLon / 2) * Math.Sin(dLon / 2);
        var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
        return R * c; // Avstand i km
    }
}
