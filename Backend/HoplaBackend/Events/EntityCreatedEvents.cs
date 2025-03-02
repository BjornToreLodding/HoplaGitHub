namespace HoplaBackend.Events;

public class EntityCreatedEvent : EntityEvent
{
    public EntityCreatedEvent(Guid entityId, string entityType, Guid userId)
        : base(entityId, entityType, userId, "Created") { }
}
