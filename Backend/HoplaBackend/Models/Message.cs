using System;
using System.Collections.Generic;

namespace MyApp.Models;


public partial class Message
{
    public Guid Id { get; set; }

    public int SUserId { get; set; }

    public int RUserId { get; set; }

    //Det burde istedenfor vært slik, men jeg vet ikke hvordan jeg gjør det når det er 2 users fra samme tabell?
    //public int UserId { get; set; } //Sender
    //[ForeignKey("UserId")]
    //public required User User { get; set; }  // Navigasjonsegenskap
    //public int UserId { get; set; } //Mottaker
    //[ForeignKey("UserId")]
    //public required User User { get; set; }  // Navigasjonsegenskap

    public string MessageText { get; set; } = null!;

    public DateTime? SentAt { get; set; } = DateTime.UtcNow;

    //public virtual User RUser { get; set; } = null!;
    //public virtual User SUser { get; set; } = null!;

}
