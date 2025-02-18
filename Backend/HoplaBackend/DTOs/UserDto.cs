using System.ComponentModel.DataAnnotations;

namespace MyApp.DTOs;

public class CreateUserDto
{
    public string? Name { get; set; }
    public string? Alias { get; set; }
    public string? Email { get; set; }
    public string? PasswordHash { get; set; }

}

public class UpdateUserDto
{
    [Key] //Denne er unødvendig tror jeg, fordi Id blir automatisk som PK hvis ikke man bruker [key] et annet sted.
    public Guid Id { get; set; } 
    //public guid Id { get; set; } //Bruker kanskje denne senere
    public string? Name { get; set; }
    public string? Alias { get; set; }

    public string? Email { get; set; }

    public string? PasswordHash { get; set; }
    public string? ProfilePictureUrl { get; set; } // Direkte lagring av profilbilde-URL

    public bool Admin { get; set; } = false;
    public bool Premium { get; set; } = false;
    public bool VerifiedTrail { get; set; } = false;
}