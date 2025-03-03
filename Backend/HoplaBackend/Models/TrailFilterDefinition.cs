public class TrailFilterDefinition
{
    public Guid Id { get; set; } = Guid.NewGuid(); // Bruker Guid
    public string Name { get; set; } // Filter-navn (f.eks. "Cart", "RiverBridge")
    public string DataType { get; set; } // "bool", "string", "double"
}
