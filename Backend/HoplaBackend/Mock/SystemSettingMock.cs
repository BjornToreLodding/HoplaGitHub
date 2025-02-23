using System.Collections.Generic;
using HoplaBackend.Models; // Husk å erstatte med ditt faktiske namespace

public static class SystemSettingMock
{
    public static List<SystemSetting> SetDefaultSettingsMock()
    {
        return new List<SystemSetting>
         {
            new SystemSetting { Key = "activatePremium", Value = "false", Type = "bool" }, // Gir begrensede funksjoner til ikkebetalende medlemmer.
            new SystemSetting { Key = "activateVerifiedAddNewTrailRoute", Value = "false", Type = "bool" }, // Lar kun brukere som er verified legge inn nye Turer.
            new SystemSetting { Key = "DefaultPageSize", Value = "20", Type = "int" }, // Hvor mange som vises hvis ingenting er oppgitt som $limit
            new SystemSetting { Key = "DefaultNextSize", Value = "20", Type = "Int"}, // Hvor mange som lastes hvis $offset ikke er oppgitt, f.eks laster videre lasting av meldinger aktiveres av js
            new SystemSetting { Key = "SearchResponseValue", Value = "20" , Type = "int" }, // Give response if matches are equal or less than this value
            new SystemSetting { Key = "EnableLogging", Value = "false", Type = "bool" },
            new SystemSetting { Key = "LogLevel", Value = "Info", Type = "string" },
            new SystemSetting { Key = "TokenExpireDays", Value = "7", Type = "int" }, // altså når man må logge inn på nytt.
            
            new SystemSetting { Key = "TokenExpireDaysAdmin", Value = "1", Type = "int" }, // dager

            new SystemSetting { Key = "AutoLogoutMinsAdmin", Value = "60", Type = "int" }, // minutter
            new SystemSetting { Key = "AllowUserRegistration", Value = "true", Type = "bool" }, // Må være aktivert for at ny medlemmer skal kunne registreres.          
            new SystemSetting { Key = "MaxLoginAttemptBeforeDelay", Value = "2", Type = "int" }, // Etter Value forsøk på å logge inn, må man vente oppgitt verdi før man kan gjøre neste login
            new SystemSetting { Key = "DelayLoginAttemp", Value = "5", Type = "int" }, // oppgitt i sekunder
            new SystemSetting { Key = "DelayMaxLoginAttemps", Value = "10", Type = "int" }, // oppgitt i sekunder

            // Hvis aktivert svarer kun Admin/-endpoints.
            new SystemSetting { Key = "EnableAPI", Value = "true", Type = "bool" },

            new SystemSetting { Key = "UserProfilePictureList-height", Value = "100", Type = "int" }, // oppgitt i pixler
            new SystemSetting { Key = "UserProfilePictureList-width", Value = "100", Type = "int" }, // oppgitt i pixler
            new SystemSetting { Key = "UserProfilePictureSelect-height", Value = "400", Type = "int" }, // oppgitt i pixler
            new SystemSetting { Key = "UserProfilePictureSelect-height", Value = "400", Type = "int" }, // oppgitt i pixler
            new SystemSetting { Key = "HorsepictureList-height", Value = "100", Type = "int" }, // oppgitt i pixler
            new SystemSetting { Key = "Zettings1", Value = "10", Type = "int" }, // oppgitt i ?
            new SystemSetting { Key = "Zettings2", Value = "20", Type = "int" }, // oppgitt i ?
            new SystemSetting { Key = "Zettings3", Value = "30", Type = "int" }, // oppgitt i ?
            
            //new SystemSetting { Key = "MaxAPICallsPerMinute", Value = "100", Type = "int" },
            //new SystemSetting { Key = "CacheDuration", Value = "300", Type = "int" },
            //new SystemSetting { Key = "MaintenanceMode", Value = "false", Type = "bool" }
        };
    }
}
