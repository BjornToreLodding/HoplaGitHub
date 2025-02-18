using System.Linq;
using System;
using System.Collections.Generic;
using MyApp.Models;

public static class UserMock 
{
    public static List<User> CreateUsersMock(){
        return new List<User>
        {            
            new User { Id = 1, Name = "Bjørn Tore Lødding", Alias = "Shredlord", Email = "bjortlod@stud.ntnu.no", PasswordHash = "hashed_pw1", Admin = true, Premium = true, VerifiedTrail = true },
            new User { Id = 2, Name = "Ane Marie", Alias = "Utvikler Anmajo" ,Email = "amjohnse@stud.ntnu.no", PasswordHash = "hashed_pw2", Admin = true, Premium = true, VerifiedTrail = true },
            new User { Id = 3, Name = "Vilde", Alias = "Utvikler Vildesk" ,Email = "vakvaer@stud.ntnu.no", PasswordHash = "hashed_pw3", Admin = true, Premium = true, VerifiedTrail = true },
            new User { Id = 4, Name = "Thea Dyring", Alias = "Jockey Thea", Email = "td@hopla.no", PasswordHash = "hashed_pw4", Admin = true, Premium = true, VerifiedTrail = true },
            new User { Id = 5, Name = "Ann Iren Haakenstad", Alias = "Jockey Ann Iren", Email = "aih@hopla.no", PasswordHash = "hashed_pw4", Admin = true, Premium = true, VerifiedTrail = true  },
            new User { Id = 6, Name = "Anna Louise Sedolfsen", Alias = "Jockey Anna Louise", Email = "als@hopla.no", PasswordHash = "hashed_pw4", Admin = true, Premium = true, VerifiedTrail = true  },
            new User { Id = 7, Name = "Helle Kopter", Alias = "Rotor snurror", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 8, Name = "Heli Kopeter", Alias = "HeliKo-Peter", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 9, Name = "Finn Biff", Alias = "FantBiff", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 10, Name = "Kari Usel", Alias = "Karusell Kari", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 11, Name = "Fred Løs", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 12, Name = "Knugen Kneggason", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 13, Name = "Kjell T. Ringen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 14, Name = "Kamuf Larsen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 15, Name = "Emba Larsen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 16, Name = "Tannlege Røskeland", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 17, Name = "Øyelege Iris Øyen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 18, Name = "Vimse Gnagerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 19, Name = "Knugis Kneggason", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 20, Name = "Gampen Raut", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 21, Name = "Vimse Gnagerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 22, Name = "Traven Bladfjær", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 23, Name = "Spjællur Vang", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 24, Name = "Kneggar Knaggen", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 25, Name = "Klara Snøfterud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 26, Name = "Sukke Reidar", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 27, Name = "Ingvaldos Snik", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 28, Name = "Drago Stea Dindrei", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 29, Name = "Fleske Berge", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 30, Name = "Ingolf Snøvlerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 31, Name = "Kalvbein Snublerud", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 32, Name = "Stalke Ulf", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 33, Name = "Hurpe heksa", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 34, Name = "Heks Decibel", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 35, Name = "Natron Snyt", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 36, Name = "Sylta Ludvik", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 37, Name = "Høysnue Kåre", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 38, Name = "Ester Espenes", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            new User { Id = 39, Name = "Raptor Fox", Email = "thea@hopla.no", PasswordHash = "hashed_pw4" },
            
            

        };
    }
}