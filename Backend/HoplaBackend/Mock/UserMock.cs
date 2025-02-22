using System.Linq;
using System;
using System.Collections.Generic;
using HoplaBackend.Models;

public static class UserMock 
{
    public static List<User> CreateUsersMock(){
        return new List<User>
        {            
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780001"), Name = "Bjørn Tore Lødding", Alias = "Shredlord", Email = "bjortlod@stud.ntnu.no", PasswordHash = "Hopla2025", Admin = true, Premium = true, VerifiedTrail = true, ProfilePictureUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780002"), Name = "Ane Marie", Alias = "Utvikler Anmajo" ,Email = "amjohnse@stud.ntnu.no", PasswordHash = "Hopla2025", Admin = true, Premium = true, VerifiedTrail = true, ProfilePictureUrl = "https://plus.unsplash.com/premium_photo-1661868397660-8c52f33c5934" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780003"), Name = "Vilde", Alias = "Utvikler Vildesk" ,Email = "vakvaer@stud.ntnu.no", PasswordHash = "Hopla2025", Admin = true, Premium = true, VerifiedTrail = true, ProfilePictureUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780004"), Name = "Thea Dyring", Alias = "Jockey Thea", Email = "td@hopla.no", PasswordHash = "Hopla2025", Admin = true, Premium = true, VerifiedTrail = true, ProfilePictureUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780005"), Name = "Ann Iren Haakenstad", Alias = "Jockey Ann Iren", Email = "aih@hopla.no", PasswordHash = "Hopla2025", Admin = true, Premium = true, VerifiedTrail = true, ProfilePictureUrl = "https://images.unsplash.com/photo-1554151228-14d9def656e4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780006"), Name = "Anna Louise Sedolfsen", Alias = "Jockey Anna Louise", Email = "als@hopla.no", PasswordHash = "Hopla2025", Admin = true, Premium = true, VerifiedTrail = true, ProfilePictureUrl = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780007"), Name = "Helle Kopter", Alias = "Rotor snurror", Email = "thea@hopla.no", PasswordHash = "Hopla2025", ProfilePictureUrl = "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780008"), Name = "Heli Kopeter", Alias = "HeliKo-Peter", Email = "thea@hopla.no", PasswordHash = "Hopla2025", ProfilePictureUrl = "https://images.unsplash.com/photo-1509460913899-515f1df34fea" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780009"), Name = "Finn Biff", Alias = "FantBiff", Email = "thea@hopla.no", PasswordHash = "Hopla2025", ProfilePictureUrl = "https://images.unsplash.com/photo-1568038479111-87bf80659645" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780010"), Name = "Kari Usel", Alias = "Karusell Kari", Email = "thea@hopla.no", PasswordHash = "Hopla2025", ProfilePictureUrl = "https://images.unsplash.com/photo-1514626585111-9aa86183ac98" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780011"), Name = "Fred Løs", Email = "thea@hopla.no", PasswordHash = "Hopla2025", ProfilePictureUrl = "https://images.unsplash.com/photo-1557053910-d9eadeed1c58"  },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780012"), Name = "Knugen Kneggason", Email = "thea@hopla.no", PasswordHash = "hashed_pw4", ProfilePictureUrl = "https://images.unsplash.com/photo-1534954553104-88cb75be7648" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780013"), Name = "Kjell T. Ringen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4", ProfilePictureUrl = "https://images.unsplash.com/photo-1500832333538-837287aad2b6" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780014"), Name = "Kamuf Larsen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4", ProfilePictureUrl = "https://plus.unsplash.com/premium_photo-1729688320738-8616a4ab3528"},
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780015"), Name = "Emba Larsen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4", ProfilePictureUrl = "https://plus.unsplash.com/premium_photo-1729688320964-b42095a429c6" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780016"), Name = "Tannlege Røskeland", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780017"), Name = "Øyelege Iris Øyen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780018"), Name = "Vimse Gnagerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780019"), Name = "Knugis Gnagur", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780020"), Name = "Gampen Raut", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780021"), Name = "Vimse Gnagerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780022"), Name = "Traven Bladfjær", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780023"), Name = "Spjællur Vang", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780024"), Name = "Kneggar Knaggen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780025"), Name = "Klara Snøfterud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780026"), Name = "Sukke Reidar", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780027"), Name = "Ingvaldos Snik", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780028"), Name = "Drago Stea Dindrei", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780029"), Name = "Fleske Berge", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780030"), Name = "Ingolf Snøvlerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780031"), Name = "Kalvbein Snublerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780032"), Name = "Stalke Ulf", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780033"), Name = "Hurpe heksa", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780034"), Name = "Heks Decibel", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780035"), Name = "Natron Snyt", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780036"), Name = "Sylta Ludvik", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780037"), Name = "Høysnue Kåre", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780038"), Name = "Ester Espenes", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = Guid.Parse("12345678-0000-0000-0001-123456780039"), Name = "Raptor Fox", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            
            

        };
    }
}