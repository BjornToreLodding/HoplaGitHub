using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Org.BouncyCastle.Bcpg;

public static class TrailRatingMock 
{
    public static List<TrailRating> CreateTrailRatingMock(){
        return new List<TrailRating> 
        {
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780018"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780014"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-29).AddHours(-2) //("2025-03-07T03:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780019"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780006"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-28).AddHours(-2) //("2025-03-07T05:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780020"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780008"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-27).AddHours(-2) //("2025-03-07T07:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780021"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780011"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-26).AddHours(-2) //("2025-03-07T09:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780022"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780019"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-25).AddHours(-2) //("2025-03-07T11:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780023"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780011"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-24).AddHours(-2) //("2025-03-07T13:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780024"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780009"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-23).AddHours(-12) //("2025-03-07T15:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780025"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780005"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-23).AddHours(-2) //("2025-03-07T17:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780026"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780019"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-22).AddHours(-12) //("2025-03-07T19:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780027"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780006"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780009"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-22).AddHours(-2) //("2025-03-07T21:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780028"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780014"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-21).AddHours(-12) //("2025-03-07T23:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780029"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-21).AddHours(-2) //("2025-03-08T01:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780030"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780008"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-20).AddHours(-12) //("2025-03-08T03:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780031"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780007"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780016"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-20).AddHours(-2) //("2025-03-08T05:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780032"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780016"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780010"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-19).AddHours(-2) //("2025-03-08T07:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780033"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780010"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-18).AddHours(-2) //("2025-03-08T09:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780034"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780013"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-17).AddHours(-2) //("2025-03-08T11:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780035"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780013"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-16).AddHours(-12) //("2025-03-08T13:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780036"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780008"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-16).AddHours(-2) //("2025-03-08T15:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780037"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780007"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-15).AddHours(-12) //("2025-03-08T17:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780038"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780004"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780020"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-15).AddHours(-2) //("2025-03-08T19:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780039"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780001"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780012"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-14).AddHours(-12) //("2025-03-08T21:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780040"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780004"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780006"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-14).AddHours(-2) //("2025-03-08T23:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780041"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780013"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780014"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-13).AddHours(-12) //("2025-03-09T01:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780042"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780001"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780009"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-13).AddHours(-2) //("2025-03-09T03:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780043"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780019"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-12).AddHours(-12) //("2025-03-09T05:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780044"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780008"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780005"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-12).AddHours(-2) //("2025-03-09T07:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780045"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780016"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780020"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-11).AddHours(-12) //("2025-03-09T09:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780046"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780016"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780011"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-11).AddHours(-2) //("2025-03-09T11:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780047"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780009"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-10).AddHours(-12) //("2025-03-09T13:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780048"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780017"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780012"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-10).AddHours(-2) //("2025-03-09T15:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780049"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780001"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780008"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-9).AddHours(-12) //("2025-03-09T17:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780050"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780019"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780015"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-9).AddHours(-2) //("2025-03-09T19:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780051"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780013"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-12) //("2025-03-09T21:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780052"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780017"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780006"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-8).AddHours(-2) //("2025-03-09T23:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780053"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780004"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780005"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-7).AddHours(-12) //("2025-03-10T01:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780054"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-7).AddHours(-2) //("2025-03-10T03:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780055"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780011"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780005"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-6).AddHours(-12) //("2025-03-10T05:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780056"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780002"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780006"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-6).AddHours(-2) //("2025-03-10T07:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780057"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780004"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"),
                Rating = 1,
                CreatedAt = DateTime.UtcNow.AddDays(-5).AddHours(-12) //("2025-03-10T09:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780058"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-5).AddHours(-2) //("2025-03-10T11:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780059"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780017"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780004"),
                Rating = 5,
                CreatedAt = DateTime.UtcNow.AddDays(-4).AddHours(-12) //("2025-03-10T13:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780060"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780009"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-4).AddHours(-2) //("2025-03-10T15:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780061"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780007"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780005"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-3).AddHours(-12) //("2025-03-10T17:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780062"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780006"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780013"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-3).AddHours(-2) //("2025-03-10T19:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780063"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780012"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Rating = 3,
                CreatedAt = DateTime.UtcNow.AddDays(-2).AddHours(-12) //("2025-03-10T21:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780064"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780018"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780015"),
                Rating = 2,
                CreatedAt = DateTime.UtcNow.AddDays(-2).AddHours(-2) //("2025-03-10T23:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780065"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780020"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780007"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-1).AddHours(-12) //("2025-03-11T01:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780066"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780020"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-1).AddHours(-2) //("2025-03-11T03:59:46"), DateTimeKind.Utc)
            },
            new TrailRating {
                Id = Guid.Parse("12345678-0000-0000-0026-123456780067"),
                TrailId = Guid.Parse("12345678-0000-0000-0021-123456780003"),
                UserId = Guid.Parse("12345678-0000-0000-0001-123456780018"),
                Rating = 4,
                CreatedAt = DateTime.UtcNow.AddDays(-0).AddHours(-2) //("2025-03-11T05:59:46"), DateTimeKind.Utc)
            },
        };
    }
}
