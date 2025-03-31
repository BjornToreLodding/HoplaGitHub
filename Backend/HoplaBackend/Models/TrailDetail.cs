using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;
using Microsoft.EntityFrameworkCore;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public class TrailDetail
{
    [Key, ForeignKey(nameof(Trail))]
    public Guid Id { get; set; }

    public Trail Trail { get; set; } = null!;

    public string Description { get; set; } = string.Empty;

    public double LatMin { get; set; }
    public double LatMax { get; set; }
    public double LongMin { get; set; }
    public double LongMax { get; set; }

    // CSV-basert forhåndsvisning (50 punkter)
    // for thumbnail-visning av løype på kart. 
    public string PreviewCoordinatesCsv { get; set; } = string.Empty;

    [NotMapped]
    public List<(double Lat, double Lng)> Coordinates50
    {
        get
        {
            return PreviewCoordinatesCsv
                .Split(';', StringSplitOptions.RemoveEmptyEntries)
                .Select(pair =>
                {
                    var parts = pair.Split(',');
                    return (double.Parse(parts[0]), double.Parse(parts[1]));
                }).ToList();
        }
        set
        {
            PreviewCoordinatesCsv = string.Join(";", value.Select(c => $"{c.Lat},{c.Lng}"));
        }
    }

    // Andre felt om nødvendig
    public List<EntityImage> Images { get; set; } = new();
    public string Notes { get; set; } = string.Empty;
}


/*public class TrailDetail
{
    [Key, ForeignKey("Trail")]
    public Guid Id { get; set; } // Samme som Trail.Id
    [JsonIgnore]
    public Trail Trail { get; set; } = null!;
    public string? Description { get; set; } 

    public double? LatMin { get; set; }
    public double? LongMin { get; set; }
    public double? LatMax { get; set; }
    public double? LongMax { get; set; }

    //public string? JsonCoordinates50 { get; set; } // 50 koordinater for forhåndsvisning, bør kanskje forrandres til Liste som under.
    public List<TrailCoordinate50> Coordinates50 { get; set; } = new();
    public List<EntityImage> Images { get; set; } = new();
    public string Notes { get; set; } = string.Empty;

}
[Owned]
public class TrailCoordinate50
{
public double Lat { get; set; }
public double Long { get; set; }
}

*/