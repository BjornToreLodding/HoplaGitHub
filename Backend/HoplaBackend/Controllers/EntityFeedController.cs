using System.Security.Claims;
using HoplaBackend.Controllers;
using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.DTOs;

namespace HoplaBackend.Controllers;
[ApiController]
[Route("feed")]

public class EntityFeedController : ControllerBase
{
    private readonly AppDbContext _context;

    public EntityFeedController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet("all")]
    public async Task<IActionResult> GetFeed()
    {
        var feedEntries = await _context.EntityFeeds
            .OrderByDescending(f => f.CreatedAt)
            .Take(50)
            .ToListAsync();

        var result = new List<FeedItemDto>();

        foreach (var entry in feedEntries)
        {
            var dto = entry.EntityName switch
            {
                EntityType.Trail => await BuildTrailDto(entry),
                EntityType.UserHike => await BuildUserHikeDto(entry),
                EntityType.TrailReview => await BuildTrailReviewDto(entry),
                _ => null
            };

            if (dto != null)
                result.Add(dto);
        }

        return Ok(result);
    }

    private async Task<FeedItemDto?> BuildTrailDto(EntityFeed entry)
    {
        var trail = await _context.Trails
            .Include(t => t.User)
            .FirstOrDefaultAsync(t => t.Id == entry.EntityId);

        if (trail == null) return null;

        return new FeedItemDto
        {
            EntityId = trail.Id,
            EntityName = "Trail",
            Title = trail.Name,
            Description = trail.TrailDetails.Description,
            PictureUrl = entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = trail.UserId ?? Guid.Empty,
            UserAlias = trail.User?.Alias
        };
    }

    private async Task<FeedItemDto?> BuildUserHikeDto(EntityFeed entry)
    {
        var hike = await _context.UserHikes
            .Include(h => h.User)
            .Include(h => h.Trail)
            .FirstOrDefaultAsync(h => h.Id == entry.EntityId);

        if (hike == null) return null;

        return new FeedItemDto
        {
            EntityId = hike.Id,
            EntityName = "UserHike",
            Title = hike.Trail?.Name ?? "Tur",
            Description = $"Red en tur: {hike.Trail?.TrailDetails.Description}",
            PictureUrl = entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = hike.UserId,
            UserAlias = hike.User?.Alias,
            Duration = hike.Duration
        };
    }

    private async Task<FeedItemDto?> BuildTrailReviewDto(EntityFeed entry)
    {
        var review = await _context.TrailReviews
            .Include(r => r.User)
            .Include(r => r.Trail)
            .FirstOrDefaultAsync(r => r.Id == entry.EntityId);

        if (review == null) return null;

        return new FeedItemDto
        {
            EntityId = review.Id,
            EntityName = "TrailReview",
            Title = review.Trail?.Name ?? "Anmeldelse",
            Description = review.Comment,
            PictureUrl = review.PictureUrl ?? entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = review.UserId,
            UserAlias = review.User?.Alias
        };
    }
}

