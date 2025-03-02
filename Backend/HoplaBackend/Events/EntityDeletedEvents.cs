namespace HoplaBackend.Events;
public class EntityDeletedEvent : EntityEvent
{
    public EntityDeletedEvent(Guid entityId, string entityType, Guid userId)
        : base(entityId, entityType, userId, "Deleted") { }
}
