using MediatR;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.Events;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.EventHandlers;
public class EntityCreatedEventHandler : INotificationHandler<EntityCreatedEvent>
{
    private readonly AppDbContext _context;

    public EntityCreatedEventHandler(AppDbContext context)
    {
        _context = context;
    }

    public async Task Handle(EntityCreatedEvent notification, CancellationToken cancellationToken)
    {
        if (!Enum.TryParse<EntityType>(notification.EntityType, out var entityType))
        {
            Console.WriteLine($"Ukjent EntityType: {notification.EntityType} - hopper over.");
            return; // Hopper over ukjente entiteter uten crash
        }

        var entityFeed = new EntityFeed
        {
            EntityId = notification.EntityId,
            EntityName = entityType,
            //CreatedAt = DateTime.UtcNow,
            UserId = notification.UserId,
            ActionType = notification.ActionType
        };
                //_context.EntityFeeds.Add(entityFeed);
        //await _context.Database.ExecuteSqlRawAsync("COMMIT;");  // 🚀 Lagrer uten å trigge SaveChangesAsync()
    
        switch (entityFeed.EntityName)
        {
            case EntityType.Horse:
                var horse = await _context.Horses.FindAsync(notification.EntityId);
                if (horse != null)
                {
                    entityFeed.EntityTitle = horse.Name;
                    entityFeed.PictureUrl = horse.PictureUrl;
                    entityFeed.CreatedAt = horse.CreatedAt;
                }
                break;

            case EntityType.Trail:
                var trail = await _context.Trails.FindAsync(notification.EntityId);
                if (trail != null)
                {
                    entityFeed.EntityTitle = trail.Name;
                    entityFeed.PictureUrl = trail.PictureUrl;
                    entityFeed.CreatedAt = trail.CreatedAt;
                }
                break;

            case EntityType.UserHike:
                var userHike = await _context.UserHikes.FindAsync(notification.EntityId);
                if (userHike != null)
                {
                    entityFeed.EntityTitle = "Tur på " + userHike.CreatedAt.ToShortDateString();
                    entityFeed.PictureUrl = userHike.PictureUrl;
                    entityFeed.CreatedAt = userHike.CreatedAt;
                    // userHike har kanskje ikke bilde
                }
                break;
            case EntityType.Stable:
                var stable = await _context.Stables.FindAsync(notification.EntityId);
                if (stable != null)
                {
                    entityFeed.EntityTitle = stable.Name;
                    entityFeed.PictureUrl = stable.PictureUrl;
                    entityFeed.CreatedAt = stable.CreatedAt;
                }
                break;

            case EntityType.StableMessage:
                var stableMessage = await _context.StableMessages.FindAsync(notification.EntityId);
                if (stableMessage != null)
                {
                    entityFeed.EntityTitle = stableMessage.MessageText; // eller Title hvis du har det
                    // entityFeed.PictureUrl = stableMessage.PictureUrl; // hvis du har bilde
                    entityFeed.CreatedAt = stableMessage.SentAt;
                }
                break;

            case EntityType.TrailReview:
                var trailReview = await _context.TrailReviews.FindAsync(notification.EntityId);
                if (trailReview != null)
                {
                    entityFeed.EntityTitle = "Anmeldelse: " + trailReview.Comment;
                    entityFeed.PictureUrl = trailReview.PictureUrl; // hvis du har bilde
                    entityFeed.CreatedAt = trailReview.CreatedAt;
                }
                break;

        }

        // 🚨 Legg på standardbilde hvis PictureUrl er tom
        if (string.IsNullOrEmpty(entityFeed.PictureUrl))
        {
            entityFeed.PictureUrl = GetDefaultPicture(entityFeed.EntityName);
        }

        _context.EntityFeeds.Add(entityFeed);
        await _context.Database.ExecuteSqlRawAsync("COMMIT;");
    }
        

private string GetDefaultPicture(EntityType entityType)
{
    return entityType switch
    {
        EntityType.Horse => "main-horse.jpg",
        EntityType.Trail => "main-trail.jpg",
        EntityType.UserHike => "main-hike.jpg",
        EntityType.TrailReview => "main-review.jpg",
        EntityType.Stable => "main-stable.jpg",
        _ => "main.jpg", // fallback for ukjent type
    };
    }
}
