using System;
using System.Collections.Generic;
using HoplaBackend.Models;

public static class RideMock 
{
    public static List<Ride> CreateRidesMock(){ 
        /*
    var rides = new List<Ride>();
    var locations = new List<(double LatMean, double LongMean)>
    {
        (60.7925, 10.695),    // Gjøvik
        (60.95558, 10.6115),  // Biri
        (59.95, 10.466667),   // Lommedalen
        (59.9175, 10.7283)    // Slottsparken
    };

    for (int i = 1; i <= 10; i++)  // Itererer over UserId
    {
        for (int j = 1; j <= 10; j++)  // Itererer over AddDays(-(10-j))
        {
            var location = locations[j % locations.Count];  // Henter en plassering basert på j

            rides.Add(new Ride
            {
                UserId = i,
                HorseId = i,  // Du kan endre dette om ønskelig
                Length = 10 + (j % 5) * 2,  // Variasjon i lengde
                Duration = 40 + (j % 3) * 10, // Variasjon i varighet
                RideDetails = new RideDetail
                {
                    LatMean = location.LatMean,
                    LongMean = location.LongMean
                },
                CreatedAt = DateTime.UtcNow.AddDays(-(10 - j)) // Dato forskjøvet bakover
            });
        }
    }

    return rides;
    */

    // Gammel kode for backup, hvis koden ovenfor skulle feile.
        return new List<Ride>
        {
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780001"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695 
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780002"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 12.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.95558, LongMean = 10.6115 }, CreatedAt = DateTime.UtcNow.AddDays(-8) }, //Biri //LatMean = 60.95558, LongMean = 10.6115
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780003"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 10.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.95  , LongMean = 10.466667}, CreatedAt = DateTime.UtcNow.AddDays(-7) }, //Lommedalen
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780004"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.9175 , LongMean = 10.7283 }, CreatedAt = DateTime.UtcNow.AddDays(-6) }, //slottsparken 59.9175, 10.7283
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780005"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.94033 , LongMean = 10.940  },  CreatedAt = DateTime.UtcNow.AddDays(-5) }, //lørsnskog 59.9403337386 10.940326572
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780006"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925 , LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-4) }, //motorvei
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780007"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.8206 , LongMean = 7.1855  },  CreatedAt = DateTime.UtcNow.AddDays(-3) }, //haukeli [59.82068 7.1855] 
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780008"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 62.1036 , LongMean = 7.0992  },  CreatedAt= DateTime.UtcNow.AddDays(-2) }, //geiranger 62.103561, and the longitude is 7.099228
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780009"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.1415 , LongMean = 11.175  },  CreatedAt = DateTime.UtcNow.AddDays(-1) }, // Jessheim  atitude 60.14151 and longitude 11.17515.
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780010"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780001"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.0765,  LongMean = 11.1532 },  CreatedAt = DateTime.UtcNow.AddDays(-0) }, //kløfta  60.076581, 11.1432147
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780011"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.9666,  LongMean = 10.9392 },  CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Gjelleråsen 59.966694, 10.939220
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780012"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.2581,  LongMean = 11.0293 },  CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Maura 60.25810 11.02930
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780013"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.9862 , LongMean = 11.2415 }, CreatedAt = DateTime.UtcNow.AddDays(-8) }, //sørumsand  59.98621 and longitude of 11.24154
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780014"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.9117,  LongMean = 10.6364 }, CreatedAt = DateTime.UtcNow.AddDays(-7) },//Lysaker 59.91177 10.63642
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780015"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.8890,  LongMean = 10.5220 }, CreatedAt = DateTime.UtcNow.AddDays(-6) }, //Sandvika Latitude: 59.8890 Longitude: 10.5220
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780016"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-5) }, //Fornebu
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780017"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-4) }, //Høvik
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780018"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-3) }, //Stabekk
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780019"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-2) }, //Sørkedalen
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780020"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-1) }, //Gaustad
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780021"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780002"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-0) }, //Sjølyst
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780022"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780023"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.95558, LongMean = 10.6115 }, CreatedAt = DateTime.UtcNow.AddDays(-8) }, //Biri
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780024"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-7) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780025"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-6) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780026"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-5) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780027"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-4) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780028"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-3) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780029"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-2) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780030"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-1) },
            new Ride { Id = Guid.Parse("12345678-0000-0000-0011-123456780031"), UserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), HorseId = Guid.Parse("12345678-0000-0000-0002-123456780003"), Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-0) },
            
        };
        
    }
}