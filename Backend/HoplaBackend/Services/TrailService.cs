using HoplaBackend.Data;
using HoplaBackend.DTOs;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Services;
public class TrailService
{
    private readonly AppDbContext _context;

    public TrailService(AppDbContext context)
    {
        _context = context;
    }

       public async Task<List<TrailDto>> GetUserTrails(Guid userId, int pageNumber = 1, int pageSize = 10)
        {
            return await _context.Trails
                .Where(t => t.UserId == userId)
                .OrderByDescending(t => t.CreatedAt) // Sorter etter nyeste
                .Skip((pageNumber - 1) * pageSize)
                .Take(pageSize)
                .Select(t => new TrailDto
                {
                    Id = t.Id,
                    Name = t.Name,
                    PictureUrl = t.PictureUrl,
                    AverageRating = (double)t.AverageRating
                    //lag riktig DTO for dette
                })
                .ToListAsync();
        }
    }
    

