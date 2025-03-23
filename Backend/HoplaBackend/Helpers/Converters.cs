using HoplaBackend.Helpers;
using Microsoft.EntityFrameworkCore.Metadata.Conventions;
using Microsoft.EntityFrameworkCore.Metadata.Internal;
//
// Denne brukes bare til test.
//
namespace HoplaBackend.Helpers;
public static class CustomConvert { //Kunne ikke bruke Convert da det denne klassen allerede eksisterer i C#
    public static Guid IntToGuid(string tabell, int idValue)
    {
        string tableValue = "0001";
        //raskere enn padding, og kan brukes så lenge verdien er under 20000
        //Dette er kun til testdata så antakeligvis vil verdiene ligge under 100
        string idString = $"1234567" + (80000 + idValue).ToString();
        if (tabell == "Horse") { tableValue = "0002"; }
        if (tabell == "Message") { tableValue = "0003"; }
        if (tabell == "StableMessage") { tableValue = "0004"; }
        if (tabell == "UserRelation") { tableValue = "0005"; }
        if (tabell == "Stable") { tableValue = "0006"; }
        string guidString = $"12345678-0000-0000-{tableValue}-{idString}";
        Console.WriteLine(guidString);        
        if (Guid.TryParse(guidString, out Guid guidValue))
        {
            return guidValue;
            //return error("En feil har oppstått");
        }else 
        {
            Console.WriteLine("Feil oppstod under konvertering fra int til GuId");
            throw new FormatException("Kunne ikke konvertere til en gyldig Guid");
        }
    }
}