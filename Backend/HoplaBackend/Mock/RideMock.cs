using MyApp.Models;

public static class RideMock 
{
    public static List<Ride> CreateRidesMock(){ 
        return [
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695   , Date = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695 
            new Ride { UserId = 1, HorseId = 1, Length = 12.536, Duration = 50, LatMean = 60.95558, LongMean = 10.6115  , Date = DateTime.UtcNow.AddDays(-8) }, //Biri //LatMean = 60.95558, LongMean = 10.6115
            new Ride { UserId = 1, HorseId = 1, Length = 10.536, Duration = 50, LatMean = 59.95   , LongMean = 10.466667, Date = DateTime.UtcNow.AddDays(-7) }, //Lommedalen
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 59.9175 , LongMean = 10.7283  , Date = DateTime.UtcNow.AddDays(-6) }, //slottsparken 59.9175, 10.7283
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925 , LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-5) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925 , LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-4) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925 , LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-3) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925 , LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-2) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925 , LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-1) },
            new Ride { UserId = 1, HorseId = 1, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-0) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-9) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695   ,  Date = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.95558, LongMean = 10.6115 ,  Date = DateTime.UtcNow.AddDays(-8) }, //Biri
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  ,   Date = DateTime.UtcNow.AddDays(-7) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  ,  Date = DateTime.UtcNow.AddDays(-6) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  ,  Date = DateTime.UtcNow.AddDays(-5) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  , Date = DateTime.UtcNow.AddDays(-4) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  , Date = DateTime.UtcNow.AddDays(-3) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  , Date = DateTime.UtcNow.AddDays(-2) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  , Date = DateTime.UtcNow.AddDays(-1) },
            new Ride { UserId = 2, HorseId = 2, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  , Date = DateTime.UtcNow.AddDays(-0) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695  , Date = DateTime.UtcNow.AddDays(-9) }, //Gjøvik lat 60.7925, long 10.695
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.95558, LongMean = 10.6115, Date = DateTime.UtcNow.AddDays(-8) }, //Biri
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-7) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-6) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-5) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-4) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-3) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-2) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-1) },
            new Ride { UserId = 3, HorseId = 3, Length = 15.536, Duration = 50, LatMean = 60.7925,  LongMean = 10.695 , Date = DateTime.UtcNow.AddDays(-0) },
            
        ];
    }
}