namespace MyApp.DTOs;

public class CreateStableMessageDto
{
    public int UserId { get; set; }
    public int StableId { get; set; }
    public string Content { get; set; } = string.Empty;
}
