namespace HoplaBackend.Models;

public class EntityFeed
{
    public Guid Id { get; set; }
    public Guid EntityId { get; set; }
    public string EntityName { get; set; }
    public string? EntityTitle { get; set; }  // Felt for å lagre navn på hesten/turen
    public string? EntityObject { get ; set; } //Felt for å lagre navn på den/det som ble brukt på Entity
    public string? PictureUrl { get; set; }
    public string ActionType { get; set; } // Galloperte, la til, er nå venner med , 
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public Guid UserId { get; set; }

    // Teller for likes og kommentarer
    public int LikesCount { get; set; } = 0;
    public int CommentsCount { get; set; } = 0;
}
