using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;


public partial class Message
{
    public Guid Id { get; set; }

    [ForeignKey("Sender")]
    public Guid SUserId { get; set; }
    public User Sender { get; set; } = null!;

    [ForeignKey("Receiver")]
    public Guid RUserId { get; set; }
    public User Receiver { get; set; } = null!;
    public string MessageText { get; set; } = null!;
    public string? PictureUrl { get; set; }


    public DateTime? CreatedAt { get; set; } = DateTime.UtcNow;


}
