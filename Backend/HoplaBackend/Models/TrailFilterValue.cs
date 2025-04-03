using System.ComponentModel.DataAnnotations.Schema;
using HoplaBackend.Models;

public class TrailFilterValue
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid TrailId { get; set; }
    //Burde omdøpes til TrailFilterDefinitionId
    public Guid TrailFilterDefinitionId { get; set; }

    public string Value { get; set; } = string.Empty;

    [ForeignKey(nameof(TrailId))]
    public Trail Trail { get; set; } = null!;

    [ForeignKey(nameof(TrailFilterDefinitionId))]
    public TrailFilterDefinition TrailFilterDefinition { get; set; } = null!;
}
