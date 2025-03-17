using HoplaBackend.Data;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Services;
public class EntityService
{
    private readonly AppDbContext _context;

    public EntityService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<string> GetEntityNameAsync(string entityName, Guid entityId)
    {
        Console.WriteLine(entityName);
        Console.WriteLine(entityId);
        return entityName switch
        {
            "Horses" => await _context.Horses
                .Where(h => h.Id == entityId)
                .Select(h => h.Name)
                .FirstOrDefaultAsync() ?? "Ukjent",

            "Trails" => await _context.Trails
                .Where(t => t.Id == entityId)
                .Select(t => t.Name)
                .FirstOrDefaultAsync() ?? "Ukjent",

            "Users" => await _context.Users
                .Where(u => u.Id == entityId)
                .Select(u => u.Name)
                .FirstOrDefaultAsync() ?? "Ukjent",

            _ => "Ukjent"
        };
    }
}
