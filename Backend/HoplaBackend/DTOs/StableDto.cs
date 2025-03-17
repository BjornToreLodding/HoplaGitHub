namespace HoplaBackend.DTOs;
public class CreateStableRequest
{
    public required string Name { get; set; }
    public required string Description { get; set; }
    public string PictureUrl { get; set; } = "";
    public required double Latitude { get; set; }
    public required double Longitude { get; set;}
    public bool PrivateGroup { get; set; } = false;
}
