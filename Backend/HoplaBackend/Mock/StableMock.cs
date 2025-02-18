using System.Collections.Generic;
using MyApp.Models;

public static class StableMock 
{
    public static List<Stable> GetStablesMock(){
        return new List<Stable> 
        {
            new Stable { Id = Guid.Parse("12345678-0000-0000-0006-123456780001"), Name = "Den Kongelige Stall", Location = "Slottsparken"},
            new Stable { Id = Guid.Parse("12345678-0000-0000-0006-123456780002"), Name = "Lommedalen Samdrift", Location = "Lommedalen"},
            new Stable { Id = Guid.Parse("12345678-0000-0000-0006-123456780003"), Name = "Gjøvik Hestesamling", Location = "Gjøvik"},
            new Stable { Id = Guid.Parse("12345678-0000-0000-0006-123456780004"), Name = "Billitt Hesteforening", Location = "Starum"},
            new Stable { Id = Guid.Parse("12345678-0000-0000-0006-123456780005"), Name = "Biri Travbane", Location = "Biri"},
            new Stable { Id = Guid.Parse("12345678-0000-0000-0006-123456780006"), Name = "Sørkedalen Stalldrift", Location = "Sørkedalen"}

        };
    }
}