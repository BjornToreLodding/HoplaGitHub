namespace HoplaBackend.DTOs;
public class RegisterHorseForm
{
    public required string Name { get; set; }
    public string? Breed { get; set; }
    public IFormFile? Image { get; set; }    
    //public DateTime? Dob { get; set; }
    
    public int? Age { get; set; }
    public int? Year { get; set; }
    public int? Month { get; set; }
    public int? Day { get; set; }
}
public class RegisterHorse //gammel uten bilde
{
    public required string Name { get; set; }
    public Guid UserId { get; set; }
    public string? Breed { get; set; }
    //public DateTime? Dob { get; set; }
    public int? Age { get; set; }
    public int? Year { get; set; }
    public int? Month { get; set; }
    public int? Day { get; set; }
}