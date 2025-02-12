// Plassering: Models/User.cs
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;

//[Table("users")] // Forteller EF Core at denne modellen er koblet til `users`-tabellen i PostgreSQL
public class User
{
    [Key] //Denne er unødvendig tror jeg, fordi Id blir automatisk som PK hvis ikke man bruker [key] et annet sted.
    public int Id { get; set; } 
    //public guid Id { get; set; } //Bruker kanskje denne senere
    public string? Alias { get; set; }
    public string? Name { get; set; }

    public string? Email { get; set; }

    public string? PasswordHash { get; set; }

    public bool Admin { get; set; } = false;
    public bool Premium { get; set; } = false;
    public bool VerifiedTrail { get; set; } = false;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    
    //[Required]
    public DateTime? Dob { get; set; } = DateTime.UtcNow;

    public ICollection<Horse> Horses { get; set; } = new List<Horse>();

    //public ICollection<Friendrequest>? FriendRequests {get;set;}
}

/*using System;
using System.Collections.Generic;
using HoplaBackend.Models;

namespace MyApp.Models;

public partial class User
{
    public int UserId { get; set; }

    public string Name { get; set; } = null!;

    public string Email { get; set; } = null!;

    public string PasswordHash { get; set; } = null!;

    public DateTime? CreatedAt { get; set; }

    public virtual ICollection<Friend> FriendFriendNavigations { get; set; } = new List<Friend>();

    public virtual ICollection<Friend> FriendUsers { get; set; } = new List<Friend>();

    public virtual ICollection<Friendrequest> FriendrequestFromUsers { get; set; } = new List<Friendrequest>();

    public virtual ICollection<Friendrequest> FriendrequestToUsers { get; set; } = new List<Friendrequest>();

    public virtual ICollection<Horse> Horses { get; set; } = new List<Horse>();

    public virtual ICollection<Message> MessageRUsers { get; set; } = new List<Message>();

    public virtual ICollection<Message> MessageSUsers { get; set; } = new List<Message>();

    public virtual ICollection<Ride> Rides { get; set; } = new List<Ride>();

    public virtual ICollection<Stablemessage> Stablemessages { get; set; } = new List<Stablemessage>();

    public virtual ICollection<Stable> Stables { get; set; } = new List<Stable>();
}
*/