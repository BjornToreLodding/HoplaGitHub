namespace HoplaBackend.DTOs;
public class RegisterHorse
{
    public required string Name { get; set; }
    public Guid UserId { get; set; }
    public string? Breed { get; set; }
    public string? PictureUrl { get; set; }
    //public DateTime? Dob { get; set; }
    public int? Age { get; set; }
}