using HoplaBackend.Data;
using Microsoft.EntityFrameworkCore;

public class TrailFavoriteService
{
    private readonly AppDbContext _context;

    public TrailFavoriteService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<bool> IsTrailFavoriteAsync(Guid userId, Guid trailId)
    {
        return await _context.TrailFavorites
            .AnyAsync(tf => tf.UserId == userId && tf.TrailId == trailId);
    }
}
