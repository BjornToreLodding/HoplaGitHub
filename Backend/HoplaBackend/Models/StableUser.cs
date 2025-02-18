using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;

public class StableUser
{
    [Key]
    public Guid Id { get; set; }

    public Guid UserId { get; set; }

    [ForeignKey("UserId")]
    public required User User { get; set; }

    public Guid StableId { get; set; }

    [ForeignKey("StableId")]
    public required Stable Stable { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public bool IsOwner { get; set; } = false;
    public bool IsAdmin { get; set; } = false;
    public bool IsModerator { get; set; } = false;
    public bool NotifyNewMessage { get; set; } = true;
}
