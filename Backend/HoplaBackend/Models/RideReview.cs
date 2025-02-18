using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;
public class RideReview
{
    [Key, ForeignKey("Ride")]
    public Guid Id { get; set; } // Samme som Ride.Id

    public Ride Ride { get; set; } = null!;

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public int Rating { get; set; } // 1-5 stjerner
    public string ReviewText { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
