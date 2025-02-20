using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using System.IO;
using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;
using HoplaBackend.Helpers;

namespace HoplaBackend.Controllers;

[Route("trails")]
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
        [FromQuery] double longitude,
        [FromQuery] bool filter = false, // Hvis "filter" er false, returner alle trails
        [FromQuery] string? difficulty = null,
        [FromQuery] bool? riverBridge = null,
        [FromQuery] bool? cart = null,
        [FromQuery] bool? roadCrossing = null,
        [FromQuery] bool? traficRoads = null,
        [FromQuery] bool? peopleTraffic = null,
        [FromQuery] double? lengthMin = null,
        [FromQuery] double? lengthMax = null)
    {
        // Start med alle trails
        var query = _context.Trails.AsQueryable();

        if (filter)
        {
            //if (!string.IsNullOrEmpty(difficulty))
            //    query = query.Where(t => t.Difficulty == difficulty);
                // Length { get; set; }
                // HasBridge { get; set; }
                // Season { get; set; } = string.Empty; // F.eks. "Summer, Winter"
                // Cart { get; set; } // Kan man bruke hest og vogn?
                // TrafficRoads { get; set; } // Går den langs bilvei?
                // PeopleTraffic { get; set; } // Mye folk?
                // Other { get; set; } = string.Empty; // Annen info
            if (cart.HasValue)
                query = query.Where(t => t.TrailFilters.Cart == cart.Value);

            //if (roadCrossing.HasValue)
            //    query = query.Where(t => t.RoadCrossing == roadCrossing.Value);

            if (traficRoads.HasValue)
                query = query.Where(t => t.TrailFilters.TrafficRoads == traficRoads.Value);

            if (peopleTraffic.HasValue)
                query = query.Where(t => t.TrailFilters.PeopleTraffic == peopleTraffic.Value);

            if (lengthMin.HasValue)
                query = query.Where(t => t.TrailFilters.Length >= lengthMin.Value);

            if (lengthMax.HasValue)
                query = query.Where(t => t.TrailFilters.Length <= lengthMax.Value);
        }

        
        // Henter trails direkte fra databasen uten referanser til Rides
        //var trails = await _context.Trails.ToListAsync();
        var trails = await query.ToListAsync(); // Bruker queryet med filtrene

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
                //if (!t.LatMean.Value || !t.LongMean.HasValue) //fungerer ikke. Nødløsning nedenfor
                if (t.LatMean == 0 || t.LongMean == 0)

                {
                    excludedCount++;
                    string errorMessage = $"Trail '{t.Name}' (ID: {t.Id}) har ugyldige koordinater.";

                    Console.WriteLine("Warning: " + errorMessage);
                    logFile.WriteLine($"{DateTime.UtcNow}: {errorMessage}");
                    continue;
                }

                validTrails.Add(new
                {
                    t.Id,
                    t.Name,
                    //t.TrailDetails.Description, 
                    Distance = DistanceCalc.SimplePytagoras(latitude, longitude, t.LatMean, t.LongMean)
                });
            }
        }

        Console.WriteLine($"Antall trails filtrert ut pga. manglende koordinater: {excludedCount}");
        Console.WriteLine($"Antall gyldige trails som sendes til frontend: {validTrails.Count}");

        var sortedTrails = validTrails.OrderBy(t => ((dynamic)t).Distance).ToList();
        return Ok(sortedTrails);
    }

    [HttpGet("list")]
    public async Task<IActionResult> CreateTrailsList(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] double? Width ,
        [FromQuery] double? Height,
        [FromQuery] int zoomlevel) //
    {
        //Width og Height er optional. Hvis de ikke er oppgitt, settes de til følgende standardverdier
        double screenPixelsWidth = Width ?? 1080;
        double screenPixelsHeight = Height ?? 2400;

        // beregne hvilke verdier skjermen har ut i fra zoomlevel. 
        var (latMin, latMax, longMin, longMax) = CoordinateCalculator.MapZoomLevel(latitude, longitude, screenPixelsWidth, screenPixelsHeight, zoomlevel);

        // Henter trails direkte fra databasen uten referanser til Rides
        //var trails = await _context.Trails.ToListAsync();

        
        Console.ForegroundColor = ConsoleColor.Blue; // Endrer tekstfarge til lilla
        Console.WriteLine($"latMin {latMin}");
        Console.WriteLine($"latMax {latMax}");
        Console.WriteLine($"longMin {longMin}");
        Console.WriteLine($"longMax {longMax}");
        Console.ResetColor();
        List<object> validTrails = new List<object>();
        
        // Finne ruter som kan tegnes inn på kartet.
        // Dette gjøres på en veldig enkel måte, men medfører at den også finner noen ruter utenfor kartet.
        // Dette er til hjelp når man skal flytte kartutsnittet.
        // Denne metoden medfører minimalt med regneoperasjoner som gir bedre responstid
        // Planen er også caching av det som allerede er tegnet opp, slik at man slipper å hente samme data igjen og igjen.
        //
        // Finne alle ruter som kan touche kartet ved å bruke latMean og longMean og t.distance / 2
        // Dette kan kun skje hvis ruta går på perfekt rett linje enten vertikalt eller horisontalt.
        // Så benyttes enkle kollisjonsdeteksjons-teknikker. 
        //først sjekke mot latitude og om løypa kan ligge innenfor kartutsnittet. 
        // Det gjør ingen ting å ta det med hvis det ligger litt utenfor. 
        // Det er bedre å tegne opp lit for mye som ligger uten for kartutsnittet enn at backend skal gjøre unødvendig mye beregninger
    
        // Filtrerer trails som er innenfor kartgrensene
        var trails = await _context.Trails
            .Where(t => t.LatMean >= latMin && t.LatMean <= latMax 
                    && t.LongMean >= longMin && t.LongMean <= longMax)
            .ToListAsync();

        return Ok(trails);   
         }

    /*[HttpGet("closest")]
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
    */


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


    // Erstattet med enklere formel.
    // Beregner avstand mellom to koordinater med Haversine-formelen. Ikke i bruk lenger.

    /*
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
    */
}
