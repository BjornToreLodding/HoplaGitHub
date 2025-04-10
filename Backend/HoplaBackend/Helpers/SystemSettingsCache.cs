using HoplaBackend.Data;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Helpers;
/// <summary>
/// Caches system settings to avoid frequent database lookups.
/// </summary>
public static class SystemSettingsCache
{
    private static Dictionary<string, SystemSetting> _settings = new();
    private static bool _initialized = false;

    /// <summary>
    /// Initializes the settings cache if not already initialized.
    /// </summary>
    public static async Task InitializeAsync(AppDbContext context)
    {
        if (_initialized) return; // Only initialize once

        await ReloadAsync(context);
        _initialized = true;
    }

    /// <summary>
    /// Reloads all system settings from the database.
    /// </summary>
    public static async Task ReloadAsync(AppDbContext context)
    {
        _settings = await context.SystemSettings.ToDictionaryAsync(s => s.Key, s => s);
    }

    /// <summary>
    /// Gets the raw value of a setting by its key.
    /// Returns null if not found.
    /// </summary>
    public static string? GetValue(string key)
    {
        if (_settings.TryGetValue(key, out var setting))
        {
            return setting.Value;
        }
        return null;
    }

    /// <summary>
    /// Gets the integer value of a setting by its key.
    /// Returns null if not found or not a valid integer.
    /// </summary>
    public static int? GetValueInt(string key)
    {
        var value = GetValue(key);
        if (int.TryParse(value, out int result))
        {
            return result;
        }
        return null;
    }

    /// <summary>
    /// Gets the base URL for pictures.
    /// Returns a default value if not configured.
    /// </summary>
    public static string GetPictureBaseUrl()
    {
        if (_settings.TryGetValue("PictureBaseUrl", out var setting))
        {
            return setting.Value ?? "https://hopla.imgix.net/";
        }

        return "https://hopla.imgix.net/"; // Default fallback
    }

    /// <summary>
    /// Updates a setting in the cache.
    /// </summary>
    public static void UpdateSetting(SystemSetting updatedSetting)
    {
        _settings[updatedSetting.Key] = updatedSetting;
    }

   /// <summary>
    /// Gets picture resizing settings (width, height, fit, fallback) for a specific context.
    /// Returns default values if not configured properly.
    /// </summary>
    public static (int width, int height, string fit, string fallback) GetPictureSettings(string context)
    {
        var rawValue = GetValue(context);
        if (string.IsNullOrWhiteSpace(rawValue))
        {
            // Default fallback settings
            return (200, 200, "crop", "fallback.jpg");
        }

        var parts = rawValue.Split(',');
        if (parts.Length >= 2)
        {
            int width = int.TryParse(parts[0], out var w) ? w : 200;
            int height = int.TryParse(parts[1], out var h) ? h : 200;
            string fit = parts.Length >= 3 ? parts[2] : "crop";
            string fallback = parts.Length >= 4 ? parts[3] : "fallback.jpg"; // If missing, use default

            return (width, height, fit, fallback);
        }

        // If parts are missing or invalid
        return (200, 200, "crop", "fallback.jpg");
    }

}