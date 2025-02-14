using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;


// Tabell som brukes av ImageEntity..

public class Image
{
    public int Id { get; set; }
    public string Url { get; set; } = null!;// Full størrelse bilde
    public string ThumbnailUrl { get; set; } = null!; // Liten versjon
    public string Description { get; set; } = null!; // Beskrivelse av bildet
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
