public class TrailFilterDefinitionDto
{
    public string DisplayName { get; set; }
    public string Type { get; set; } // string input, f.eks. \"enum\"
    public List<string>? Alternatives { get; set; } // optional
    public object? DefaultValue { get; set; } // kan være string eller liste
    public bool IsActive { get; set; }
}
