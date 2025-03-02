using HoplaBackend.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("feed")]
public class EntityFeedController : ControllerBase
{
    private readonly AppDbContext _context;

    public EntityFeedController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet("{userId}")]
    public async Task<IActionResult> GetUserFeed(Guid userId)
    {
        var feed = await _context.EntityFeeds
            .Where(f => f.UserId == userId)
            .OrderByDescending(f => f.CreatedAt)
            .ToListAsync();

        return Ok(feed);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteFeedItem(Guid id)
    {
        var feedItem = await _context.EntityFeeds.FindAsync(id);
        if (feedItem == null)
            return NotFound();

        _context.EntityFeeds.Remove(feedItem);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
