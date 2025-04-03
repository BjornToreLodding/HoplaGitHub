namespace HoplaBackend.DTOs;
public class UserHikeDto
{
    public Guid Id { get; set; }
    public string TrailName { get; set; }
    public Guid TrailId { get; set; }
    public double? Length { get ; set; }
    public double Duration { get; set; }
    public string? PictureUrl { get; set; }
    public bool TrailButton { get; set; }
}
public class CoordinateInput
{
    public long Timestamp { get; set; } // Unix-tid i ms
    public double Lat { get; set; }
    public double Long { get; set; }
}

public class CreateUserHikeForm
{
    public Guid? TrailId { get; set; }
    public IFormFile? Image { get; set; } 

    public Guid? HorseId { get; set; }
    public DateTime StartedAt { get; set; }
    public string? Title { get; set; }
    public string? Description { get; set; }  
    public double Distance { get; set; } 
    public double Duration { get; set; } 

    public string Coordinates { get; set; }
    //public List<CoordinateInput> Coordinates { get; set; } = new();
}

public class UpdateUserHikeForm
{
    public Guid? TrailId { get; set; } //lage søkefunksjon for å finne matching Trail. 
    // f.eks utgangspunkt i lat/longMean og sammenligne rutas koordinater.
    public IFormFile Image { get; set; } 
    public string? Title { get; set; }
    public string? Description { get; set; }
    //public int? Rating { get; set; }
    
    public Guid HorseId { get; set; }
}