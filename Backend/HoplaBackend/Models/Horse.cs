using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public partial class Horse
{
    [Key]
    public Guid Id { get; set; }

    public required string Name { get; set; }
    public Guid UserId { get; set; }
    
    [ForeignKey("UserId")]
    public required User User { get; set; }  // Navigasjonsegenskap

    public string? Breed { get; set; }

    public DateTime? Dob { get; set; }

    public DateTime? CreatedAt { get; set; }

    //public virtual ICollection<Ride> Rides { get; set; } = new List<Ride>();
}

