using HoplaBackend.Models;
using HoplaBackend.Data;

namespace HoplaBackend.Helpers;
public class EntityFeedHelper
{
    private readonly AppDbContext _context;

    public EntityFeedHelper(AppDbContext context)
    {
        _context = context;
    }

    public async Task AddFeedEntryAsync(Guid entityId, EntityType entityType, string actionType, Guid userId, string? entityTitle = null, string? pictureUrl = null)
    {
        var feedItem = new EntityFeed
        {
            Id = Guid.NewGuid(),
            EntityId = entityId,
            EntityName = entityType,
            ActionType = actionType,
            UserId = userId,
            EntityTitle = entityTitle,
            PictureUrl = pictureUrl,
            CreatedAt = DateTime.UtcNow
        };

        _context.EntityFeeds.Add(feedItem);
        await _context.SaveChangesAsync();
    }
}
