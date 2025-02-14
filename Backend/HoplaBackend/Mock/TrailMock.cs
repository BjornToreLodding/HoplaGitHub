using MyApp.Models;

public static class TrailMock 
{
    public static List<Trail> CreateTrailsMock(List<Ride> existingRides){ 
        return [
            
            new Trail { RideId = 1, Ride = existingRides.First(r => r.Id == 1), Name = "Biriløypa", TrailDetails = new TrailDetail { Description = "Flott Løype" } },
            new Trail { RideId = 2, Ride = existingRides.First(r => r.Id == 2), Name = "Gjøviksruta", TrailDetails = new TrailDetail { Description = "Koselig tur langs fabrikker" } },
            new Trail { RideId = 3, Ride = existingRides.First(r => r.Id == 3), Name = "Lommedalsrunden", TrailDetails = new TrailDetail { Description = "Vakkert landskap" } },
            new Trail { RideId = 4, Ride = existingRides.First(r => r.Id == 4), Name = "Slottsparkstråkket", TrailDetails = new TrailDetail { Description = "Pass på sinte gardister" } }
        
        ];
    }
}

 