using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace MyApp.Models;

public class Stable
{
    [Key]
    public int Id { get; set; }

    public string Name { get; set; } = null!;

    public string? Location { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    //public virtual ICollection<Stablemessage> Stablemessages { get; set; } = new List<Stablemessage>();

    //public virtual ICollection<User> Users { get; set; } = new List<User>();
}
