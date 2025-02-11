using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

//using HoplaBackend.Models;

namespace MyApp.Models;

public partial class Horse
{
    [Key]
    public int Id { get; set; }

    public required string Name { get; set; }
    public int UserId { get; set; }
    
    [ForeignKey("UserId")]
    public required User User { get; set; }  // Navigasjonsegenskap

    public string? Breed { get; set; }

    public int? Age { get; set; }

    public DateTime? CreatedAt { get; set; }

    //public virtual ICollection<Ride> Rides { get; set; } = new List<Ride>();
}

