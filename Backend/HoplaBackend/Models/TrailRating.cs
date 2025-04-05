using HoplaBackend.Interfaces;

namespace HoplaBackend.Models;
public class TrailRating : IEntityWithUser
{
    public Guid Id { get; set; } // Unikt Id (IKKE samme som Trail.Id)

    public Guid TrailId { get; set; } // FK til Trail
    public Trail Trail { get; set; } = null!;

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public int Rating { get; set; } // 1-5 stjerner
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}