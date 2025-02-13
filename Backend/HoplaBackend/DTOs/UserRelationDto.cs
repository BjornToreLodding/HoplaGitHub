namespace MyApp.DTOs;

public class CreateUserRelationDto
{
    //public required Guid FromUserId { get; set; }
    //public required Guid ToUserId { get; set; }
    public required int FromUserId { get; set; }
    public required int ToUserId { get; set; }
    public required string Status { get; set; }

    // Brukes kun hvis status er "blocked"
    public int? BlockingUserId { get; set; }
}

// Eventuell annen DTO for FriendRequest
public class UserRelationDto
{
    public Guid Id { get; set; }
    //public Guid FromUserId { get; set; }
    //public Guid ToUserId { get; set; }
    public int FromUserId { get; set; }
    public int ToUserId { get; set; }
    
    public string Status { get; set; } = string.Empty;
}
public class UpdateUserRelationStatusDto
{
    public required string Status { get; set; }  // For eksempel "accepted" eller "declined"
    public int? FromUserId { get; set; } //brukes ved blokkering
    public int? ToUserId { get; set; } //brukes ved blokkering

}
