using MyApp.Models;

public static class HorseMock 
{
    public static List<Horse> CreateHorsesMock(List<User> existingUsers)
    {
        return new List<Horse> 
        {
            new Horse { Id = 1, Name = "Flodhest", UserId = 1, User = existingUsers.First(u => u.Id == 1) },
            new Horse { Id = 2, Name = "Enhjørning", UserId = 2, User = existingUsers.First(u => u.Id == 2) },
            new Horse { Id = 3, Name = "Zebra", UserId = 3, User = existingUsers.First(u => u.Id == 3) },
            new Horse { Id = 4, Name = "Sjøhest", UserId = 2, User = existingUsers.First(u => u.Id == 2) },
            new Horse { Id = 5, Name = "Gyngehest", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 6, Name = "Unikorn", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 7, Name = "Blue Layla", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 8, Name = "Pinkypai", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 9, Name = "Ponni", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 10, Name = "Twilight Sparkle", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 11, Name = "Rainbow Dash", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 12, Name = "Fluttershy", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 13, Name = "Applejack", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 14, Name = "Rarity", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 15, Name = "Æplekjækk", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 16, Name = "Princess celestia", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 17, Name = "Princess Luna", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 18, Name = "Kronprins Durek", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 19, Name = "Princess Marta", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 20, Name = "Mette Marit", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 21, Name = "Marius O. pat", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 22, Name = "Postman Pat", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 23, Name = "Psyko Pat", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 24, Name = "Mona Lysa", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 25, Name = "Fola Blakken", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 26, Name = "Esel", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 27, Name = "Kengeru Galopp", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 28, Name = "Snøfte Hirre", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 29, Name = "Fritz Viritsekk", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 30, Name = "Shreddlord", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 31, Name = "Brøyte pløyern", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 32, Name = "Glise Glad", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 33, Name = "Foto Gen", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 34, Name = "Marius Høypå Seisjøl", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 35, Name = "Snorke Sagbruk", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 36, Name = "Gas S. Kammer", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 37, Name = "Afterburner", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 38, Name = "Lommedalsplogen", UserId = 4, User = existingUsers.First(u => u.Id == 4) },
            new Horse { Id = 39, Name = "Trump Duck", UserId = 4, User = existingUsers.First(u => u.Id == 4) }

        };
    }   
} 