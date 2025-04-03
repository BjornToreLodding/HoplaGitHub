using MediatR;
using HoplaBackend.Data;
using HoplaBackend.Events;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models;

namespace HoplaBackend.EventHandlers
{
    public class EntityDeletedEventHandler : INotificationHandler<EntityDeletedEvent>
    {
        private readonly AppDbContext _context;

        public EntityDeletedEventHandler(AppDbContext context)
        {
            _context = context;
        }

        public async Task Handle(EntityDeletedEvent notification, CancellationToken cancellationToken)
        {
            var entityType = Enum.Parse<EntityType>(notification.EntityType);

            var entityFeeds = _context.EntityFeeds
                .Where(f => f.EntityId == notification.EntityId && f.EntityName == entityType);


            _context.EntityFeeds.RemoveRange(entityFeeds);
            await _context.Database.ExecuteSqlRawAsync("COMMIT;");
        }
    }
}
