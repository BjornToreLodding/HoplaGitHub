using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/reactions")]
public class EntityReactionController : ControllerBase
{
    private readonly AppDbContext _context;

    public EntityReactionController(AppDbContext context)
    {
        _context = context;
    }

    [HttpPost]
    public async Task<IActionResult> AddReaction([FromBody] EntityReaction reaction)
    {
        var existingReaction = await _context.EntityReactions
            .FirstOrDefaultAsync(r => r.UserId == reaction.UserId && r.EntityId == reaction.EntityId);

        if (existingReaction != null)
        {
            _context.EntityReactions.Remove(existingReaction);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        _context.EntityReactions.Add(reaction);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(AddReaction), new { id = reaction.Id }, reaction);
    }

    [HttpGet("{entityId}")]
    public async Task<IActionResult> GetReactions(Guid entityId)
    {
        var reactions = await _context.EntityReactions
            .Where(r => r.EntityId == entityId)
            .ToListAsync();

        return Ok(reactions);
    }
}
