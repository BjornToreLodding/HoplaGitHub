using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public class TrailDetail
{
    [Key, ForeignKey("Trail")]
    public Guid Id { get; set; } // Samme som Trail.Id

    public Trail Trail { get; set; } = null!;

    public string? PictureThumbURL { get; set; }
    public string? PictureFullURL { get; set; }
    public double? LatMin { get; set; }
    public double? LongMin { get; set; }
    public double? LatMax { get; set; }
    public double? LongMax { get; set; }

    public string? JsonCoordinates50 { get; set; } // 50 koordinater for forhåndsvisning, bør kanskje forrandres til Liste som under.
    public List<EntityImage> Images { get; set; } = new();
    public string? Description { get; set; } 
    public string Notes { get; set; } = string.Empty;

    // For å vise punkt istedenfor hele ruta ved store kartutsnitt
}
