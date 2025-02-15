using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.Models;
using System.IO;
using HoplaBackend;

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
        //Må skrives om pga omstrukturering av database.
        var trails = await _context.Trails
            .Include(t => t.Ride)           // Hvis Ride fortsatt finnes
            .Include(t => t.Ride.RideDetails)    // Legger til RideDetail
            .ToListAsync();

        //Console.WriteLine($"Antall trails hentet fra databasen: {trails.Count}");

        List<object> validTrails = new List<object>();
        int excludedCount = 0;
        string logFilePath = "logs/trail_errors.log";
        string? logDirectory = Path.GetDirectoryName(logFilePath);

        if (!string.IsNullOrEmpty(logDirectory))
        {
            Directory.CreateDirectory(logDirectory);
        }

        using (StreamWriter logFile = new StreamWriter(logFilePath, true))
        {
            foreach (var t in trails)
            {
                if (t.Ride == null || !t.Ride.RideDetails.LatMean.HasValue || !t.Ride.RideDetails.LatMean.HasValue)
                {
                    excludedCount++;
                    string errorMessage = $"Trail '{t.Name}' (ID: {t.Id}) har ugyldige koordinater eller mangler RideData.";

                    Console.WriteLine("Warning: " + errorMessage);
                    logFile.WriteLine($"{DateTime.UtcNow}: {errorMessage}");
                    continue;
                }

                validTrails.Add(new
                {
                    t.Id,
                    t.Name,
                    t.TrailDetails.Description, 
                    Distance = DistanceCalc.SimplePytagoras(latitude, longitude, t.Ride.RideDetails.LatMean.Value, t.Ride.RideDetails.LatMean.Value)
                });
            }
        }

        Console.WriteLine($"Antall trails filtrert ut pga. manglende RideData/koordinater: {excludedCount}");
        Console.WriteLine($"Antall gyldige trails som sendes til frontend: {validTrails.Count}");

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
