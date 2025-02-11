using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;

//Fjerne partial?
public partial class FriendRequest
{
    //[Key]
    public Guid Id { get; set; }
    //Guid

    public required int FromUserId { get; set; }

    public required int ToUserId { get; set; }

    public required string Status { get; set; } //enum Default "pending"

    [Required]
    public DateTime? CreatedAt { get; set; } = DateTime.UtcNow;
/*
    [ForeignKey("FromUserId")]
    public virtual User? FromUser { get; set; }
    
    [ForeignKey("ToUserId")]
    public virtual User? ToUser { get; set; }

*/
}
