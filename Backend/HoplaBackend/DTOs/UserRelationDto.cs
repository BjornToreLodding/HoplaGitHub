namespace HoplaBackend.DTOs;

public class CreateUserRelationDto
{
    //public required Guid FromUserId { get; set; }
    //public required Guid ToUserId { get; set; }
    public required Guid FromUserId { get; set; }
    public required Guid ToUserId { get; set; }
    public required string Status { get; set; }

    // Brukes kun hvis status er "blocked"
    public Guid? BlockingUserId { get; set; }
}

// Eventuell annen DTO for FriendRequest
public class UserRelationDto
{
    public Guid Id { get; set; }
    //public Guid FromUserId { get; set; }
    //public Guid ToUserId { get; set; }
    public Guid FromUserId { get; set; }
    public Guid ToUserId { get; set; }
    
    public string Status { get; set; } = string.Empty;
}
public class UpdateUserRelationStatusDto
{
    public required string Status { get; set; }  // For eksempel "accepted" eller "declined"
    public Guid? FromUserId { get; set; } //brukes ved blokkering
    public Guid? ToUserId { get; set; } //brukes ved blokkering

}
