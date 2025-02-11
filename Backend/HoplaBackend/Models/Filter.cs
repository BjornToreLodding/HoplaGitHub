using System;
using System.Collections.Generic;
//using HoplaBackend.Models;

namespace MyApp.Models;

public partial class Filter
{
    public int Id { get; set; }

    public bool? Cart { get; set; }

    public bool? Bridge { get; set; }

    public string? Difficulty { get; set; } //enum?

    public string? Traffic { get; set; }

    //public virtual Trail? Trail { get; set; }
}
