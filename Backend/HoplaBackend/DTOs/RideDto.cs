namespace HoplaBackend
{
    public class RideRequestDto
    {
        public Guid UserId { get; set; }
        public Guid HorseId { get; set; }
        public Guid TrailId { get; set; }
        public double Duration { get; set; }
        public double Length { get; set; }
        public List<RideCoordinateDto> Coordinates { get; set; } = null!;
    }

    public class RideCoordinateDto
    {
        public double? TimeSinceLast { get; set; } // tid_siden_forrige_koordinat
        public double Latitude { get; set; }
        public double Longitude { get; set; }
    }

    public class RideUpdateDto
    {
        public string? Review { get; set; }
        public List<string>? Images { get; set; }
    }
}
