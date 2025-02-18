
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;
public class TrailReview
{
    public Guid Id { get; set; } // Unikt Id (IKKE samme som Trail.Id)

    public Guid TrailId { get; set; } // FK til Trail
    public Trail Trail { get; set; } = null!;

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public int Rating { get; set; } // 1-5 stjerner
    public string ReviewText { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
