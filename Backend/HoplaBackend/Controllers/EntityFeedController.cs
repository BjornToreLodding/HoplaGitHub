using System.Security.Claims;
using HoplaBackend.Data;
using HoplaBackend.Models;
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

    [HttpGet("all")]
    public async Task<IActionResult> GetUserFeed()
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        var userId = parsedUserId;
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
