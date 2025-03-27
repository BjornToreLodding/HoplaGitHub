// Enum for filtertype
public enum TrailFilterType
{
    Bool,
    Enum,
    MultiEnum,
    Int
}


// Modell for TrailFilterDefinition
public class TrailFilterDefinition
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string DisplayName { get; set; } = string.Empty;
    public TrailFilterType Type { get; set; }
    public string? DefaultValue { get; set; }
    public string? OptionsJson { get; set; }
    public bool IsActive { get; set; } = true;
    public int Order { get; set; }
}
    
/*
public class TrailFilterDefinition
{
    public Guid Id { get; set; } = Guid.NewGuid(); // Bruker Guid
    public string Name { get; set; } // Filter-navn (f.eks. "Cart", "RiverBridge")
    public string DataType { get; set; } // "bool", "string", "double"
}
*/