namespace HoplaBackend.DTOs;

public class ListSettings
{
    public required string Key { get; set; }  // Alltid satt
    public required string Value { get; set; }  // Alltid satt
    public required string Type { get; set; }  // Alltid satt
}

public class UpdateSettingRequest
{
    public string? Key { get; set; }
    public required string Value { get; set; } 
    public string? Type { get; set; }
}

