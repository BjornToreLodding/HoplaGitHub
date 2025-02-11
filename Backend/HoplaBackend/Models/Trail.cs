using System;
using System.Collections.Generic;
//using HoplaBackend.Models;

namespace MyApp.Models;

public partial class Trail
{
    public int Id { get; set; }

    public int? RideId { get; set; }

    public string? Beskrivelse { get; set; }

    public int? FilterId { get; set; }

    public DateTime? CreatedAt { get; set; }

    //public virtual Filter? Filter { get; set; }

    //public virtual TrailDetail? TrailDetail { get; set; }
}
