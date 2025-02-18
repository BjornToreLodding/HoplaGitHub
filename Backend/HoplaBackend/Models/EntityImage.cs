using System.ComponentModel.DataAnnotations.Schema;


namespace MyApp.Models;
//kobling mellom Image og de forskjellige Entitys.

public class EntityImage
{
    public int Id { get; set; }

    public int? ImageId { get; set; } // FK til Image
    public Image? Image { get; set; } 

    public int? RideDetailId { get; set; } // FK til RideDetails (valgfri)
    public RideDetail? RideDetails { get; set; }
    public int? TrailDetailsId { get; set; } // Valgfritt
    public TrailDetail? TrailDetails { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}


