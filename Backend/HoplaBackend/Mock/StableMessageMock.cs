using MyApp.Models;  // Dette må matche `using`-direktivet i controlleren

public static class StableMessageMock
{
    public static List<StableMessage> CreateStableMessagesMock()  
    {
        return new List<StableMessage>  // Riktig syntaks!
        {          
            new StableMessage { UserId = 1, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-9), Message = "Vil noen ri idag?" },
            new StableMessage { UserId = 2, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-8), Message = "Vil noen ri imorgen?" },
            new StableMessage { UserId = 3, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-7), Message = "Vil noen ri idag?" },
            new StableMessage { UserId = 4, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-9), Message = "Vil noen ri ifjor?" },
            new StableMessage { UserId = 5, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-8), Message = "Vil noen ri når banen er preparert?" },
            new StableMessage { UserId = 6, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-9), Message = "Vil noen ri idag?" },
            new StableMessage { UserId = 7, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-8), Message = "Vil noen ri neste år?" },
            new StableMessage { UserId = 8, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-7), Message = "Vil noen ri i måneskinn?" },
            new StableMessage { UserId = 9, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-6), Message = "Vil noen ri det fine været?" },
            new StableMessage { UserId = 10, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-5), Message = "Vil noen ri meg til ballet ikveld?" },
            new StableMessage { UserId = 11, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-4), Message = "Håper så mange som mulig vil være med å ri idag?" },
            new StableMessage { UserId = 12, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-3), Message = "Vil noen galoppere idag?" },
            new StableMessage { UserId = 13, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-2), Message = "Vil noen trave rundt idag?" },
            new StableMessage { UserId = 14, StableId = 4, SentAt = DateTime.UtcNow.AddDays(-9), Message = "Vil noen være med å stelle hestene idag?" },
            new StableMessage { UserId = 15, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-8), Message = "Vil noen være med på travløp?" },
            new StableMessage { UserId = 16, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-7), Message = "Vil noen være med V75 idag" },
            new StableMessage { UserId = 17, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-6), Message = "Vil noen være med å spille på hest idag" },
            new StableMessage { UserId = 18, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-5), Message = "Vil noen ri igår?" }
            
        };
    }
}
