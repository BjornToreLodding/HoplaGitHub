using System.Drawing;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.DTOs;
public class TrailDto
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string PictureUrl { get; set; }
    public double AverageRating { get; set; }
    public bool IsFavorite { get; set; }
}

public class CreateTrailDto
{
    public string Name { get; set; }
    public string? PictureUrl { get; set; }
    public string Coordinates { get; set; }
    public double Distance { get; set; }
    

}
public class TrailRateDto
{
    public Guid TrailId { get; set; }
    public int Rating { get; set; } //1-5 stjerner
}


//Trenger ikke å brukes.
[Owned] // Dette gjør at EF håndterer det uten å lage en egen tabell manuelt
public class TrailCoordinateDto
{
    public double Lat { get; set; }
    public double Long { get; set; }
}
//Lage flere underklasser ettersom hva som skal overføres.