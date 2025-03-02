using System.ComponentModel.DataAnnotations.Schema;


namespace HoplaBackend.Models;
//kobling mellom Image og de forskjellige Entitys.

public class EntityImage
{
    public Guid Id { get; set; }

    public Guid ImageId { get; set; } // FK til Image
    public Image Image { get; set; } 
    public Guid EntityId { get; set; }

    public string EntityName { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}


