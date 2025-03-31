using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using HoplaBackend.Models;

public class UserHikeDetail
{
    [Key, ForeignKey(nameof(Hike))]
    public Guid UserHikeId { get; set; }

    public UserHike Hike { get; set; } = null!;
    public double LatMin { get; set; }
    public double LatMax { get; set; }
    public double LatMean { get; set; }

    public double LongMin { get; set; }
    public double LongMax { get; set; }
    public double LongMean { get; set; }
    public string? Description { get; set; }

    public string CoordinatesCsv { get; set; } = string.Empty;

    [NotMapped]
    public List<(int OffsetTenths, double Lat, double Lng)> Coordinates
    {
        get
        {
            return CoordinatesCsv
                .Split(';', StringSplitOptions.RemoveEmptyEntries)
                .Select(part =>
                {
                    var split = part.Split(',');
                    return (
                        int.Parse(split[0]),   // offset i tid (0.1s)
                        double.Parse(split[1]),
                        double.Parse(split[2])
                    );
                }).ToList();
        }
        set
        {
            CoordinatesCsv = string.Join(';', value.Select(c =>
                $"{c.OffsetTenths},{c.Lat},{c.Lng}"));
        }
    }


    //
    //For faktisk tid:
    //var start = userHike.StartedAt;
    //var actualTimestamp = start.AddSeconds(coord.OffsetTenths / 10.0);
    //

}


