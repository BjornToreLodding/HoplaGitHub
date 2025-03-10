using System.Drawing;

namespace HoplaBackend.DTOs;
public class TrailDto
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string PictureUrl { get; set; }
    public double AverageRating { get; set; }
    public bool IsFavorite { get; set; }
}
//Lage flere underklasser ettersom hva som skal overføres.