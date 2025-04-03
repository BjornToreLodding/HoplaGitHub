using MediatR;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.Events;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.EventHandlers
{
    public class EntityCreatedEventHandler : INotificationHandler<EntityCreatedEvent>
    {
        private readonly AppDbContext _context;

        public EntityCreatedEventHandler(AppDbContext context)
        {
            _context = context;
        }

        public async Task Handle(EntityCreatedEvent notification, CancellationToken cancellationToken)
        {
            var entityFeed = new EntityFeed
            {
                EntityId = notification.EntityId,
                EntityName = Enum.Parse<EntityType>(notification.EntityType),
                CreatedAt = DateTime.UtcNow,
                UserId = notification.UserId,
                ActionType = notification.ActionType
            };

            _context.EntityFeeds.Add(entityFeed);
            await _context.Database.ExecuteSqlRawAsync("COMMIT;");  // 🚀 Lagrer uten å trigge SaveChangesAsync()

        }
    }
}
