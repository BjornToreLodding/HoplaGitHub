using System.ComponentModel.DataAnnotations.Schema;


namespace HoplaBackend.Models;
//kobling mellom Image og de forskjellige Entitys.

public class EntityImage
{
    public Guid Id { get; set; }

    public Guid? ImageId { get; set; } // FK til Image
    public Image? Image { get; set; } 

    public Guid? RideDetailId { get; set; } // FK til RideDetails (valgfri)
    public RideDetail? RideDetails { get; set; }
    public Guid? TrailDetailsId { get; set; } // Valgfritt
    public TrailDetail? TrailDetails { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}


