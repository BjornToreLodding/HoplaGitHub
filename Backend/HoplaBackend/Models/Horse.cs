using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;
using HoplaBackend.Interfaces;
using Microsoft.AspNetCore.Mvc;

//using HoplaBackend.Models;

namespace HoplaBackend.Models;

public partial class Horse : IEntityWithUser
{
    [Key]
    public Guid Id { get; set; }

    [Required]
    public string Name { get; set; } = null!; // Name is required, must be set on creation

    [Required]
    public Guid UserId { get; set; } // Foreign key to the owning user

    [ForeignKey(nameof(UserId))]
    [JsonIgnore] // Avoid self-referencing loop in JSON serialization
    public User? User { get; set; } // Navigation property (optional)

    public string? Breed { get; set; }

    public string? PictureUrl { get; set; }

    public DateOnly? Dob { get; set; } // Date of birth, optional

    public bool IsDeleted { get; set; } = false; // Soft-delete flag, default false

    public int LikesCount { get; set; } = 0;

    public int CommentsCount { get; set; } = 0;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}