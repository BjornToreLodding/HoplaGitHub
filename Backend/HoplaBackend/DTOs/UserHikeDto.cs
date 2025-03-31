namespace HoplaBackend.DTOs;
public class UserHikeDto
{
    public Guid Id { get; set; }
    public string TrailName { get; set; }
    public double? Length { get ; set; }
    public double Duration { get; set; }
    public string? PictureUrl { get; set; }


}
public class CoordinateInput
{
    public long Timestamp { get; set; } // Unix-tid i ms
    public double Lat { get; set; }
    public double Lng { get; set; }
}

public class CreateUserHikeDto
{
    public Guid? TrailId { get; set; }
    public Guid HorseId { get; set; }
    public DateTime StartedAt { get; set; }
    public string? Description { get; set; }  
    public double Distance { get; set; } 
    public double Duration { get; set; } 

    public List<CoordinateInput> Coordinates { get; set; } = new();
}

public class UpdateUserHikeDto
{
    public string? Title { get; set; }
    public string? Description { get; set; }
    public string? PictureUrl { get; set; } // Du kan utvide til å støtte bildeopplasting senere
    //public int? Rating { get; set; }
    public Guid? TrailId { get; set; } //lage søkefunksjon for å finne matching Trail. 
    // f.eks utgangspunkt i lat/longMean og sammenligne rutas koordinater.
    
    public Guid HorseId { get; set; }
}