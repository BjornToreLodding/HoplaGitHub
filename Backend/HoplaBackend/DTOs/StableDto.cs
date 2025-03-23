namespace HoplaBackend.DTOs;
public class CreateStableForm
{
    public required string Name { get; set; }
    public required string Description { get; set; }
    public required double Latitude { get; set; }
    public required double Longitude { get; set;}
    public bool PrivateGroup { get; set; } = false;
    public IFormFile? Image { get; set; }
}
