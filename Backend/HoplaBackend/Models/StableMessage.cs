using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
//using HoplaBackend.Models;

namespace MyApp.Models;

public class StableMessage
{
    [Key]
    public int Id { get; set; }
    public int UserId { get; set; }

    [ForeignKey("UserId")]
    public User? User { get; set; }

    public int StableId { get; set; }

    [ForeignKey("StableId")]
    public Stable? Stable { get; set; }
    public string Message { get; set; } = null!;

    public DateTime SentAt { get; set; } = DateTime.UtcNow;

    //public int StablemessageId { get; set; }

    //public virtual Stable Stable { get; set; } = null!;

    //public virtual User User { get; set; } = null!;
}
