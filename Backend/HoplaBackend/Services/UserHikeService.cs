using HoplaBackend.Data;
using HoplaBackend.DTOs;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

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
        var hikes = await _context.UserHikes
            .Where(u => u.UserId == userId)
            .OrderByDescending(u => u.CreatedAt)
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .Include(u => u.Trail) // Husk å hente Trail!
            .ToListAsync();

        return hikes.Select(u => new UserHikeDto
        {
            Id = u.Id,
            TrailName = string.IsNullOrWhiteSpace(u.Trail?.Name) ? "ikke oppgitt" : u.Trail?.Name,
            TrailId = u.TrailId ?? Guid.Empty,
            Length = u.Distance,
            Duration = u.Duration,
            PictureUrl = u.PictureUrl,
            TrailButton = !u.TrailId.HasValue
        }).ToList();
    }

}

        

