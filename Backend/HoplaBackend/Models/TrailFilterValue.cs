using HoplaBackend.Models;

public class TrailFilterValue
{
    public Guid Id { get; set; } = Guid.NewGuid(); // ✅ Bruker Guid
    public Guid TrailId { get; set; } // ✅ Referanse til Trail
    public Guid FilterDefinitionId { get; set; } // ✅ Referanse til FilterDefinition
    public string Value { get; set; } // Verdien (lagres som string)

    public Trail Trail { get; set; }
    public TrailFilterDefinition TrailFilterDefinition { get; set; }
}
