using MyApp.Models;

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
        return [
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695 
            new Ride { UserId = 1, HorseId = 1, Length = 12.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.95558, LongMean = 10.6115 }, CreatedAt = DateTime.UtcNow.AddDays(-8) }, //Biri //LatMean = 60.95558, LongMean = 10.6115
            new Ride { UserId = 1, HorseId = 1, Length = 10.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.95  , LongMean = 10.466667}, CreatedAt = DateTime.UtcNow.AddDays(-7) }, //Lommedalen
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 59.9175 , LongMean = 10.7283 }, CreatedAt = DateTime.UtcNow.AddDays(-6) }, //slottsparken 59.9175, 10.7283
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925 , LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-5) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925 , LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-4) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925 , LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-3) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925 , LongMean = 10.695  },  CreatedAt= DateTime.UtcNow.AddDays(-2) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925 , LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-1) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-0) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-9) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  },  CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.95558, LongMean = 10.6115 }, CreatedAt = DateTime.UtcNow.AddDays(-8) }, //Biri
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-7) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-6) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-5) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-4) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-3) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-2) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-1) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-0) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695  }, CreatedAt = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.95558, LongMean = 10.6115 }, CreatedAt = DateTime.UtcNow.AddDays(-8) }, //Biri
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-7) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-6) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-5) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-4) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-3) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-2) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-1) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, RideDetails = new RideDetail { LatMean = 60.7925,  LongMean = 10.695 }, CreatedAt = DateTime.UtcNow.AddDays(-0) },
            
        ];
        
    }
}