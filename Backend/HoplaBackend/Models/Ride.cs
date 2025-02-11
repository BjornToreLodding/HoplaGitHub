using System;
using System.Collections.Generic;
//using HoplaBackend.Models;

namespace MyApp.Models;

public partial class Ride
{
    public int Id { get; set; }

    public int? UserId { get; set; }

    public int? HorseId { get; set; }

    public DateTime Date { get; set; } = DateTime.UtcNow;

    public double? Length { get; set; }

    public int? Duration { get; set; }

    public double? LatMean { get; set; }

    public double? LongMean { get; set; }

    public double? LatStart { get; set; }

    public double? LongStart { get; set; }

    public double? LatEnd { get; set; }

    public double? LongEnd { get; set; }

    //Disse ligger i RideDetail og blir flyttet dit ettervhert når ting fungerer.
    public string? CoordinateslistShort { get; set; }

    public string? CoordinatesAll { get; set; }

    public byte[]? Picturethumb { get; set; }

    public byte[]? Picturefull { get; set; }



/*
    public virtual Horse? Horse { get; set; }

    public virtual RideDetail? RideDetail { get; set; }

    public virtual User? User { get; set; }

*/
}
