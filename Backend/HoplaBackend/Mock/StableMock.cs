using MyApp.Models;

public static class StableMock 
{
    public static List<Stable> GetStablesMock(){
        return [
            new Stable { Name = "Den Kongelige Stall", Location = "Slottsparken"},
            new Stable { Name = "Lommedalen Samdrift", Location = "Lommedalen"},
            new Stable { Name = "Gjøvik Hestesamling", Location = "Gjøvik"},
            new Stable { Name = "Billitt Hesteforening", Location = "Starum"},
            new Stable { Name = "Biri Travbane", Location = "Biri"},
            new Stable { Name = "Sørkedalen Stalldrift", Location = "Sørkedalen"}

        ];
    }
}