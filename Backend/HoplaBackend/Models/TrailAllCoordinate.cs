using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;
public class TrailAllCoordinate
{
    [Key, ForeignKey("Trail")]
    public Guid Id { get; set; } // Samme som Trail.Id

    public Trail Trail { get; set; } = null!;

    public List<TrailCoordinate> Coordinates { get; set; } = new(); // Liste med koordinater
}

public class TrailCoordinate
{
    public int Id { get; set; }
    public Guid TrailAllCoordinatesId { get; set; } // FK til TrailAllCoordinates
    public TrailAllCoordinate TrailAllCoordinates { get; set; } = null!;

    public double Lat { get; set; }
    public double Long { get; set; }
}
