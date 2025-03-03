/*
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

using HoplaBackend.DTOs;
using HoplaBackend.Models;
using HoplaBackend;
using HoplaBackend.Helpers;
using HoplaBackend.Data;

namespace HoplaBackend.Controllers;

[Route("rides")]
[ApiController]
public class RideController : ControllerBase
{
    private readonly AppDbContext _context;

    public RideController(AppDbContext context)
    {
        _context = context;
    }
    [HttpGet("int/{userId}")] 
    public async Task<IActionResult> GetIntHorse(int userId)
    {
        //var endpointName = ControllerContext.ActionDescriptor.ActionName;
        var controllerName = ControllerContext.ActionDescriptor.ControllerName;
        Guid newGuid = CustomConvert.IntToGuid(controllerName, userId);
    
        return await GetUserRides(newGuid);
    }

    [HttpGet("user/{userId}")]
    public async Task<IActionResult> GetUserRides(Guid userId)
    {
        //Burde kanskje lage noen querys. Dette kan være ?limit og ?next.
        //Når man har bladd ned til bunnen, burde frontend spørre om neste 20 rides
        var rides = await _context.Rides
            .Where(r => r.UserId == userId)
            .OrderByDescending(r => r.CreatedAt) // Sorterer synkende på dato
            .Select(r => new
            {
                r.Id,
                r.CreatedAt,
                r.Length,
                r.Duration,
                HorseName = r.Horse != null ? r.Horse.Name : "" // Hvis ikke hest registrert, vises "" istedenfor null
            })
            .ToListAsync();

        return Ok(rides);
    }

    [HttpGet("{rideId}/details")]
    public async Task<IActionResult> GetRideDetails(Guid rideId)
    {
        var rideDetails = await _context.RideDetails
            .Where(rd => rd.Id == rideId)
            .Select(rd => new
            {
                rd.LatMin,
                rd.LatMax,
                rd.LatMean,
                rd.LongMin,
                rd.LongMax,
                rd.LongMean,
                Coordinates50 = rd.JsonCoordinates50 // JSON-streng med 50 koordinater
            })
            .FirstOrDefaultAsync();

        if (rideDetails == null)
            return NotFound("Ride details not found.");

        return Ok(rideDetails);
    }
    
    [HttpGet("{rideId}/trackingdata")]
    public async Task<IActionResult> GetRideTrackingData(Guid rideId)
    {
    /*
        var trackingData = await _context.RideTrackingDatas
            .Where(rt => rt.Id == rideId)
            .Select(rt => new
            {
                rt.Id,
              //TrackingPoints = rt.TrackingPoints.Select(tp => new
                TrackingPoints = rt.TrackingPoints.AsEnumerable().Select(tp => new
                {
                    tp.Lat,
                    tp.Long,
                    tp.TimeSinceLast
                })
            })
            .FirstOrDefaultAsync();

        if (trackingData == null)
            return NotFound("Ride tracking data not found.");

        return Ok(trackingData);
    }
    ///
        var rideTrackingData = await _context.RideTrackingDatas
            .AsNoTracking()
            .Where(rt => rt.Id == rideId)
            .FirstOrDefaultAsync();

        if (rideTrackingData == null)
            return NotFound("Ride tracking data not found.");

        // Returnerer TrackingPoints fra JSON-kolonnen
        var result = new
        {
            rideTrackingData.Id,
            TrackingPoints = rideTrackingData.TrackingPoints.Select(tp => new
            {
                tp.Lat,
                tp.Long,
                tp.TimeSinceLast
            }).ToList() // Dette vil nå fungere som en liste
        };

        return Ok(result);
    }

    [HttpPost]
    public async Task<IActionResult> CreateRide([FromBody] RideRequestDto request)
    {
        if (request == null || request.Coordinates == null || !request.Coordinates.Any())
            return BadRequest("Ride must include at least one coordinate.");

        // Henter ut verdier for min, max og gjennomsnitt for koordinatene.
        var latitudes = request.Coordinates.Select(c => c.Latitude).ToList();
        double latMin = latitudes.Min();
        double latMax = latitudes.Max();
        double latMean = latitudes.Average();
        
        var longitudes = request.Coordinates.Select(c => c.Longitude).ToList();
        double longMin = longitudes.Min();
        double longMax = longitudes.Max();
        double longMean = longitudes.Average();

        // Reduser antall koordinater til maks 50
        var reducedTo50Coordinates = RideCoordinatesTrim.ReduceTo50Coordinates(request.Coordinates); 


        //
        // Dette testes senere når en liste på en lengde over 3333 eksisterer
        //
        // Hvis over 3333 tupler, fjern ekstra koordinater og legg til tidsverdier

        var reducedCoordinates = request.Coordinates;
        if (request.Coordinates.Count > 3333) //Rød linje under reducedCoordinates.Count
            reducedCoordinates = RideCoordinatesTrim.AdjustForTupleLimit(reducedCoordinates); //rød linje under AdjustForTupleLimit
        Console.ForegroundColor = ConsoleColor.Magenta;
        Console.WriteLine("checkpoint4");
        Console.ResetColor();


        // Opprett Ride og RideDetails
        var ride = new Ride
        {
            Id = Guid.NewGuid(),
            //Id = 0,
            UserId = request.UserId,
            HorseId = request.HorseId,
            TrailId = request.TrailId,
            Duration = request.Duration,
            Length = request.Length,
            CreatedAt = DateTime.UtcNow,
            RideDetails = new RideDetail
            {
                LatMin = latMin,
                LatMax = latMax,
                LatMean = latMean,
                LongMin = longMin,
                LongMax = longMax,
                LongMean = longMean,
                //JsonCoordinates50 = reducedTo50Coordinates //Forsøkte å legge til denne, men da får jeg rød strek under
                JsonCoordinates50 = System.Text.Json.JsonSerializer.Serialize(reducedTo50Coordinates)
            }
        };
        Console.ForegroundColor = ConsoleColor.Magenta;
        Console.WriteLine("checkpoint4");
        Console.ResetColor();

        // Lagre Ride og RideDetails
        _context.Rides.Add(ride);
        await _context.SaveChangesAsync();

        // Lagre RideTrackDatas
        //var trackDataList = reducedCoordinates.Select(c => new RideTrackingData //rød strek under RideTrackData
        /*
        var rideTrackingData = new RideTrackingData
        {
            //Id = Guid.NewGuid(),
            Id = 0,
            Ride = ride,
            TrackingPoints = reducedCoordinates.Select(c => new TrackingPoint
            {
                Lat = c.Latitude,  // Bruk TrackingPoint sine felter
                Long = c.Longitude,
                TimeSinceLast = c.TimeSinceLast ?? 0,  // Håndterer null-verdi
            }).ToList()
        };

        // Lagre RideTrackingData (inkluderer TrackingPoints automatisk)
        _context.RideTrackingDatas.Add(rideTrackingData);
        await _context.SaveChangesAsync();
        return Ok(new { ride.Id, message = "Ride successfully created!" });
    ///
        var rideTrackingData = new RideTrackingData
        {
            //Id = Guid.NewGuid(),
            Id = ride.Id,
            Ride = ride,
            TrackingPoints = request.Coordinates.Select(c => new TrackingPoint
            {
                Lat = c.Latitude,
                Long = c.Longitude,
                TimeSinceLast = c.TimeSinceLast ?? 0
            }).ToList()
        };

        _context.RideTrackingDatas.Add(rideTrackingData);
        await _context.SaveChangesAsync();
        return Ok(new { ride.Id, message = "Ride successfully created!" });

    }
    

    [HttpPut("{rideId}")]
    public async Task<IActionResult> UpdateRide(Guid rideId, [FromBody] RideUpdateDto request)
    {
        var ride = await _context.Rides
            .Include(r => r.RideDetails)
            .Include(r => r.RideReviews)
            //.FirstOrDefaultAsync(r => r.Id == rideId); //Rød Strek under RideDetail Feilmelding: 'Ride' does not contain a definition for 'RideDetail' and no accessible extension method 'RideDetail' accepting a first argument of type 'Ride' could be found (are you missing a using directive or an assembly reference?)CS1061
           .FirstOrDefaultAsync(r => r.Id == rideId); 
        if (ride == null)
            return NotFound("Ride not found");

        // Oppdater felt om de er satt
        //if (!string.IsNullOrEmpty(request.Review))
        //    ride.Review = request.Review; //Rød strek under Review. Feilmelding: 'Ride' does not contain a definition for 'Review' and no accessible extension method 'Review' accepting a first argument of type 'Rid
        if (!string.IsNullOrEmpty(request.Review))
        {
            if (ride.RideReviews == null)
            {
                ride.RideReviews = new RideReview { Id = ride.Id, ReviewText = request.Review };
                _context.RideReviews.Add(ride.RideReviews);
            }
            else
            {
                ride.RideReviews.ReviewText = request.Review;
                _context.RideReviews.Update(ride.RideReviews);
            }
        }

        //
        // Det er en feil i denne delen som må rettes opp.
        //
        //if (request.Images != null && request.Images.Any())
        //    ride.Images = request.Images; // Anta at Images er en liste av URL-er 

        /*
        if (request.Images != null && request.Images.Any())
        {
            if (ride.RideDetails == null)
                return BadRequest("RideDetails må være definert for å legge til bilder.");

            // Hent eksisterende EntityImages for denne RideDetails
            var existingImages = await _context.EntityImages
                .Where(e => e.RideDetailsId == ride.RideDetails.Id)
                .ToListAsync();

            _context.EntityImages.RemoveRange(existingImages); // Slett gamle bilder

            // Opprett nye EntityImages for de nye bildene
            var newEntityImages = request.Images.Select(img => new EntityImages
            {
                RideDetailsId = ride.RideDetails.Id, // Knytter til riktig RideDetails
                ImageUrl = img  // Anta at `request.Images` er en liste med URL-er
            }).ToList();

            _context.EntityImages.AddRange(newEntityImages);
        }
        //*

        _context.Rides.Update(ride);
        await _context.SaveChangesAsync();

        return Ok(new { ride.Id, message = "Ride successfully updated!" });
    }
}

*/