using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
//using HoplaBackend.Models;

namespace MyApp.Models;

public partial class Filter
{
    [Key]
    public int Id { get; set; }

    public bool? Cart { get; set; }

    public bool? Bridge { get; set; }

    public string? Difficulty { get; set; } //enum?

    public string? Traffic { get; set; }

    //public virtual Trail? Trail { get; set; }
}
