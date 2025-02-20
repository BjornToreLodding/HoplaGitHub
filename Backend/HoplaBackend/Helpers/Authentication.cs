using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using BCrypt.Net;
using HoplaBackend.Models;

namespace HoplaBackend.Helpers;
public static class Authentication
{
    private static readonly string SecretKey = "hemmelig_nøkkel123"; // Bør hentes fra config

    public static string GenerateJwtToken(User user)
    {
        var tokenHandler = new JwtSecurityTokenHandler();
        var key = Encoding.ASCII.GetBytes(SecretKey);

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(new[] { new Claim(ClaimTypes.Name, user.Email) }),
            Expires = DateTime.UtcNow.AddHours(1),
            SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
        };

        var token = tokenHandler.CreateToken(tokenDescriptor);
        return tokenHandler.WriteToken(token);
    }
    /*
    //
    //
    //
    var user = new User
    {
        Username = "bruker123",
        HashedPassword = HashPassword("MittSterkePassord123")
    };

    dbContext.Users.Add(user);
    dbContext.SaveChanges();

    */

    // Funksjon for å verifisere passordet

    public static string HashPassword(string password)
    {
        return BCrypt.Net.BCrypt.HashPassword(password);
    }

    public static bool VerifyPassword(string password, string hashedPassword)
    {
        return BCrypt.Net.BCrypt.Verify(password, hashedPassword);
    }


    /*
    //
    //  Eksempel på bruk av funksjonen ovenfor
    //
    var user = dbContext.Users.FirstOrDefault(u => u.Username == "bruker123");

    if (user != null && VerifyPassword("MittSterkePassord123", user.HashedPassword))
    {
        Console.WriteLine("Innlogging vellykket!");
    }
    else
    {
        Console.WriteLine("Feil brukernavn eller passord.");
    }
    */


    /*
    //
    //  Eksempel på program som bruker funksjonene.
    //

    static void Main()
    {
        // Simulerer brukerens passordinput
        string password = "MittSterkePassord123";

        // 1. Hash passordet
        string hashedPassword = HashPassword(password);
        Console.WriteLine($"Hashed Password: {hashedPassword}");

        // 2. Verifiser passordet (ved innlogging)
        bool isMatch = VerifyPassword(password, hashedPassword);
        Console.WriteLine($"Passordet er korrekt: {isMatch}");
    }
    */


}
