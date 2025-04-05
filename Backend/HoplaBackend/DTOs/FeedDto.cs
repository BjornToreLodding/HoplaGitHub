using System.Text.Json.Serialization;
using HoplaBackend.Models;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.DTOs;

public class FeedItemDto
{
    public Guid EntityId { get; set; }
    public string EntityName { get; set; } = "";
    public string? Title { get; set; }
    public string? Description { get; set; }
    public string? PictureUrl { get; set; }
    public string ActionType { get; set; } = "";
    public DateTime CreatedAt { get; set; }

    public Guid UserId { get; set; }
    public string? UserAlias { get; set; }
    public string? UserProfilePicture { get; set; }
    [JsonIgnore]
    public double? Latitude { get; set; } // 🌍
    [JsonIgnore]
    public double? Longitude { get; set; } // 🌍

    public Double Duration { get; set; } // Kun relevant for UserHike
}
