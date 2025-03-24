
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;
public class TrailReview
{
    public Guid Id { get; set; } // Unikt Id (IKKE samme som Trail.Id)

    public Guid TrailId { get; set; } // FK til Trail
    public Trail Trail { get; set; } = null!;

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    public string Comment { get; set; } = string.Empty;
    public string PictureUrl { get; set; } = string.Empty;
    public TrailConditionType Condition { get; set; } = TrailConditionType.Ukjent;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

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