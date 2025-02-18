using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;

public class RideDetail
{
    [Key, ForeignKey("Ride")]
    public Guid Id { get; set; } // Samme som Ride.Id

    public Ride Ride { get; set; } = null!;
    public double? LatMean { get; set; }
    public double? LongMean { get; set; }
    public double? LatMin { get; set; }
    public double? LongMin { get; set; }
    public double? LatMax { get; set; }
    public double? LongMax { get; set; }

    public string? JsonCoordinates50 { get; set; }
    public List<EntityImage> Images { get; set; } = new(); // Henter via EntityImageId

    public string Notes { get; set; } = string.Empty;
}
