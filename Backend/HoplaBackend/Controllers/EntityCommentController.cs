using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/comments")]
public class EntityCommentController : ControllerBase
{
    private readonly AppDbContext _context;

    public EntityCommentController(AppDbContext context)
    {
        _context = context;
    }

    [HttpPost]
    public async Task<IActionResult> AddComment([FromBody] EntityComment comment)
    {
        _context.EntityComments.Add(comment);
        await _context.SaveChangesAsync();
        return CreatedAtAction(nameof(AddComment), new { id = comment.Id }, comment);
    }

    [HttpGet("{entityId}")]
    public async Task<IActionResult> GetComments(Guid entityId)
    {
        var comments = await _context.EntityComments
            .Where(c => c.EntityId == entityId)
            .OrderByDescending(c => c.CreatedAt)
            .ToListAsync();

        return Ok(comments);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteComment(Guid id)
    {
        var comment = await _context.EntityComments.FindAsync(id);
        if (comment == null)
            return NotFound();

        _context.EntityComments.Remove(comment);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}
