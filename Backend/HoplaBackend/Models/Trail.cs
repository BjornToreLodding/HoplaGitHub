﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using HoplaBackend.Interfaces;

//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public class Trail : IEntityWithUser
{
    public Guid Id { get; set; }
    public string? Name { get; set; }
    public double Distance { get; set; }
    public double LatMean { get; set; }
    public double LongMean { get; set; }
    //public double? Length { get; set; }

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    public int LikesCount { get ; set ; } = 0 ;
    public int CommentsCount { get ; set ; } = 0;
    public string? PictureUrl { get; set; }
    public double? AverageRating { get; set; } = 0;
    public TrailVisibility Visibility { get; set; } = 0; // Enum, lenger ned

    [InverseProperty(nameof(TrailFilterValue.Trail))]
    public virtual ICollection<TrailFilterValue> TrailFilterValues { get; set; } = new List<TrailFilterValue>();
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public TrailDetail TrailDetails { get; set; } = null!;
    public TrailAllCoordinate TrailAllCoordinates { get; set; } = null!;
    //public TrailFilter TrailFilters { get; set; } = null!;
    public List<TrailReview> TrailReviews { get; set; } = new();
}
public enum TrailVisibility
{
    Public = 0,
    FriendsOnly = 1,
    Private = 2
}
/*
public partial class Trail
{
    [Key]
    public int Id { get; set; }

    public required int RideId { get; set; }
    [ForeignKey("RideId")]
    public Ride Ride { get; set; } = null!;
    public required string Name { get; set; }
    public string? Beskrivelse { get; set; }

    public int? FilterId { get; set; }

    public DateTime? CreatedAt { get; set; } = DateTime.UtcNow;

    //public virtual Filter? Filter { get; set; }

    //public virtual TrailDetail? TrailDetail { get; set; }
}
*/