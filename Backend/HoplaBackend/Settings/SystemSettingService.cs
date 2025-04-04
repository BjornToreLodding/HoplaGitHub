using System;
using System.Linq;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models;
using HoplaBackend.Data;
using HoplaBackend.Helpers; // Husk å endre til riktig namespace

public class SystemSettingService
{
    private readonly AppDbContext _context;

    public SystemSettingService(AppDbContext context)
    {
        _context = context;
    }

    /// <summary>
    /// Henter en systeminnstilling basert på nøkkel.
    /// </summary>
    public SystemSetting GetSetting(string key)
    {
        return _context.SystemSettings.FirstOrDefault(s => s.Key == key);
    }

    /// <summary>
    /// Henter verdien av en systeminnstilling i riktig datatype.
    /// </summary>
    public T GetValue<T>(string key)
    {
        var setting = GetSetting(key);
        if (setting == null) throw new Exception($"Innstillingen '{key}' ble ikke funnet.");

        return ConvertValue<T>(setting.Value, setting.Type);
    }

    /// <summary>
    /// Oppdaterer en systeminnstilling.
    /// </summary>
    public void UpdateSetting(string key, string value)
    {
        var setting = GetSetting(key);
        if (setting == null) throw new Exception($"Innstillingen '{key}' ble ikke funnet.");

        setting.Value = value;
        _context.SaveChanges();
    }

    /// <summary>
    /// Konverterer en verdi til riktig type.
    /// </summary>
    private T ConvertValue<T>(string value, string type)
    {
        try
        {
            return type.ToLower() switch
            {
                "int" => (T)(object)int.Parse(value),
                "bool" => (T)(object)bool.Parse(value),
                "string" => (T)(object)value,
                _ => throw new Exception($"Ukjent datatype '{type}' for setting.")
            };
        }
        catch (Exception ex)
        {
            throw new Exception($"Feil ved konvertering av '{value}' til '{typeof(T).Name}': {ex.Message}");
        }
    }

    /// <summary>
    /// Refresher SystemSettingsCache fra databasen.
    /// </summary>
    public async Task RefreshCacheAsync()
    {
        await SystemSettingsCache.ReloadAsync(_context);
    }
}

