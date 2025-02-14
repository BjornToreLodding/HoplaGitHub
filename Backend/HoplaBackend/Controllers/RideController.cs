
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

using MyApp.DTOs;
using MyApp.Models;
using HoplaBackend;
using HoplaBackend.Helpers;
using MyApp.Data;

namespace MyApp.Controllers;

[Route("api/rides")]
[ApiController]
public class RideController : ControllerBase
{
    private readonly AppDbContext _context;

    public RideController(AppDbContext context)
    {
        _context = context;
    }

    [HttpPost]
    public async Task<IActionResult> CreateRide([FromBody] RideRequestDto request)
    {
        if (request == null || request.Coordinates == null || !request.Coordinates.Any())
            return BadRequest("Ride must include at least one coordinate.");

        // Finn min, max, og mean
        var latitudes = request.Coordinates.Select(c => c.Latitude).ToList();
        var longitudes = request.Coordinates.Select(c => c.Longitude).ToList();

        double latMin = latitudes.Min();
        double latMax = latitudes.Max();
        double latMean = latitudes.Average();

        double longMin = longitudes.Min();
        double longMax = longitudes.Max();
        double longMean = longitudes.Average();

        // Reduser antall koordinater til maks 50
        //var reducedCoordinates = ReduceCoordinates(request.Coordinates);
        var reducedCoordinates = RideCoordinatesTrim.ReduceCoordinates(request.Coordinates); //Rød linje under RideCoordinatesTrim
        // Hvis over 3333 tupler, fjern ekstra koordinater og legg til tidsverdier
        if (reducedCoordinates.Count > 3333) //Rød linje under reducedCoordinates.Count
            reducedCoordinates = RideCoordinatesTrim.AdjustForTupleLimit(reducedCoordinates); //rød linje under AdjustForTupleLimit

        // Opprett Ride og RideDetails
        var ride = new Ride
        {
            //Id = Guid.NewGuid(),
            Id = 0,
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
                LongMean = longMean
            }
        };

        // Lagre Ride og RideDetails
        _context.Rides.Add(ride);
        await _context.SaveChangesAsync();

        // Lagre RideTrackDatas
        //var trackDataList = reducedCoordinates.Select(c => new RideTrackingData //rød strek under RideTrackData
        var rideTrackingData = new RideTrackingData
        {
            //Id = Guid.NewGuid(),
            Id = 0,
            Ride = ride,
            TrackingPoints = reducedCoordinates.Select(c => new TrackingPoint
            {
                Lat = c.Latitude,  // ✅ Bruk TrackingPoint sine felter
                Long = c.Longitude,
                TimeSinceLast = c.TimeSinceLast ?? 0,  // Håndterer null-verdi
            }).ToList()
        };

        // Lagre RideTrackingData (inkluderer TrackingPoints automatisk)
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
           .FirstOrDefaultAsync(r => r.Id == rideId.GetHashCode()); 
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
        */

        _context.Rides.Update(ride);
        await _context.SaveChangesAsync();

        return Ok(new { ride.Id, message = "Ride successfully updated!" });
    }
}