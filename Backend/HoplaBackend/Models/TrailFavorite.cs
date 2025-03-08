using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.Extensions.Configuration.UserSecrets;

namespace HoplaBackend.Models;

public class TrailFavorite
{
    public Guid Id { get; set; }
    public Guid UserId { get; set; }
    [ForeignKey("UserId")]    
    public User User { get; set; }
    public Guid TrailId { get; set; }
    [ForeignKey("TrailId")]    
    public Trail Trail { get; set; }
}