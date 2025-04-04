using System.ComponentModel.DataAnnotations.Schema;
using HoplaBackend.Interfaces;

namespace HoplaBackend.Models;


public class UserHike : IEntityWithUser
{
    public Guid Id { get; set; }

    public Guid UserId { get; set; }

    [ForeignKey("UserId")]    
    public User User { get; set; }
    public string? Title { get; set; }
    public double Distance { get; set; }
    public double Duration { get; set; }
    public Guid? HorseId { get; set; }

    [ForeignKey("HorseId")]
    public Horse? Horse { get; set; }

    public Guid? TrailId { get; set; }

    [ForeignKey("TrailId")]
    public Trail? Trail { get; set; }
    public string? PictureUrl  { get; set; }
    public string? Comment { get; set; }
    public bool Secret { get;  set; } = false; //publiseres ikke
    public DateTime StartedAt { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public UserHikeDetail UserHikeDetail { get; set; } = null;
}