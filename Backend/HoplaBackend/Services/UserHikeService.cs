using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Helpers;
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
            .Include(u => u.Horse)
            .ToListAsync();

        return hikes.Select(u => new UserHikeDto //linje 28
        {
            Id = u.Id,
            TrailName = string.IsNullOrWhiteSpace(u.Trail?.Name) ? "ikke oppgitt" : u.Trail?.Name,
            TrailId = u.TrailId ?? Guid.Empty,
            Length = u.Distance,
            Duration = u.Duration,
            Title = u.Title,
            Comment = u.Comment,
            HorseName = string.IsNullOrWhiteSpace(u.Horse?.Name) ? "Ukjent" : u.Horse.Name,
            PictureUrl = PictureHelper.BuildPictureUrl(u.PictureUrl, "UserProfileUserHikes"),
            //PictureUrl = u.PictureUrl,
            TrailButton = !u.TrailId.HasValue
        }).ToList();
    }

}

        

