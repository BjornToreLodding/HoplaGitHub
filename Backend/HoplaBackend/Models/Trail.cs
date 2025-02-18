using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public class Trail
{
    public Guid Id { get; set; }
    public string? Name { get; set; }
    public double LatMean { get; set; }
    public double LongMean { get; set; }
    //public double? Length { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public Guid RideId { get; set; } // FK til Ride (for å kopiere data)
 
    [ForeignKey("RideId")]
    public Ride Ride { get; set; } = null!;

    public TrailDetail TrailDetails { get; set; } = null!;
    public TrailAllCoordinate TrailAllCoordinates { get; set; } = null!;
    public TrailFilter TrailFilters { get; set; } = null!;
    public List<TrailReview> TrailReviews { get; set; } = new();
}
/*
public partial class Trail
{
    [Key]
    public int Id { get; set; }

    public required int RideId { get; set; }
    [ForeignKey("RideId")]
    public Ride Ride { get; set; } = null!;
    public required string Name { get; set; }
    public string? Beskrivelse { get; set; }

    public int? FilterId { get; set; }

    public DateTime? CreatedAt { get; set; } = DateTime.UtcNow;

    //public virtual Filter? Filter { get; set; }

    //public virtual TrailDetail? TrailDetail { get; set; }
}
*/