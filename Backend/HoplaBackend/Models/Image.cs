using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;


// Tabell som brukes av ImageEntity..

public class Image
{
    public Guid Id { get; set; }
    public string PictureUrl { get; set; } = null!;// Full størrelse bilde
    public string Description { get; set; } = null!; // Beskrivelse av bildet
    public int LikesCount { get ; set ; } = 0 ;
    public int CommentsCount { get ; set ; } = 0;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
