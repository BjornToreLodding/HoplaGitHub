using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;
public class TrailAllCoordinate
{
    [Key, ForeignKey("Trail")]
    public Guid Id { get; set; }

    public Trail Trail { get; set; } = null!;

    // Lagring – bred modell som CSV-streng
    public string CoordinatesCsv { get; set; } = string.Empty;

    // Praktisk C#-tilgang
    [NotMapped]
    public List<(double Lat, double Lng)> Coordinates
    {
        get
        {
            return CoordinatesCsv
                .Split(';', StringSplitOptions.RemoveEmptyEntries)
                .Select(pair =>
                {
                    var parts = pair.Split(',');
                    return (double.Parse(parts[0]), double.Parse(parts[1]));
                })
                .ToList();
        }
        set
        {
            CoordinatesCsv = string.Join(';', value.Select(c => $"{c.Lat},{c.Lng}"));
        }
    }
}
/*
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
*/