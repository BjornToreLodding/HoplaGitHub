using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using HoplaBackend.Models;
using Microsoft.Extensions.Configuration;
using BCrypt.Net; 

namespace HoplaBackend.Helpers;
public class Authentication
{
    private readonly IConfiguration _configuration;

    public Authentication(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public string GenerateJwtToken(User user)
    {
        var tokenHandler = new JwtSecurityTokenHandler();
        var secretKey = _configuration["Jwt:Key"];

        if (string.IsNullOrEmpty(secretKey) || secretKey.Length < 32)
        {
            throw new Exception("🚨 Jwt:Key er for kort eller mangler!");
        }

        var key = Encoding.UTF8.GetBytes(secretKey);

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(new[]
            {
                new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
                new Claim(ClaimTypes.Email, user.Email)
            }),
            Expires = DateTime.UtcNow.AddDays(7),
            SigningCredentials = new SigningCredentials(
                new SymmetricSecurityKey(key),
                SecurityAlgorithms.HmacSha256Signature
            )
        };

        var token = tokenHandler.CreateToken(tokenDescriptor);
        return tokenHandler.WriteToken(token);
    }

    // ✅ Beholder `static` her for enkel tilgang uten instans
    public static string HashPassword(string password)
    {
        return BCrypt.Net.BCrypt.HashPassword(password);
    }

    public static bool VerifyPassword(string password, string hashedPassword)
    {
        return BCrypt.Net.BCrypt.Verify(password, hashedPassword);
    }
}



    /*
    var user = new User
    {
        Username = "bruker123",
        HashedPassword = HashPassword("MittSterkePassord123")
    };

    dbContext.Users.Add(user);
    dbContext.SaveChanges();

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



