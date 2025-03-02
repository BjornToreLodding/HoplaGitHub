using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace HoplaBackend.Models;

public class Stable
{
    [Key]
    public Guid Id { get; set; }

    public string Name { get; set; } = null!;

    public string? Location { get; set; }
    public bool PrivateGroup { get; set; } =false; // krever godkjenning av admin/moderator før man medlem
    public bool ModeratedMessages { get; set; } = false; // melding sendt til stall, må godkjennes av admin
    public bool SecretGroup { get; set; } = false; //stallen vises ikke på søk.
    public int LikesCount { get ; set ; } = 0 ;
    public int CommentsCount { get ; set ; } = 0;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    //public virtual ICollection<Stablemessage> Stablemessages { get; set; } = new List<Stablemessage>();

    //public virtual ICollection<User> Users { get; set; } = new List<User>();
}
