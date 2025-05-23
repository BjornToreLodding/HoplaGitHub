﻿using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using HoplaBackend.Interfaces;
//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public class StableMessage : IEntityWithUser
{
    [Key]
    public Guid Id { get; set; }
    public Guid UserId { get; set; }

    [ForeignKey("UserId")]
    public User User { get; set; } = null!;

    public Guid StableId { get; set; }

    [ForeignKey("StableId")]
    public Stable Stable { get; set; } = null!;
    public string MessageText { get; set; } = null!;
    public int LikesCount { get ; set ; } = 0 ;
    public int CommentsCount { get ; set ; } = 0;


    public DateTime SentAt { get; set; } = DateTime.UtcNow;

    //public int StablemessageId { get; set; }

    //public virtual Stable Stable { get; set; } = null!;

    //public virtual User User { get; set; } = null!;
}
