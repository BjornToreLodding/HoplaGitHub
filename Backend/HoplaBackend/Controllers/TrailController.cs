using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.DTOs;
using System.IO;
using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;
using HoplaBackend.Helpers;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using System.Text.Json;

namespace HoplaBackend.Controllers;

[Route("trails")]
[ApiController]
public class TrailController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;

    public TrailController(Authentication authentication, AppDbContext context)
    {
        _authentication = authentication;
        _context = context;
    }

    [Authorize]
    [HttpGet("user")]
    public async Task<ActionResult<List<TrailDto>>> GetUserTrails(
        [FromQuery] Guid? userId, 
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        // Må skrives om når denne blir Authorized.
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }         
        if (!userId.HasValue)
        {
            userId = parsedUserId;
        }
        Console.WriteLine(userId);
        bool paging = pageNumber.HasValue && pageSize.HasValue;
        if (paging && (pageNumber < 1 || pageSize < 1))
            return BadRequest("pageNumber og pageSize må være større enn 0.");

        var trails = _context.Trails
            .Where(t => t.UserId == userId)
            .OrderByDescending(t => t.CreatedAt) // Sorterer etter nyeste innlegg
            .Select(t => new TrailDto
            {
                Id = t.Id,
                Name = t.Name,
                PictureUrl = t.PictureUrl,
                AverageRating = _context.TrailRatings
                    .Where(tr => tr.TrailId == t.Id)
                    .Select(tr => (float?)tr.Rating) // Konverterer til nullable float
                    .Average() ?? 0 // Setter 0 hvis ingen ratings finnes
            });
        if (paging)
        {
            trails = trails
                .Skip((pageNumber.Value - 1) * pageSize.Value)
                .Take(pageSize.Value);
        }
        var results = await trails.ToListAsync();

        return Ok(results);
    }

    [Authorize]
    [HttpGet("all")]
    public async Task<IActionResult> GetAllTrails(
        [FromQuery] string sort, 
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10)
    {
        int page = pageNumber ?? 1;
        int size = pageSize ?? 10;
        var trails = await _context.Trails
            .OrderByDescending(t => Math.Round(t.AverageRating ?? 0)) // Først etter avrundet rating
            .ThenByDescending(t => t.CreatedAt) // Deretter etter CreatedAt for likeverdige ratings
            .Skip((page - 1) * size)
            .Take(size)
            .Select(t => new TrailDto
            {
                Id = t.Id,
                Name = t.Name,
                PictureUrl = t.PictureUrl,
                AverageRating = t.AverageRating ?? 0
                //lag riktig DTO for dette
            })
            .ToListAsync();
            return Ok(trails);
    }



    [Authorize]
    [HttpGet("list")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] int? pageNumber = 1, 
        [FromQuery] int? pageSize = 10,
        [FromQuery] string? filters = null, // JSON-baserte filtre
        [FromQuery] double? lengthMin = null,
        [FromQuery] double? lengthMax = null) 
    {
        var query = _context.Trails.AsQueryable();

        // Hent JSON-filtre fra querystring
        if (!string.IsNullOrEmpty(filters))
        {
            try
            {
                var filterDict = JsonSerializer.Deserialize<Dictionary<string, object>>(filters);
                foreach (var filter in filterDict)
                {
                    query = query.ApplyDynamicFilter(filter.Key, filter.Value);
                }
            }
            catch (JsonException)
            {
                return BadRequest("Ugyldig JSON-format for 'filters'.");
            }
        }

        // **Filtrer ut ugyldige koordinater**
        query = query.Where(t => t.LatMean != 0 && t.LongMean != 0);

        // Beregn distanse før ekskludering av trails
        var filteredTrails = await query
            .Where(t => (!lengthMin.HasValue || t.Distance >= lengthMin) &&  // Min lengde-filter
                        (!lengthMax.HasValue || t.Distance  <= lengthMax))    // Maks lengde-filter
            .ApplyPagination(pageNumber, pageSize) // Paginering skjer HER
            .Select(t => new
            {
                t.Id,
                t.Name,
                Distance = DistanceCalc.SimplePytagoras(latitude, longitude, t.LatMean, t.LongMean),
                })
            .OrderBy(t => t.Distance) // Sortering skjer etter Select()
            .ToListAsync();


        return Ok(filteredTrails);
    }

    /*
    [HttpGet("list")]
    public async Task<IActionResult> GetClosestTrails(
        [FromQuery] double latitude, 
        [FromQuery] double longitude,
        [FromQuery] int? pageNumber, 
        [FromQuery] int? pageSize,
        [FromQuery] bool filter = false, // Hvis "filter" er false, returner alle trails
        //Omskriving av filter
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
*()
    [HttpGet("map")]
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
