namespace HoplaBackend.Models.DTOs;

public class FeedQueryOptions
{
    public int PageNumber { get; set; } = 1;
    public int PageSize { get; set; } = 50;
    public string Sort { get; set; } = "desc"; // "desc", "asc" eller "likes"
    public string? Search { get; set; }
    public string? Show { get; set; } // "trails,userhikes,trailreviews,horses"
    public bool OnlyFriendsAndFollowing { get; set; } = false;
    public bool OnlyLikedTrails { get; set; } = false;
    public double? Lat { get; set; }
    public double? Long { get; set; }
    public double? Radius { get; set; }
}
