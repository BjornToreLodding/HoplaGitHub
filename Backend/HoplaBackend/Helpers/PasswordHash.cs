using System;
using BCrypt.Net;

namespace HoplaBackend.Helpers;
public static class PWHelper
{
    // Funksjon for å hashe passord
    static string HashPassword(string password)
    {
        return BCrypt.Net.BCrypt.HashPassword(password, workFactor: 12); // workFactor bestemmer sikkerheten
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
    static bool VerifyPassword(string password, string hashedPassword)
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
