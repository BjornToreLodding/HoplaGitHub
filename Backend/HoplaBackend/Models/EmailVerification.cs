using System;
using System.ComponentModel.DataAnnotations;

namespace HoplaBackend;
public class EmailVerification
{
    [Key]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Required, EmailAddress]
    public string Email { get; set; }
    public string PasswordHash { get; set; }
    //Password  and Email will be transfered Users-table when registration is completed.

    [Required]
    public string Token { get; set; }

    public DateTime ExpiryDate { get; set; }
    public bool IsUsed { get; set; } = false;
}
