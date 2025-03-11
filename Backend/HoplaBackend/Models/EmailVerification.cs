using System;
using System.ComponentModel.DataAnnotations;

namespace HoplaBackend;
public class EmailVerification
{
    [Key]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Required, EmailAddress]
    public string Email { get; set; }

    [Required]
    public string Token { get; set; }

    public DateTime ExpiryDate { get; set; }
    public bool IsUsed { get; set; } = false;
}
