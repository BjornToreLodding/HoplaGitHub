using System.Security.Claims;
using HoplaBackend.Controllers;
using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.DTOs;
using HoplaBackend.Helpers;
using HoplaBackend.Models.DTOs;
using System.Linq;

namespace HoplaBackend.Controllers;
[ApiController]
[Route("feed")]

public class EntityFeedController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;

    public EntityFeedController(AppDbContext context, Authentication authentication)
    {
        _context = context;
        _authentication = authentication;
    }

/*
    [HttpGet("all")]
    public async Task<IActionResult> GetFeed(
        int pageNumber = 1,
        int pageSize = 50,
        string sort = "desc",
        string? search = null,
        string? show = null,
        bool onlyFriendsAndFollowing = false,
        bool onlyLikedTrails = false,
        double? userLat = null,
        double? userLon = null,
        double? radiusKm = null)
*/
    [HttpGet("all")]
    public async Task<IActionResult> GetFeed([FromQuery] FeedQueryOptions options)
    {
        if (options.PageSize > 200) options.PageSize = 200;

        var query = _context.EntityFeeds.AsQueryable();
        var userId = _authentication.GetUserIdFromToken(User);

        // 🔒 Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        // 🔍 Søk
        if (!string.IsNullOrWhiteSpace(options.Search))
        {
            query = query.Where(f =>
                (f.EntityTitle != null && f.EntityTitle.ToLower().Contains(options.Search.ToLower())) ||
                (f.EntityObject != null && f.EntityObject.ToLower().Contains(options.Search.ToLower()))
            );
        }

        // 📆 Begrens til siste 30 dager
        var thirtyDaysAgo = DateTime.UtcNow.AddDays(-200);
        query = query.Where(f => f.CreatedAt >= thirtyDaysAgo);

        // 🔎 EntityType-filter
        if (!string.IsNullOrWhiteSpace(options.Show))
        {
            var allowedTypes = options.Show.Split(',', StringSplitOptions.RemoveEmptyEntries)
                .Select(s => EntityTypeMapper.MapStringToEntityType(s))
                .Where(t => t.HasValue)
                .Select(t => t.Value)
                .ToList();

            query = query.Where(f => allowedTypes.Contains(f.EntityName));
        }

        // 🧍‍♂️ Venner/følgere-filter
        if (options.OnlyFriendsAndFollowing)
        {
            var friendsAndFollowingIds = await _context.UserRelations
                .Where(r => (r.FromUserId == userId || r.ToUserId == userId) &&
                            (r.Status == "FRIENDS" || r.Status == "FOLLOWING"))
                .Select(r => r.FromUserId == userId ? r.ToUserId : r.FromUserId)
                .ToListAsync();

            query = query.Where(f => friendsAndFollowingIds.Contains(f.UserId));
        }

        // 🌟 Løyper brukeren har likt
        if (options.OnlyLikedTrails)
        {
            var likedTrailIds = await _context.EntityReactions
                .Where(r => r.UserId == userId && r.EntityName == "Trail")
                .Select(r => r.EntityId)
                .ToListAsync();

            query = query.Where(f => likedTrailIds.Contains(f.EntityId));
        }

        // 🔀 Sortering
        query = options.Sort?.ToLower() switch
        {
            "likes" => query.OrderByDescending(f => f.LikesCount)
                            .ThenByDescending(f => f.CreatedAt),
            _ => query.OrderByDescending(f => f.CreatedAt)
        };

        // 📈 Begrens antall rader som hentes fra DB
        query = query.Take(1000); // F.eks. maks 500 for effektivitet

        // 🚀 Hent alle relevante poster
        var feedEntries = await query.ToListAsync();

        // 🌍 Bygg Feed DTOs og filtrer på radius
        var result = new List<FeedItemDto>();

        foreach (var entry in feedEntries)
        {
            var dto = entry.EntityName switch
            {
                EntityType.Trail => await BuildTrailDto(entry),
                EntityType.UserHike => await BuildUserHikeDto(entry),
                EntityType.TrailReview => await BuildTrailReviewDto(entry),
                EntityType.TrailRating => await BuildTrailRatingDto(entry),
                EntityType.Horse => await BuildHorseDto(entry),
                EntityType.Stable => await BuildStableDto(entry),
                EntityType.StableMessage => await BuildStableMessageDto(entry),
                _ => null
            };

            if (dto == null)
                continue;

            // 🌍 Radiusfilter
            if (options.Lat.HasValue && options.Long.HasValue && options.Radius.HasValue)
            {
                if (!dto.Latitude.HasValue || !dto.Longitude.HasValue)
                    continue;

                double distance = DistanceCalc.ImprovedPytagoras(
                    options.Lat.Value,
                    options.Long.Value,
                    dto.Latitude.Value,
                    dto.Longitude.Value
                );

                if (distance > options.Radius.Value)
                    continue;
            }

            result.Add(dto);
        }

        // 📄 Manuell Paging
        var pagedResult = result
            .Skip((options.PageNumber - 1) * options.PageSize)
            .Take(options.PageSize)
            .ToList();

        return Ok(new
        {
            totalCount = result.Count, // filtrerte resultater
            options.PageNumber,
            options.PageSize,
            hasNextPage = (options.PageNumber * options.PageSize) < result.Count,
            items = pagedResult
        });
    }



    /*
    [HttpGet("all")]
    public async Task<IActionResult> GetFeed(
        int pageNumber = 1,
        int pageSize = 50,
        string sort = "desc",
        string? search = null,
        string? show = null)
    {
        if (pageSize > 200) pageSize = 200;

        var query = _context.EntityFeeds.AsQueryable();

        // 🔍 Søking
        if (!string.IsNullOrWhiteSpace(search))
        {
            query = query.Where(f =>
                (f.EntityTitle != null && f.EntityTitle.ToLower().Contains(search.ToLower())) ||
                (f.EntityObject != null && f.EntityObject.ToLower().Contains(search.ToLower()))
            );
        }

        // 🔎 Filtrering på EntityType
        if (!string.IsNullOrWhiteSpace(show))
        {
            var allowedTypes = show.Split(',', StringSplitOptions.RemoveEmptyEntries)
                .Select(s => s.Trim().ToLower())
                .ToList();

            query = query.Where(f =>
                (allowedTypes.Contains("trails") && f.EntityName == EntityType.Trail) ||
                (allowedTypes.Contains("userhikes") && f.EntityName == EntityType.UserHike) ||
                (allowedTypes.Contains("trailreviews") && f.EntityName == EntityType.TrailReview) ||
                (allowedTypes.Contains("trailrating") && f.EntityName == EntityType.TrailRating) ||
                (allowedTypes.Contains("stables") && f.EntityName == EntityType.Stable) ||
                (allowedTypes.Contains("stablemessages") && f.EntityName == EntityType.StableMessage)
            );
        }

        // 🔀 Sortering
        query = sort.ToLower() switch
        {
            "asc" => query.OrderBy(f => f.CreatedAt),
            _ => query.OrderByDescending(f => f.CreatedAt)
        };

        // 📄 Paging
        query = query
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize);

        var feedEntries = await query.ToListAsync();

        var result = new List<FeedItemDto>();

        foreach (var entry in feedEntries)
        {
            var dto = entry.EntityName switch
            {
                EntityType.Trail => await BuildTrailDto(entry),
                EntityType.UserHike => await BuildUserHikeDto(entry),
                EntityType.TrailReview => await BuildTrailReviewDto(entry),
                EntityType.Horse => await BuildHorseDto(entry),
                EntityType.Stable => await BuildStableDto(entry),
                EntityType.StableMessage => await BuildStableMessageDto(entry),
                _ => null
            };

            if (dto != null)
                result.Add(dto);
        }

        return Ok(result);
    }
    */


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
            Description = trail.TrailDetails?.Description != null
                ? trail.TrailDetails.Description
                : $"{trail.User.Alias} opprettet ny løype: {trail.Name}",
            PictureUrl = PictureHelper.BuildPictureUrl(entry.PictureUrl, "FeedPicture"),
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            Latitude = trail.LatMean,
            Longitude = trail.LongMean,
            UserId = trail.UserId,
            UserAlias = trail.User?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(trail.User.PictureUrl, "UserProfilePictureList"),

        };
    }
    private async Task<FeedItemDto?> BuildUserHikeDto(EntityFeed entry)
    {
        var hike = await _context.UserHikes
            .Include(h => h.User)
            .Include(h => h.Trail)
            .Include(h => h.UserHikeDetail) // 🚀 Viktig!
            .FirstOrDefaultAsync(h => h.Id == entry.EntityId);

        if (hike == null) return null;

        double? latitude = null;
        double? longitude = null;

        if (hike.TrailId != null)
        {
            latitude = hike.Trail.LatMean;
            longitude = hike.Trail.LongMean;
        }
        else if (hike.UserHikeDetail != null)
        {
            latitude = hike.UserHikeDetail.LatMean;
            longitude = hike.UserHikeDetail.LongMean;
        }
        var trailName = !string.IsNullOrWhiteSpace(hike.Trail?.Name) ? hike.Trail.Name : "finnes ikke";
        return new FeedItemDto
        {
            EntityId = hike.Id,
            EntityName = "UserHike",
            Title = !string.IsNullOrWhiteSpace(hike.Title) ? hike.Title : "Ridetur",
            //Title = hike.Trail?.Name ?? "Tur",
            Description = $"{hike.User.Alias} red en tur i løypen: {trailName}",
            PictureUrl = PictureHelper.BuildPictureUrl(entry.PictureUrl, "FeedPicture"),
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = hike.UserId,
            UserAlias = hike.User?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(hike.User.PictureUrl, "UserProfilePictureList"),
            Latitude = latitude,
            Longitude = longitude,
            Duration = hike.Duration
        };
    }

        /*
        private async Task<FeedItemDto?> BuildUserHikeDto(EntityFeed entry)
        {
            var hike = await _context.UserHikes
                .Include(h => h.User)
                .Include(h => h.Trail)
                .FirstOrDefaultAsync(h => h.Id == entry.EntityId);

            if (hike == null) return null;

            return new FeedItemDto //linje 80
            {
                EntityId = hike.Id,
                EntityName = "UserHike",
                //Title = hike.Trail?.Name ?? "Tur",
                Description = hike.Trail?.TrailDetails?.Description != null 
                    ? $"Red en tur: {hike.Trail.TrailDetails.Description}"
                    : "Red en tur",
                //Description = $"Red en tur: {hike.Trail?.TrailDetails.Description}",
                PictureUrl = entry.PictureUrl,
                ActionType = entry.ActionType,
                CreatedAt = entry.CreatedAt,
                UserId = hike.UserId,
                UserAlias = hike.User?.Alias,
                Duration = hike.Duration
            };
        }
        */

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
            Description = $"{review.User.Alias} skrev en anmeldelse av {review.Trail.Name}",
            PictureUrl = PictureHelper.BuildPictureUrl(entry.PictureUrl, "FeedPicture"),
            //PictureUrl = review.PictureUrl ?? entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = review.UserId,
            Latitude = review.Trail.LatMean,
            Longitude = review.Trail.LongMean,
            UserAlias = review.User?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(review.User.PictureUrl, "UserProfilePictureList"),

        };
    }
    private async Task<FeedItemDto?> BuildTrailRatingDto(EntityFeed entry)
    {
        var rating = await _context.TrailRatings
            .Include(r => r.User)
            .Include(r => r.Trail)
            .FirstOrDefaultAsync(r => r.Id == entry.EntityId);

        if (rating == null) return null;

        return new FeedItemDto
        {
            EntityId = rating.Id,
            EntityName = "TrailRating",
            Title = rating.Trail?.Name ?? "Anmeldelse",
            Description = $"{rating.User.Alias} Vurderte {rating.Trail.Name} til {rating.Rating} stjerner",
            PictureUrl = null,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = rating.UserId,
            Latitude = rating.Trail.LatMean,
            Longitude = rating.Trail.LongMean,
            UserAlias = rating.User?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(rating.User.PictureUrl, "UserProfilePictureList"),

        };
    }
    private async Task<FeedItemDto?> BuildHorseDto(EntityFeed entry)
    {
        var horse = await _context.Horses
            .Include(h => h.User)
            .FirstOrDefaultAsync(h => h.Id == entry.EntityId);

        if (horse == null) return null;

        return new FeedItemDto
        {
            EntityId = horse.Id,
            EntityName = "Horse",
            Title = horse.Name ?? "Hest",
            Description = $"{horse.User.Alias} Registrerte en ny hest: {horse.Name}",
            PictureUrl = PictureHelper.BuildPictureUrl(entry.PictureUrl, "FeedPicture"),
            //PictureUrl = horse.PictureUrl ?? entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = horse.UserId,
            UserAlias = horse.User?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(horse.User.PictureUrl, "UserProfilePictureList"),

        };
    }
    private async Task<FeedItemDto?> BuildStableDto(EntityFeed entry)
    {
        var stable = await _context.Stables
            .Include(s => s.StableUsers)
            .ThenInclude(su => su.User)
            .FirstOrDefaultAsync(s => s.Id == entry.EntityId);

        if (stable == null) return null;

        // 🚀 Finn eieren (StableUser der IsOwner == true)
        var owner = stable.StableUsers
            .FirstOrDefault(su => su.IsOwner)?.User;

        return new FeedItemDto
        {
            EntityId = stable.Id,
            EntityName = "Stable",
            Title = stable.Name ?? "Stall",
            Description = stable.Description ?? "En ny stall er registrert.",
            PictureUrl = PictureHelper.BuildPictureUrl(entry.PictureUrl, "FeedPicture"),
            //PictureUrl = stable.PictureUrl ?? entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = owner?.Id ?? Guid.Empty, // 👈 bruker eieren hvis finnes
            UserAlias = owner?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(owner.PictureUrl, "UserProfilePictureList"),

        };
    }

    private async Task<FeedItemDto?> BuildStableMessageDto(EntityFeed entry)
    {
        var stableMessage = await _context.StableMessages
            .Include(sm => sm.User)
            .Include(sm => sm.Stable)
            .FirstOrDefaultAsync(sm => sm.Id == entry.EntityId);

        if (stableMessage == null) return null;

        return new FeedItemDto
        {
            EntityId = stableMessage.Id,
            EntityName = "StableMessage",
            Title = stableMessage.Stable?.Name ?? "Stallmelding",
            Description = stableMessage.MessageText ?? "Ny melding fra stall.",
            //PictureUrl = stableMessage.PictureUrl ?? entry.PictureUrl,
            ActionType = entry.ActionType,
            CreatedAt = entry.CreatedAt,
            UserId = stableMessage.UserId,
            UserAlias = stableMessage.User?.Alias,
            UserProfilePicture = PictureHelper.BuildPictureUrl(stableMessage.User.PictureUrl, "UserProfilePictureList"),

        };
    }


}

