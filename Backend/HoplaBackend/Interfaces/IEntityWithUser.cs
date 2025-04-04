namespace HoplaBackend.Interfaces;

public interface IEntityWithUser
{
    Guid Id { get; }
    Guid UserId { get; }
}
