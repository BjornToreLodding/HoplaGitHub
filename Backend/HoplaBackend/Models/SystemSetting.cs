public class SystemSetting
{
    public int Id { get; set; } // Vurderer å droppe denne og heller bruke Key som PK, siden den allikevel er unik.
    public required string Key { get; set; }
    public required string Value { get; set; }
    public required string Type { get; set; }
}