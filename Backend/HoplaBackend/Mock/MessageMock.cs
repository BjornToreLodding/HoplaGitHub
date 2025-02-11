using Microsoft.VisualBasic;
using MyApp.Models;

public static class MessageMock 
{
    public static List<Message> CreateMessagesMock(){ 
        return [
            
            new Message { SUserId = 1 , RUserId = 2, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Hei! Er du interessert i å selge hesten din? Hvor mye selger du den eventuelt for?" },
            new Message { SUserId = 2 , RUserId = 1, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Kom med et bud" },
            new Message { SUserId = 1 , RUserId = 2, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Jeg har en 10Liters bøtte fra KFC, som er veldig sjeldent. Snakker vi en deal?"},
            new Message { SUserId = 2 , RUserId = 1, SentAt = DateTime.UtcNow.AddDays(-6), MessageText = "Oi, så kult. Da har vi en avtale. Gleder meg til å bli eier av en så sjelden bøtte." },
            new Message { SUserId = 1 , RUserId = 2, SentAt = DateTime.UtcNow.AddDays(-5), MessageText = "Følger det med både sommer og vinterdekk på den firebeinte?"},
            new Message { SUserId = 2 , RUserId = 1, SentAt = DateTime.UtcNow.AddDays(-4), MessageText = "Ja, selvfølgelig skal du få med det. Vinterdekkene er helt nye av typen Himalia Winter Expert" },
            new Message { SUserId = 1 , RUserId = 3, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Hei! Var det du som ridde gjennom hagen min igår?" },
            new Message { SUserId = 3 , RUserId = 1, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Ja, det stemmer. Hvordan det?" },
            new Message { SUserId = 1 , RUserId = 3, SentAt = DateTime.UtcNow.AddDays(-6), MessageText = "Du har tråkket istykker mange av mine blomster og plenen min er ødelagt"},
            new Message { SUserId = 3 , RUserId = 1, SentAt = DateTime.UtcNow.AddDays(-5), MessageText = "Det du kaller blomster var bare ustelt ugress, og den plenen har vært forsømt i mange år, så det gjør ingen forskjell. Du får ta bedre vare på hagen din og klippe plenen og luke ugress hvis du er så redd for den?" },
            new Message { SUserId = 1 , RUserId = 3, SentAt = DateTime.UtcNow.AddDays(-4), MessageText = "Det er dårlig gjort av deg å kalle de flotte Hestehovene mine for ugress. Jeg lider stort økonomisk tap fordi Hestehovene har knekt eller visnet."},
            new Message { SUserId = 3 , RUserId = 1, SentAt = DateTime.UtcNow.AddDays(-3), MessageText = "Ja, det er sånt som skjer med Hestehov når du glemer å vanne dem. Jeg og mine ridevenner kaller hagen din for jungelen fordi vokser ukontrollert" },
            new Message { SUserId = 2 , RUserId = 3, SentAt = DateTime.UtcNow.AddDays(-9), MessageText = "Hei! Var det du som ridde forbi stallen min igår?" },
            new Message { SUserId = 3 , RUserId = 2, SentAt = DateTime.UtcNow.AddDays(-8), MessageText = "Ja, det stemmer. Hvordan det?" },
            new Message { SUserId = 2 , RUserId = 3, SentAt = DateTime.UtcNow.AddDays(-7), MessageText = "Hesten din bjeffet og knurra på min flotte Ponni. Nå er den redd og skjelver"},
            new Message { SUserId = 3 , RUserId = 2, SentAt = DateTime.UtcNow.AddDays(-6), MessageText = "Det er ikke sant. Det er hesten min som skjelver, fordi din Ponni flekka tenner og bjeffet på oss. Nå er den hos psykolog for behandling for traumene. Kan jeg sende regninga til deg?" },
            new Message { SUserId = 2 , RUserId = 3, SentAt = DateTime.UtcNow.AddDays(-5), MessageText = "Du må gjerne forsøke å sende regninga hit, men den kommer ikke til å bli betalt av meg. "},
            new Message { SUserId = 3 , RUserId = 2, SentAt = DateTime.UtcNow.AddDays(-4), MessageText = "Hvis du ikke betaler regninga, ringer jeg politiet" }

        ];
    }
}