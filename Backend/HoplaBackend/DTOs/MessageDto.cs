namespace HoplaBackend.DTOs;

public class CreateMessageDto
{
    public Guid SenderId { get; set; }
    public Guid ReceiverId { get; set; }
    public string Content { get; set; } = string.Empty;
}
