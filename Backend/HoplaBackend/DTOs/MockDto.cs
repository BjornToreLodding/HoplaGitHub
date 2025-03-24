namespace HoplaBackend.Models.DTOs;

public class CreateMockTrailDto
{
    public string Name { get; set; } = string.Empty;
    public double LatMean { get; set; }
    public double LongMean { get; set; }
    public double Distance { get; set; } // i kilometer
}

