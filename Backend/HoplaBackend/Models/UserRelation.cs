﻿using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;

//Fjerne partial?
public partial class UserRelation
{
    //[Key]
    public Guid Id { get; set; }
    //Guid

    // Fremmednøkler
    public required Guid FromUserId { get; set; }
    public User FromUser { get; set; } = null!; //navigasjon til Users tabellen
    public required Guid ToUserId { get; set; }
    public User ToUser { get; set; } = null!;  // Navigasjons-egenskaper (for EF-relasjoner)


    public required string Status { get; set; } = "pending";  //enum("pending", "accepted", "declined") Default "pending"
    public int LikesCount { get ; set ; } = 0 ;
    public int CommentsCount { get ; set ; } = 0;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    /*
[ForeignKey("FromUserId")]
public virtual User? FromUser { get; set; }

[ForeignKey("ToUserId")]
public virtual User? ToUser { get; set; }

*/
}
