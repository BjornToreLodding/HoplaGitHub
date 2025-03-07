namespace HoplaBackend.DTOs;
public class UserHikeDto
{
    public Guid Id { get; set; }
    public string TrailName { get; set; }
    public double? Length { get ; set; }
    public double Duration { get; set; }
    public string? PictureUrl { get; set; }


}
