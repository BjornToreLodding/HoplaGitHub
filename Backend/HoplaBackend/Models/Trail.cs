using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public partial class Trail
{
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
