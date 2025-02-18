using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public class Ride
{
    public Guid Id { get; set; }
    public double Length { get; set; }
    public double Duration { get; set; }

    public Guid? UserId { get; set; }
    [ForeignKey("UserId")]
    public User? User { get; set; }

    public Guid? HorseId { get; set; }
    [ForeignKey("HorseId")]
    public Horse? Horse { get; set; }

    public Guid? TrailId { get; set; }
    [ForeignKey("TrailId")]
    public Trail? Trail { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public RideDetail? RideDetails { get; set; }
    public RideTrackingData RideTrackingDatas { get; set; } = null!;
    public RideReview? RideReviews { get; set; } //kan være null, hvis man ikke legger inn review
}
/*
public partial class Ride
{
    [Key]
    public int Id { get; set; } //byttes fra int til Guid når det virker

    public int? UserId { get; set; } //Relasjon til Users-tabellen, for å kunne få opp navner på user.

    public int? HorseId { get; set; } //Relasjon til Horses-tabellen, for å kunne få opp hestens navn

    public DateTime Date { get; set; } = DateTime.UtcNow; //kan kalles noe annet, f.eks RideDate?

    public double? Length { get; set; } //burde kanskje være float.

    public int? Duration { get; set; } //burde være float.
    public string? Description { get; set; }
    public List<EntityImage> Images { get; set; } = new(); // Henter via EntityImageId


    public double? LatMean { get; set; }

    public double? LongMean { get; set; }

    public double? LatStart { get; set; }

    public double? LongStart { get; set; }

    public double? LatEnd { get; set; }

    public double? LongEnd { get; set; }

    //Disse ligger i RideDetail og blir flyttet dit ettervhert når ting fungerer.
    public string? CoordinateslistShort { get; set; }

    public string? CoordinatesAll { get; set; }

}
*/