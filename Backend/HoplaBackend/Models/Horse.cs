using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public partial class Horse
{
    [Key]
    public Guid Id { get; set; }

    public required string Name { get; set; }
    public Guid UserId { get; set; }
    
    [ForeignKey("UserId")]
    [JsonIgnore] //For å unngå at user også blir med i Json
    public User? User { get; set; }  // Navigasjonsegenskap

    public string? Breed { get; set; }
    public string? PictureUrl { get; set; }

    public DateOnly? Dob { get; set; }
    public int LikesCount { get ; set ; } = 0 ;
    public int CommentsCount { get ; set ; } = 0;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    //public virtual ICollection<Ride> Rides { get; set; } = new List<Ride>();
}

