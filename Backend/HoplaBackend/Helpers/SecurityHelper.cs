using System;
using System.Text.RegularExpressions;

namespace HoplaBackend.Helpers;
public class PasswordValidator
{
    public static bool IsValidPassword(string password)
    {
        if (string.IsNullOrWhiteSpace(password))
            return false;

        // Regex: 
        // ^(?=.*[a-z])       -> Minst én liten bokstav
        // (?=.*[A-Z])        -> Minst én stor bokstav
        // (?=.*\d)           -> Minst ett tall
        // (?=.*[\W_])        -> Minst ett spesialtegn (ikke bokstav/tall)
        // .{9,}$             -> Minst 9 tegn langt
        string pattern = @"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{9,}$";

        return Regex.IsMatch(password, pattern);
    }
}

