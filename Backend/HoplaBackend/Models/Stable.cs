using System;
using System.Collections.Generic;

namespace MyApp.Models;

public class Stable
{
    public int Id { get; set; }

    public string Name { get; set; } = null!;

    public string? Location { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    //public virtual ICollection<Stablemessage> Stablemessages { get; set; } = new List<Stablemessage>();

    //public virtual ICollection<User> Users { get; set; } = new List<User>();
}
