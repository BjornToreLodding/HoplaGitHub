using MediatR;

namespace HoplaBackend.Events;
public abstract class EntityEvent : INotification
{
    public Guid EntityId { get; }
    public string EntityType { get; }
    public Guid UserId { get; }
    public string ActionType { get; }

    protected EntityEvent(Guid entityId, string entityType, Guid userId, string actionType)
    {
        EntityId = entityId;
        EntityType = entityType;
        UserId = userId;
        ActionType = actionType;
    }
}
