using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public class TrailFilter
{
    [Key, ForeignKey("Trail")]
    public Guid Id { get; set; } // Samme som Trail.Id

    public Trail Trail { get; set; } = null!;

    public double Length { get; set; }
    public bool HasBridge { get; set; }
    public string Season { get; set; } = string.Empty; // F.eks. "Summer, Winter"
    public bool Cart { get; set; } // Kan man bruke hest og vogn?
    public bool TrafficRoads { get; set; } // Går den langs bilvei?
    public bool PeopleTraffic { get; set; } // Mye folk?
    public string Other { get; set; } = string.Empty; // Annen info
}

/*
public partial class TrailFilter
{
    [Key]
    public int Id { get; set; }

    public bool? Cart { get; set; }

    public bool? Bridge { get; set; }

    public string? Difficulty { get; set; } //enum?

    public string? Traffic { get; set; }

    //public virtual Trail? Trail { get; set; }
}
*/