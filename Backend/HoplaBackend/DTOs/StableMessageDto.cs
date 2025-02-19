namespace HoplaBackend.DTOs;

public class CreateStableMessageDto
{
    public Guid UserId { get; set; }
    public Guid StableId { get; set; }
    public string Content { get; set; } = string.Empty;
}
