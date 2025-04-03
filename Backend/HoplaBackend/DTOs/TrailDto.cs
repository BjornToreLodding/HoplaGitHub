using System.Drawing;
using HoplaBackend.Models;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.DTOs;
public class TrailDto
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string Description { get; set; }
    public string PictureUrl { get; set; }
    public double AverageRating { get; set; }
    public bool IsFavorite { get; set; }

    public IEnumerable<object> Filters { get; set; } = new List<object>();
}

public class CreateTrailForm
{
    //public string Name { get; set; }
    //public Guid UserHikeId { get; set; }
    public IFormFile Image { get; set; }
    public string dataJson { get; set; }
    //public List<FilterInputDto> Filters { get; set; } = new();
}
public class CreateTrailDto
{
    public Guid UserHikeId { get; set; }
    public string Name { get; set; } = string.Empty;
    public string? PictureUrl { get; set; }
    public List<FilterInputDto> Filters { get; set; } = new();
}

public class FilterInputDto
{
    public Guid FilterDefinitionId { get; set; }
    public string Value { get; set; } = string.Empty;
}

public class TrailFavoriteDto
{
    public Guid TrailId { get; set; }
    
}
public class TrailRateDto
{
    public Guid TrailId { get; set; }
    public int Rating { get; set; } //1-5 stjerner
}
public class TrailReviewForm{
    public Guid TrailId { get; set; }
    public string Message { get; set; }
    public IFormFile? Image { get; set; }
    public TrailConditionType Condition { get; set; } = TrailConditionType.Ukjent;
}
public class TrailReviewResponseDto
{
    public Guid Id { get; set; }
    public string Comment { get; set; } = string.Empty;
    public string PictureUrl { get; set; } = string.Empty;
    public TrailConditionType Condition { get; set; }
    public DateTime CreatedAt { get; set; }
    public string Alias { get; set; } = string.Empty;
}

//Trenger ikke å brukes.
[Owned] // Dette gjør at EF håndterer det uten å lage en egen tabell manuelt
public class TrailCoordinateDto
{
    public double Lat { get; set; }
    public double Long { get; set; }
}


//Lage flere underklasser ettersom hva som skal overføres.