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
    public string Category { get; set; } = "Annet";
    public string Status { get; set; } = "New"; // (New, InProgress, Resolved, Closed) Hvis ikke besvart innen 7 dager, settes status til closed.
    public string EntityName { get; set; } // skal ende på 's', f.eks Horses, Trails, Users osv.

    public string Message { get; set; }  // Rapportens innhold
    public string FeedBack { get; set; } = "";

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime? InProgress { get; set; }
    public DateTime? Resolved { get; set; }

    public DateTime? Closed { get; set; }

    public virtual User User { get; set; }
}
