using System.Linq;
using System;
using System.Collections.Generic;
using System.Runtime.Intrinsics.Arm;
using MyApp.Models;

public static class TrailMock 
{
    public static List<Trail> CreateTrailsMock(List<Ride> existingRides){ 
        return new List<Trail>
        {
            //Bygg om denne til forloop!!
            new Trail { RideId = Guid.Parse("12345678-0000-0000-0011-123456780001"), Ride = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780001")), Name = "Biriløypa", TrailDetails = new TrailDetail { Description = "Flott Løype" }, LatMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780001")).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780001")).RideDetails?.LongMean ?? 0 },
            new Trail { RideId = Guid.Parse("12345678-0000-0000-0011-123456780002"), Ride = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780002")), Name = "Gjøviksruta", TrailDetails = new TrailDetail { Description = "Koselig tur langs forlatte fabrikker" }, LatMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780002")).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780002")).RideDetails?.LongMean ?? 0 },
            new Trail { RideId = Guid.Parse("12345678-0000-0000-0011-123456780003"), Ride = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780003")), Name = "Lommedalsrunden", TrailDetails = new TrailDetail { Description = "Vakkert landskap" } , LatMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780003")).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780003")).RideDetails?.LongMean ?? 0 },
            new Trail { RideId = Guid.Parse("12345678-0000-0000-0011-123456780004"), Ride = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780004")), Name = "Slottsparkstråkket", TrailDetails = new TrailDetail { Description = "Pass på sinte gardister" } , LatMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780004")).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780004")).RideDetails?.LongMean ?? 0},
            new Trail { RideId = Guid.Parse("12345678-0000-0000-0011-123456780005"), Ride = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780005")), Name = "Lørenskogruta", TrailDetails = new TrailDetail { Description = "Vakker rute langs Høyblokker bygd i Sovjetstil" } , LatMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780005")).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780005")).RideDetails?.LongMean ?? 0},
            new Trail { RideId = Guid.Parse("12345678-0000-0000-0011-123456780006"), Ride = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780006")), Name = "Motorveiløypa", TrailDetails = new TrailDetail { Description = "Hyggelig tur langs tungt trafikkert vei" } , LatMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780006")).RideDetails?.LatMean ?? 0, LongMean = existingRides.First(r => r.Id == Guid.Parse("12345678-0000-0000-0011-123456780006")).RideDetails?.LongMean ?? 0}

       
        };
    }
}

 