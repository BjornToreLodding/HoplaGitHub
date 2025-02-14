using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public class TrailDetail
{
    [Key, ForeignKey("Trail")]
    public int Id { get; set; } // Samme som Trail.Id

    public Trail Trail { get; set; } = null!;

    public string? JsonCoordinates50 { get; set; } // 50 koordinater for forhåndsvisning
    public List<EntityImage> Images { get; set; } = new();
    public string? Description { get; set; } 
    public string Notes { get; set; } = string.Empty;

    // For å vise punkt istedenfor hele ruta ved store kartutsnitt
}
