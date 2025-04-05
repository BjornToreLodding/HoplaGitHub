using System.Collections.Generic;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Org.BouncyCastle.Bcpg;

public static class TrailReviewMock 
{
    public static List<TrailReview> CreateTrailReviewMock(){

        return new List<TrailReview> 
        {
            new TrailReview { Id = Guid.Parse("12345678-0000-0000-0025-123456780001"), TrailId = Guid.Parse("12345678-0000-0000-0021-123456780001"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Comment = "Løypa er I god stand etter flommen", PictureUrl = "12345678-0000-0000-0031-123456780001.jpg", CreatedAt = DateTime.UtcNow.AddDays(-28).AddHours(-20)  },
            new TrailReview { Id = Guid.Parse("12345678-0000-0000-0025-123456780002"), TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Comment = "Det ligger et tre og sperrer løypa", PictureUrl = "12345678-0000-0000-0031-123456780002.jpg", CreatedAt = DateTime.UtcNow.AddDays(-28).AddHours(-18)  },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780003"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780005"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Comment = "Deler av stien er sperret med bånd.",
                PictureUrl = "12345678-0000-0000-0031-123456780026.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-28).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-05T21:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780004"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780019"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780013"),
                Comment = "Stor vannpytt ved midtpartiet.",
                PictureUrl = "12345678-0000-0000-0031-123456780012.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-28).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-05T23:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780005"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780013"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780015"),
                Comment = "Stiutbedring pågår, vær forsiktig.",
                PictureUrl = "12345678-0000-0000-0031-123456780013.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-27).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T01:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780006"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780007"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780005"),
                Comment = "Sperringer fjernet, løypa er åpen.",
                PictureUrl = "12345678-0000-0000-0031-123456780019.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-27).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T03:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780007"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780016"),
                Comment = "Hinder lagt ut for testritt.",
                PictureUrl = "12345678-0000-0000-0031-123456780008.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-26).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T05:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780008"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Comment = "Stengt pga jakt i området.",
                PictureUrl = "12345678-0000-0000-0031-123456780032.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-25).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T07:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780009"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780011"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Comment = "Stien er sperret mellom kl 9 og 16.",
                PictureUrl = "12345678-0000-0000-0031-123456780050.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-24).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T09:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780010"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780010"),
                Comment = "Merkede stier er oppdatert.",
                PictureUrl = "12345678-0000-0000-0031-123456780029.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-23).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T11:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780011"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780002"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"),
                Comment = "Stengt pga jakt i området.",
                PictureUrl = "12345678-0000-0000-0031-123456780027.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-22).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T13:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780012"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780002"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780016"),
                Comment = "Stien er nymerket.",
                PictureUrl = "12345678-0000-0000-0031-123456780025.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-21).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T15:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780013"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780009"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Comment = "Nye retningsskilt satt opp.",
                PictureUrl = "12345678-0000-0000-0031-123456780022.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-20).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T17:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780014"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780019"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780007"),
                Comment = "Arbeid pågår, vent på klarsignal.",
                PictureUrl = "12345678-0000-0000-0031-123456780004.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-19).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T19:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780015"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Comment = "Røtter gjør enkelte partier glatte.",
                PictureUrl = "12345678-0000-0000-0031-123456780024.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-18).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T21:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780016"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780011"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780008"),
                Comment = "Stengt pga jakt i området.",
                PictureUrl = "12345678-0000-0000-0031-123456780002.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-17).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-06T23:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780017"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780019"),
                Comment = "Mye gjørme, vær obs.",
                PictureUrl = "12345678-0000-0000-0031-123456780032.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-16).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T01:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780018"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780007"),
                Comment = "Omkjøring etablert, følg skilting.",
                PictureUrl = "12345678-0000-0000-0031-123456780026.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-15).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T03:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780019"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Comment = "Løypa er i god stand etter flommen.",
                PictureUrl = "12345678-0000-0000-0031-123456780009.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-14).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T05:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780020"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Comment = "Stengt i 2 dager på grunn av skogsarbeid.",
                PictureUrl = "12345678-0000-0000-0031-123456780044.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-13).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T07:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780021"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780009"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Comment = "Ny bro er ferdigbygd og åpen.",
                PictureUrl = "12345678-0000-0000-0031-123456780037.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-12).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T09:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780022"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780004"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780016"),
                Comment = "Merkede stier er oppdatert.",
                PictureUrl = "12345678-0000-0000-0031-123456780029.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-11).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T11:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780023"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780007"),
                Comment = "Sperringer fjernet, løypa er åpen.",
                PictureUrl = "12345678-0000-0000-0031-123456780038.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-10).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T13:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780024"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780013"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Comment = "Flomskader reparert.",
                PictureUrl = "12345678-0000-0000-0031-123456780036.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-9).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T15:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780025"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780014"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780012"),
                Comment = "Hull i stien dekket til.",
                PictureUrl = "12345678-0000-0000-0031-123456780041.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T17:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780026"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780010"),
                Comment = "Snøsmelting gir glatte forhold.",
                PictureUrl = "12345678-0000-0000-0031-123456780040.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-10)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T19:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780027"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780010"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Comment = "Arbeid pågår, vent på klarsignal.",
                PictureUrl = "12345678-0000-0000-0031-123456780016.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-6)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T21:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780028"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780007"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780006"),
                Comment = "Ny bro er ferdigbygd og åpen.",
                PictureUrl = "12345678-0000-0000-0031-123456780050.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-07T23:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780029"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780009"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Comment = "Vannsig gjør noen partier glatte.",
                PictureUrl = "12345678-0000-0000-0031-123456780009.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-1)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T01:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780030"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780016"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780010"),
                Comment = "Løypa er i god stand etter flommen.",
                PictureUrl = "12345678-0000-0000-0031-123456780008.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-7).AddHours(-10)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T03:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780031"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780002"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780007"),
                Comment = "Liten omvei på grunn av anleggsarbeid.",
                PictureUrl = "12345678-0000-0000-0031-123456780008.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-7).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T05:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780032"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Comment = "Tørt og fint føre!",
                PictureUrl = "12345678-0000-0000-0031-123456780041.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-6).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T07:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780033"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780016"),
                Comment = "Det er observert elg i området.",
                PictureUrl = "12345678-0000-0000-0031-123456780046.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-6).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T09:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780034"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780013"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780019"),
                Comment = "Stiutbedring ferdigstilt.",
                PictureUrl = "12345678-0000-0000-0031-123456780010.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-5).AddHours(-16)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T11:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780035"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780010"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Comment = "Steinete parti repareres.",
                PictureUrl = "12345678-0000-0000-0031-123456780041.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-5).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T13:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780036"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780007"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Comment = "Deler av stien er sperret med bånd.",
                PictureUrl = "12345678-0000-0000-0031-123456780021.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-5).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T15:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780037"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780006"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780013"),
                Comment = "Delvis stengt på grunn av maskiner.",
                PictureUrl = "12345678-0000-0000-0031-123456780015.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-4).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T17:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780038"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780014"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780013"),
                Comment = "Graving pågår, vis hensyn.",
                PictureUrl = "12345678-0000-0000-0031-123456780022.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-4).AddHours(-9)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T19:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780039"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780010"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780012"),
                Comment = "Liten omvei på grunn av anleggsarbeid.",
                PictureUrl = "12345678-0000-0000-0031-123456780004.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-4).AddHours(-7)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T21:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780040"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780002"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Comment = "Hull i stien dekket til.",
                PictureUrl = "12345678-0000-0000-0031-123456780034.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-4).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-08T23:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780041"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780014"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Comment = "Mye gjørme, vær obs.",
                PictureUrl = "12345678-0000-0000-0031-123456780041.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-3).AddHours(-15)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T01:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780042"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Comment = "Hinder lagt ut for testritt.",
                PictureUrl = "12345678-0000-0000-0031-123456780019.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-3).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T03:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780043"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780011"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Comment = "Stengt i 2 dager på grunn av skogsarbeid.",
                PictureUrl = "12345678-0000-0000-0031-123456780043.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-3).AddHours(-8)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T05:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780044"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780015"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780011"),
                Comment = "Det ligger et tre og sperrer løypa.",
                PictureUrl = "12345678-0000-0000-0031-123456780046.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-3).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T07:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780045"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780007"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Comment = "Det ligger et tre og sperrer løypa.",
                PictureUrl = "12345678-0000-0000-0031-123456780010.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-2).AddHours(-16)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T09:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780046"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Comment = "Nye retningsskilt satt opp.",
                PictureUrl = "12345678-0000-0000-0031-123456780024.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-2).AddHours(-14)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T11:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780047"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780005"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Comment = "Snøsmelting gir glatte forhold.",
                PictureUrl = "12345678-0000-0000-0031-123456780013.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-2).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T13:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780048"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780016"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780006"),
                Comment = "Stengt i 2 dager på grunn av skogsarbeid.",
                PictureUrl = "12345678-0000-0000-0031-123456780038.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-2).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T15:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780049"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780004"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Comment = "Flomskader reparert.",
                PictureUrl = "12345678-0000-0000-0031-123456780033.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-1).AddHours(-14)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T17:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780050"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780001"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Comment = "Området er gjørmete, bruk støvler.",
                PictureUrl = "12345678-0000-0000-0031-123456780034.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-1).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T19:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780051"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780005"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780012"),
                Comment = "Løypa er i god stand etter flommen.",
                PictureUrl = "12345678-0000-0000-0031-123456780013.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-1).AddHours(-2)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T21:53:23"), DateTimeKind.Utc)
            },
            new TrailReview {
                Id = Guid.Parse("12345678-0000-0000-0025-123456780052"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Comment = "Pågående hogstarbeid, bruk hjelm.",
                PictureUrl = "12345678-0000-0000-0031-123456780011.jpg",
                CreatedAt = DateTime.UtcNow.AddDays(-0).AddHours(-12)
                //CreatedAt = DateTime.SpecifyKind(DateTime.Parse("2025-03-09T23:53:23"), DateTimeKind.Utc)
            },
        };
    }
}
