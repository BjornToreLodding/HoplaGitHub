using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Models;
using HoplaBackend.Data;
using Microsoft.AspNetCore.Authorization;
using HoplaBackend.DTOs;
using HoplaBackend.Helpers;

namespace HoplaBackend.Controllers;
[ApiController]
[Route("reactions")]
public class ReactionController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;
    public ReactionController(AppDbContext context, Authentication authentication)
    {
        _context = context;
        _authentication = authentication;

    }
    // POST /reaction
    [Authorize]
    [HttpPost]
    public async Task<IActionResult> AddReaction([FromBody] ReactionDto response)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // 🔒 Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });

        // Sjekk om bruker allerede har reagert
        var existingReaction = await _context.EntityReactions
            .FirstOrDefaultAsync(r => r.EntityId == response.EntityId && r.UserId == userId);

        if (existingReaction != null)
        {
            return BadRequest("User has already reacted to this post.");
        }

        var reaction = new EntityReaction
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            EntityId = response.EntityId,
            EntityName = "EntityFeed", // Du kan hardkode det nå
            Reaction = ReactionType.Like, // Alltid Like foreløpig
            CreatedAt = DateTime.UtcNow
        };

        _context.EntityReactions.Add(reaction);

        // Oppdater LikesCount i EntityFeed
        var feed = await _context.EntityFeeds.FirstOrDefaultAsync(f => f.EntityId == response.EntityId);
        Console.WriteLine("");
        Console.WriteLine(response.EntityId);
        
        Console.WriteLine(feed);
        //Console.WriteLine(feed.LikesCount);
        Console.WriteLine("");
        if (feed != null)
        {
            feed.LikesCount++;
        }

        await _context.SaveChangesAsync();

        return Ok();
    }

    // DELETE /reaction
    [Authorize]
    [HttpDelete]
    public async Task<IActionResult> RemoveReaction([FromBody] ReactionDto response)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // 🔒 Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });


        var existingReaction = await _context.EntityReactions
            .FirstOrDefaultAsync(r => r.EntityId == response.EntityId && r.UserId == userId);

        if (existingReaction == null)
        {
            return NotFound("No reaction found to remove.");
        }

        _context.EntityReactions.Remove(existingReaction);

        // Oppdater LikesCount i EntityFeed
        var feed = await _context.EntityFeeds.FirstOrDefaultAsync(f => f.EntityId == response.EntityId);
        if (feed != null && feed.LikesCount > 0)
        {
            feed.LikesCount--;
        }

        await _context.SaveChangesAsync();

        return Ok();
    }

    // GET /reaction/{entityId}
    [Authorize]
    [HttpGet("{entityId}")]
    public async Task<IActionResult> HasReacted(Guid entityId)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        // 🔒 Sjekk at brukeren finnes
        var userExists = await _context.Users.AnyAsync(u => u.Id == userId);
        if (!userExists)
            return Unauthorized(new { message = "Bruker ikke funnet" });


        var hasReacted = await _context.EntityReactions
            .AnyAsync(r => r.EntityId == entityId && r.UserId == userId);

        return Ok(new { hasReacted });
    }

    // Eksempel-metode for å hente UserId
    private Guid GetUserId()
    {
        // Dette er kun en plassholder.
        // Du må hente fra claims hvis du bruker auth!
        return Guid.Parse(User.FindFirst("sub")?.Value ?? throw new Exception("User ID not found"));
    }
}

// Request-modell for POST/DELETE
public class ReactionRequest
{
    public Guid EntityId { get; set; }
}
