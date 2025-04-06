using System.Collections.Generic;
using HoplaBackend.Models; // Husk å erstatte med ditt faktiske namespace

public static class SystemSettingMock
{
    public static List<SystemSetting> SetDefaultSettingsMock()
    {
        return new List<SystemSetting>
         {
            new SystemSetting { Key = "activatePremium", Value = "false", Type = "bool" },
            new SystemSetting { Key = "activateVerifiedAddNewTrailRoute", Value = "false", Type = "bool" },
            new SystemSetting { Key = "DefaultPageSize", Value = "20", Type = "int" },
            new SystemSetting { Key = "DefaultNextSize", Value = "20", Type = "int" },
            new SystemSetting { Key = "SearchResponseValue", Value = "20", Type = "int" },
            new SystemSetting { Key = "EnableLogging", Value = "false", Type = "bool" },
            new SystemSetting { Key = "LogLevel", Value = "Info", Type = "string" },
            new SystemSetting { Key = "TokenExpireDays", Value = "7", Type = "int" },
            new SystemSetting { Key = "TokenExpireDaysAdmin", Value = "1", Type = "int" },
            new SystemSetting { Key = "AutoLogoutMinsAdmin", Value = "60", Type = "int" },
            new SystemSetting { Key = "AllowUserRegistration", Value = "true", Type = "bool" },
            new SystemSetting { Key = "MaxLoginAttemptBeforeDelay", Value = "2", Type = "int" },
            new SystemSetting { Key = "DelayLoginAttempt", Value = "5", Type = "int" },
            new SystemSetting { Key = "DelayMaxLoginAttempts", Value = "10", Type = "int" },
            new SystemSetting { Key = "EnableAPI", Value = "true", Type = "bool" },
            new SystemSetting { Key = "Subscription-Price", Value = "69.00", Type = "float" },

            // 🖼️ Bildeinnstillinger i CSV-format
            new SystemSetting { Key = "UserProfilePictureList", Value = "64,64,crop", Type = "csv" },
            new SystemSetting { Key = "UserProfilePictureSelect", Value = "200,200,crop", Type = "csv" },
            new SystemSetting { Key = "UserProfileUserHikes", Value = "900,350,crop", Type = "csv" },
            new SystemSetting { Key = "HorsePictureList", Value = "64,64,crop", Type = "csv" },
            new SystemSetting { Key = "HorsePictureSelect", Value = "200,200,crop", Type = "csv" },
            new SystemSetting { Key = "TrailPicture", Value = "1020,420,crop", Type = "csv" }, // fyll heller enn crop
            //new SystemSetting { Key = "TrailPicture", Value = "400,300,crop", Type = "csv" }, // fyll heller enn crop
            new SystemSetting { Key = "TrailReviewPicture", Value = "400,300,crop", Type = "csv" },
            new SystemSetting { Key = "StablePicture", Value = "300,300,crop", Type = "csv" },
            new SystemSetting { Key = "StableMessagePicture", Value = "300,300,crop", Type = "csv" },
            new SystemSetting { Key = "UserHikePicture", Value = "400,300,crop", Type = "csv" },
            new SystemSetting { Key = "FeedPicture", Value = "538,391,crop", Type = "csv" },
            new SystemSetting { Key = "UserProfileWebPage", Value = "64,64,crop", Type = "csv" },
            
            //new SystemSetting { Key = "MaxAPICallsPerMinute", Value = "100", Type = "int" },
            //new SystemSetting { Key = "CacheDuration", Value = "300", Type = "int" },
            //new SystemSetting { Key = "MaintenanceMode", Value = "false", Type = "bool" }
        };
    }
}
