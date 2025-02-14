
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public class RideTrackingData
{
    [Key, ForeignKey("Ride")]
    public int Id { get; set; } // Samme som Ride.Id

    public Ride Ride { get; set; } = null!;

    public List<TrackingPoint> TrackingPoints { get; set; } = new();
}

public class TrackingPoint
{
    public int Id { get; set; }
    public int RideTrackingDataId { get; set; } // FK til RideTrackingData
    public RideTrackingData RideTrackingData { get; set; } = null!;

    public double Lat { get; set; }
    public double Long { get; set; }
    public double? TimeSinceLast { get; set; } // Tid i sekunder siden forrige punkt
}
