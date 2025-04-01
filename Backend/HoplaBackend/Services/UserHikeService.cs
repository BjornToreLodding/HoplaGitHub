using HoplaBackend.Data;
using HoplaBackend.DTOs;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Services;
public class UserHikeService
{
    private readonly AppDbContext _context;

    public UserHikeService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<UserHikeDto>> GetUserHikes(Guid userId, int pageNumber = 1, int pageSize = 10)
    {
        return await _context.UserHikes
            .Where(u => u.UserId == userId)
            .OrderByDescending(u => u.CreatedAt) // Sorter etter nyeste
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .Select(u => new UserHikeDto
            {
                Id = u.Id,
                TrailName = u.Trail.Name,
                TrailId = (Guid)u.TrailId,
                Length = u.Distance,
                Duration = u.Duration,
                PictureUrl = u.PictureUrl
            })
            .ToListAsync();
    }
}

        

