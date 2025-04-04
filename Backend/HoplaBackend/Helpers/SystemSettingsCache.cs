using HoplaBackend.Data;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Helpers;
public static class SystemSettingsCache
{
    private static Dictionary<string, SystemSetting> _settings = new();
    private static bool _initialized = false;

    public static async Task InitializeAsync(AppDbContext context)
    {
        if (_initialized) return; // Bare init én gang

        await ReloadAsync(context); // ✅ Kall Reload for å initialisere
        _initialized = true;
    }

    public static async Task ReloadAsync(AppDbContext context)
    {
        _settings = await context.SystemSettings.ToDictionaryAsync(s => s.Key, s => s);
    }

    public static string? GetValue(string key)
    {
        if (_settings.TryGetValue(key, out var setting))
        {
            return setting.Value;
        }
        return null;
    }

    public static int? GetValueInt(string key)
    {
        var value = GetValue(key);
        if (int.TryParse(value, out int result))
            return result;
        return null;
    }

    public static void UpdateSetting(SystemSetting updatedSetting)
    {
        _settings[updatedSetting.Key] = updatedSetting;
    }
    public static (int width, int height, string fit) GetPictureSettings(string context)
    {
        var rawValue = GetValue(context);
        if (string.IsNullOrWhiteSpace(rawValue))
        {
            return (200, 200, "crop"); // fallback
        }

        var parts = rawValue.Split(',');
        if (parts.Length >= 2)
        {
            int width = int.TryParse(parts[0], out var w) ? w : 200;
            int height = int.TryParse(parts[1], out var h) ? h : 200;
            string fit = parts.Length >= 3 ? parts[2] : "crop";

            return (width, height, fit);
        }

        return (200, 200, "crop");
    }
}
