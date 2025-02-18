
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public class RideTrackingData
{
    [Key, ForeignKey("Ride")]
    public Guid Id { get; set; } // Samme som Ride.Id

    public Ride Ride { get; set; } = null!;

    [Column(TypeName = "json")]  // Bruker JSON i stedet for JSONB
    public List<TrackingPoint> TrackingPoints { get; set; } = new();
}

public class TrackingPoint
{
    public int Id { get; set; }
    public Guid RideTrackingDataId { get; set; } // FK til RideTrackingData
    public RideTrackingData RideTrackingData { get; set; } = null!;

    public double Lat { get; set; }
    public double Long { get; set; }
    public double? TimeSinceLast { get; set; } // Tid i sekunder siden forrige punkt
}
