using System;
using System.Collections.Generic;

namespace MyApp.Models;

public partial class RideDetail
{
    public int Id { get; set; }

    public string? CoordinateslistShort { get; set; }

    public string? CoordinatesAll { get; set; }

    public byte[]? Picturethumb { get; set; }

    public byte[]? Picturefull { get; set; }


    //public virtual Ride Ride { get; set; } = null!;
}
