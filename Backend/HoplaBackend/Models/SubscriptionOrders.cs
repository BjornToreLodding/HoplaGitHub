using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;

public class SubscriptionOrder
{
    public Guid Id { get; set; }

    public Guid UserId { get; set; }

    [ForeignKey("UserId")]    
    public User User { get; set; }
    public float Price   {get; set;}
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

}