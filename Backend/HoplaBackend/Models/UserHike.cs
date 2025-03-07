using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;

public class UserHike
{
    public Guid Id { get; set; }

    public Guid UserId { get; set; }

    [ForeignKey("UserId")]    
    public User User { get; set; }
    public double? Length { get; set; }
    public double Duration { get; set; }
    public Guid? HorseId { get; set; }

    [ForeignKey("HorseId")]
    public Horse? Horse { get; set; }

    public Guid? TrailId { get; set; }

    [ForeignKey("TrailId")]
    public Trail? Trail { get; set; }
    public string? PictureUrl  { get; set; }
    public string? Comment { get; set; }
    public bool Secret { get;  set; } = false;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}