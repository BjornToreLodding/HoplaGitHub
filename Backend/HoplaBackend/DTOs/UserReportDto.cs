

namespace HoplaBackend.DTOs;
public class CreateUserReportRequest
{
    public Guid UserId { get; set; }      // Brukeren som rapporterer
    public Guid EntityId { get; set; }    // Entitet som rapporteres
    public string EntityName { get; set; } // Navn på entiteten (f.eks. "Horse", "Trail")
    public string Category { get; set; }
    public string Status { get; set; }
    public string Message { get; set; }    // Rapportens innhold
}
