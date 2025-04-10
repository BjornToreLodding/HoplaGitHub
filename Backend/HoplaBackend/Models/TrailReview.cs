
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using HoplaBackend.Interfaces;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;
public class TrailReview : IEntityWithUser
{
    public Guid Id { get; set; } // Unique ID (NOT the same as Trail.Id)

    public Guid TrailId { get; set; } // Foreign key to Trail
    public Trail Trail { get; set; } = null!;

    public Guid UserId { get; set; } // Foreign key to User
    public User User { get; set; } = null!;

    public string Comment { get; set; } = string.Empty; // The review text

    public string? PictureUrl { get; set; } // Optional picture

    public TrailConditionType Condition { get; set; } = TrailConditionType.Ukjent; // Default to unknown

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow; // Set automatically when created
}


public enum TrailConditionType
{
    Ukjent = 0,
    Bra = 1, 
    Vått = 2,
    Møkkette = 3,
    Farlig = 4, 
    Blokkert = 5
}