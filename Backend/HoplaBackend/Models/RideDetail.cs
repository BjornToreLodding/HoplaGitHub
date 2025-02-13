using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace MyApp.Models;

public partial class RideDetail
{
    [Key]
    public int Id { get; set; }

    public string? CoordinateslistShort { get; set; }

    public string? CoordinatesAll { get; set; }

    public byte[]? Picturethumb { get; set; }

    public byte[]? Picturefull { get; set; }


    //public virtual Ride Ride { get; set; } = null!;
}
