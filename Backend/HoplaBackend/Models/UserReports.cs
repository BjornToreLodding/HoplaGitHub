using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MyApp.Models;
public partial class UserReports // tickets
{
    //[Key]
    public Guid Id { get; set; }
    //Guid

    // Ble plutselig litt usikker her angående hvordan man gjør det med FK når det 2 forskjellige brukere skal hentes fra Users-tabellen.
    public required int CreatedUserId { get; set; }
    [ForeignKey("CreatedByUserId")]
    public User CreatedByUserId { get; set; } = null!; //navigasjon til Users tabellen
    public int? StableId { get; set; } // Hvis henvendelsen gjelder stall
    public Stable Stable { get; set; } = null!;

    public Guid? StableMessageId { get; set; } = null!;
    public StableMessage StableMessage { get; set; } = null!;
    
    public int? ReportedUserId { get; set; } // Hvis henvendelsen gjelder en bruker
    [ForeignKey("ReportUserId")]
    public User ReportUserId { get; set; } = null!;

    public required string Status { get; set; } = "pending";  //enum("pending", "accepted", "declined") Default "pending"

    [Required]
    public DateTime? CreatedAt { get; set; } = DateTime.UtcNow;
}
