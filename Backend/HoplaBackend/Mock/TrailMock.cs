using System.Runtime.Intrinsics.Arm;
using MyApp.Models;

public static class TrailMock 
{
    public static List<Trail> CreateTrailsMock(List<Ride> existingRides){ 
        return [
            
            new Trail { RideId = 1, Ride = existingRides.First(r => r.Id == 1), Name = "Biriløypa", TrailDetails = new TrailDetail { Description = "Flott Løype" }, LatMean = existingRides.First(r => r.Id == 1).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == 1).RideDetails?.LongMean ?? 0 },
            new Trail { RideId = 2, Ride = existingRides.First(r => r.Id == 2), Name = "Gjøviksruta", TrailDetails = new TrailDetail { Description = "Koselig tur langs forlatte fabrikker" }, LatMean = existingRides.First(r => r.Id == 2).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == 2).RideDetails?.LongMean ?? 0 },
            new Trail { RideId = 3, Ride = existingRides.First(r => r.Id == 3), Name = "Lommedalsrunden", TrailDetails = new TrailDetail { Description = "Vakkert landskap" } , LatMean = existingRides.First(r => r.Id == 3).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == 3).RideDetails?.LongMean ?? 0 },
            new Trail { RideId = 4, Ride = existingRides.First(r => r.Id == 4), Name = "Slottsparkstråkket", TrailDetails = new TrailDetail { Description = "Pass på sinte gardister" } , LatMean = existingRides.First(r => r.Id == 4).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == 4).RideDetails?.LongMean ?? 0},
            new Trail { RideId = 5, Ride = existingRides.First(r => r.Id == 5), Name = "Lørenskogruta", TrailDetails = new TrailDetail { Description = "Vakker rute langs Høyblokker bygd i Sovjetstil" } , LatMean = existingRides.First(r => r.Id == 5).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == 5).RideDetails?.LongMean ?? 0},
            new Trail { RideId = 6, Ride = existingRides.First(r => r.Id == 6), Name = "Motorveiløypa", TrailDetails = new TrailDetail { Description = "Hyggelig tur langs tungt trafikkert vei" } , LatMean = existingRides.First(r => r.Id == 6).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == 6).RideDetails?.LongMean ?? 0}

       
        ];
    }
}

 