using System.ComponentModel.DataAnnotations;

namespace HoplaBackend.DTOs;


// Vurdere å legge denne i ny DTO, f.eks AuthenticationDto.cs
public class LoginRequest
{
    public string? Email { get; set; }
    public string? Password { get; set; }
}
public class LoginTest
{
    public Guid? Id { get; set; }
}
// Vurdere å legge denne i ny DTO, f.eks AuthenticationDto.cs
public class RegisterRequest
{
    public string Email { get; set; }
    public string Password { get; set; }
}
// Denne er lik som som den ovenfor. Vurder senere.
public class ChangeEmailRequest 
{
    public string NewEmail { get; set;}
    public string Password { get; set;}
}
public class EmailVerificationRequest
{
    public string Token { get; set; }
}
public class ChangePasswordRequest
{
    public string OldPassword { get; set; }
    public string NewPassword { get; set; }
    public string ConfirmPassword { get; set; }
}
public class RequestPasswordReset
{
    public string Email { get; set; }
}
public class ResetPasswordRequest
{
    public string Token { get; set; }
    public string NewPassword { get; set; }
}

public class UserDto
{
    public string Email { get; set; }
    public string FullName { get; set; }
}

public class CreateUserDto
{
    public string? Name { get; set; }
    public string? Alias { get; set; }
    public string? Email { get; set; }
    public string? PasswordHash { get; set; }

}

public class UpdateUserDto
{
    //public guid Id { get; set; } //Bruker kanskje denne senere
    public string? Name { get; set; }
    public string? Alias { get; set; }
    public string? Telephone { get; set; }

    //public string? PictureUrl { get; set; } // Direkte lagring av profilbilde-URL

    public string? Description { get ; set; }
    public int? Year { get; set; }
    public int? Month { get; set; }
    public int? Day { get; set; }

    //public string? PasswordHash { get; set; }
    //public string? ProfilePictureUrl { get; set; } // Direkte lagring av profilbilde-URL
}
/*
    public bool Admin { get; set; } = false;
    public bool Premium { get; set; } = false;
    public bool VerifiedTrail { get; set; } = false;
}
*/
public class DeleteRequest
{
    public string Password { get; set; }
}