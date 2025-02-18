using System.Linq;
using System;
using System.Collections.Generic;
using MyApp.Models;

public static class HorseMock 
{
    public static List<Horse> CreateHorsesMock(List<User> existingUsers)
    {
        return new List<Horse> 
        {
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780001"), Name = "Flodhest", UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780001")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780002"), Name = "Enhjørning", UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780002")) }, //rød strek under First
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780003"), Name = "Zebra", UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780003")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780004"), Name = "Sjøhest", UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780002")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780005"), Name = "Gyngehest", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780006"), Name = "Unikorn", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780007"), Name = "Blue Layla", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780008"), Name = "Pinkypai", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780009"), Name = "Ponni", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780010"), Name = "Twilight Sparkle", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780011"), Name = "Rainbow Dash", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780012"), Name = "Fluttershy", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780013"), Name = "Applejack", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780014"), Name = "Rarity", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780015"), Name = "Æplekjækk", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780016"), Name = "Princess celestia", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780017"), Name = "Princess Luna", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780018"), Name = "Kronprins Durek", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780019"), Name = "Princess Marta", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780020"), Name = "Mette Marit", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780021"), Name = "Marius O. pat", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780022"), Name = "Postman Pat", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780023"), Name = "Psyko Pat", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780024"), Name = "Mona Lysa", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780025"), Name = "Fola Blakken", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780026"), Name = "Esel", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780027"), Name = "Kengeru Galopp", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780028"), Name = "Snøfte Hirre", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780029"), Name = "Fritz Viritsekk", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780030"), Name = "Shreddlord", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780031"), Name = "Brøyte pløyern", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780032"), Name = "Glise Glad", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780033"), Name = "Foto Gen", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780034"), Name = "Marius Høypå Seisjøl", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780035"), Name = "Snorke Sagbruk", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780036"), Name = "Gas S. Kammer", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780037"), Name = "Afterburner", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780038"), Name = "Lommedalsplogen", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) },
            new Horse { Id = Guid.Parse("12345678-0000-0000-0002-123456780039"), Name = "Trump Duck", UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), User = existingUsers.First(u => u.Id == Guid.Parse("12345678-0000-0000-0001-123456780004")) }

        };
    }   
} 