using MyApp.Models;

public static class TrailMock 
{
    public static List<Trail> CreateTrailsMock(List<Ride> existingRides){ 
        return [
            
            new Trail { RideId = 1, Ride = existingRides.First(r => r.Id == 1), Name = "Biriløypa", Beskrivelse = "Flott Løype" },
            new Trail { RideId = 2, Ride = existingRides.First(r => r.Id == 2), Name = "Gjøviksruta", Beskrivelse = "Koselig tur langs fabrikker" },
            new Trail { RideId = 3, Ride = existingRides.First(r => r.Id == 3), Name = "Lommedalsrunden", Beskrivelse = "Vakkert landskap" },
            new Trail { RideId = 4, Ride = existingRides.First(r => r.Id == 4), Name = "Slottsparkstråkket", Beskrivelse = "Pass på sinte gardister" }
        
        ];
    }
}

 