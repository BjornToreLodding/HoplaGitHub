using MyApp.Models;  // Dette må matche `using`-direktivet i controlleren

public static class StableMessageMock
{
    public static List<StableMessage> CreateStableMessagesMock()  
    {
        return new List<StableMessage>  // Riktig syntaks!
        {          
            new StableMessage { UserId = 1, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Hei, hesten min gjespa når vi fikk beskjed om at det ikke var lov ri og at det var båndtvang. Stemmer dette?"},
            new StableMessage { UserId = 2, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Jeg har lyst til å ri klokka 1. Er det flere som vil være med å ri?"},
            new StableMessage { UserId = 4, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Hesten min skal på verksted imorgen. Er det noen som kan låne meg en hest fordi jeg har lyst på en ridetur"},
            new StableMessage { UserId = 1, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-6), MessageText = "Hei, hesten min fikk hikkeanfall igår. Er det flere som har opplevd det når de har ridd i slottsparken?"},
            new StableMessage { UserId = 2, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-5), MessageText = "Nei, men jeg oppevde at den hadde diare når vi gikk forbi slottet"},
            new StableMessage { UserId = 4, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-4), MessageText = "Hei! Jeg ridde forbi slottet der 17mai toget går, og da kom det flere sinte gardister og lurte på om jeg viste noe om hvorfor asfalten var brun ved inngangen til slottet?"},
            new StableMessage { UserId = 1, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-3), MessageText = "Jeg skulle ri forbi slottet idag, men ble møtt med sperrebånd og metallgjerder.. Det stod også ekstraordinært hesteforbud pga omgangssyke blant slottshestene"},
            new StableMessage { UserId = 2, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-2), MessageText = "Hei! Det er nå åpnet for at vi kan ri forran slottet igjen, men vi må ha med spade og svart søppelsekk"},
            new StableMessage { UserId = 4, StableId = 1, SentAt = DateTime.UtcNow.AddDays(-1), MessageText = "Er de noen som kan låne meg en hest til slottsballet idag? Jeg har vogn selv, men trenger 1 ekstra hester for å trekke den tunge kjærra."},
            new StableMessage { UserId = 1, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-19), MessageText = "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?"},
            new StableMessage { UserId = 2, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-18), MessageText = "Det passer ikke idag, fordi det er dugnad i Stallen. Har du glemt det?"},
            new StableMessage { UserId = 1, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-17), MessageText = "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?"},
            new StableMessage { UserId = 2, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-16), MessageText = "Det passer ikke idag, fordi det hestene er pyntet til julebordet. Har du glemt det?"},
            new StableMessage { UserId = 1, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-15), MessageText = "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?"},
            new StableMessage { UserId = 2, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-14), MessageText = "Det passer ikke idag, fordi det hestene er fulle etter julebordet. Du burde da skjønne det?"},
            new StableMessage { UserId = 1, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-13), MessageText = "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?"},
            new StableMessage { UserId = 2, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-12), MessageText = "Det passer ikke idag, fordi det hestene trenger en fridag. Har du glemt det?"},
            new StableMessage { UserId = 1, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-11), MessageText = "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?"},
            new StableMessage { UserId = 2, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-10), MessageText = "Det passer ikke idag, fordi idag kommer det en som skal klippe negler på hestene, så da må alle hestene være tilgjengelig. Har du glemt det?"},
            new StableMessage { UserId = 3, StableId = 2, SentAt = DateTime.UtcNow.AddDays(-9),MessageText = "Jeg skal ri klokka 11 imorgen. Noen som vil være med?" }, 
            new StableMessage { UserId = 1, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Vil noen ri idag?" },
            new StableMessage { UserId = 2, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Vil noen ri imorgen?" },
            new StableMessage { UserId = 3, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Vil noen ri idag?" },
            new StableMessage { UserId = 4, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Vil noen ri ifjor?" },
            new StableMessage { UserId = 5, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Vil noen ri når banen er preparert?" },
            new StableMessage { UserId = 6, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Vil noen ri idag?" },
            new StableMessage { UserId = 7, StableId = 3, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Vil noen ri neste år?" },
            new StableMessage { UserId = 8, StableId = 4, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Vil noen ri i måneskinn?" },
            new StableMessage { UserId = 9, StableId = 4, SentAt = DateTime.UtcNow.AddDays(-6), MessageText = "Vil noen ri det fine været?" },
            new StableMessage { UserId = 10, StableId = 4, SentAt = DateTime.UtcNow.AddDays(-5), MessageText = "Vil noen ri meg til ballet ikveld?" },
            new StableMessage { UserId = 11, StableId = 4, SentAt = DateTime.UtcNow.AddDays(-4), MessageText = "Håper så mange som mulig vil være med å ri idag?" },
            new StableMessage { UserId = 12, StableId = 4, SentAt = DateTime.UtcNow.AddDays(-3), MessageText = "Vil noen galoppere idag?" },
            new StableMessage { UserId = 13, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-2), MessageText = "Vil noen trave rundt idag?" },
            new StableMessage { UserId = 14, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Vil noen være med å stelle hestene idag?" },
            new StableMessage { UserId = 15, StableId = 5, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Vil noen være med på travløp?" },
            new StableMessage { UserId = 16, StableId = 6, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Vil noen være med V75 idag" },
            new StableMessage { UserId = 17, StableId = 6, SentAt = DateTime.UtcNow.AddDays(-6), MessageText = "Vil noen være med å spille på hest idag" },
            new StableMessage { UserId = 18, StableId = 6, SentAt = DateTime.UtcNow.AddDays(-5), MessageText = "Vil noen ri igår?" }
            
        };
    }
}
