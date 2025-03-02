using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;
public class UserReport
{
    [Key]
    public Guid Id { get; set; }

    [ForeignKey("User")]
    public Guid UserId { get; set; }  // Brukeren som rapporterer

    public Guid EntityId { get; set; }

    public string EntityName { get; set; }

    public string Message { get; set; }  // Rapportens innhold

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public virtual User User { get; set; }
}

