using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;

public class StableUser
{
    [Key]
    public int Id { get; set; }

    public int UserId { get; set; }

    [ForeignKey("UserId")]
    public required User User { get; set; }

    public int StableId { get; set; }

    [ForeignKey("StableId")]
    public required Stable Stable { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public bool StableAdmin { get; set; } = false;
    public bool StableModerator { get; set; } = false;
    public bool NotifyNewMessage { get; set; } = true;
}
