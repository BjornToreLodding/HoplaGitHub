//using HoplaBackend.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using Microsoft.AspNetCore.Authorization;
using System.Linq;

namespace HoplaBackend.Controllers;


[Route("stablemessages")]
[ApiController]
public class StableMessageController : ControllerBase
{
    private readonly AppDbContext _context;

    public StableMessageController(AppDbContext context)
    {
        _context = context;
    }

    [Authorize]
    [HttpGet("{stableId}")] // Returnerer meldinger mellom to brukere eller siste melding per bruker
    public async Task<IActionResult> GetMessagesBetweenUsers(
        Guid stableId,
        [FromQuery] Guid? userid,
        [FromQuery] int pageSize = 10,
        [FromQuery] int pageNumber = 1) // id er optional, men hvis spesifisert så returneres alle meldinger som user har sendt til stableId
    {
       /*
        // Hvis id er spesifisert: Hent alle meldinger mellom userId og stableId
        if (userid.HasValue)
        {
            var userstablemessages = await _context.StableMessages
                .Include(s => s.User)  
                .Include(s => s.Stable) 
                .Where(s => s.UserId == userid && s.StableId == stableId) //&& 
                            //(m.SUserId == id.Value || m.RUserId == id.Value))
                .OrderBy(s => s.SentAt)
                .Select(s => new 
                {
                    Content = s.MessageText,
                    Timestamp = s.SentAt,
                    SenderId = s.UserId,
                    SenderName = s.User.Name,
                    StableId = s.StableId,
                    StableName = s.Stable.Name
                })
                .ToListAsync();

            return Ok(userstablemessages);
        }
*/
        // Hvis userid IKKE er spesifisert: Hent alle meldinger fra stallid
        var stablemessages = await _context.StableMessages
            .Where(s => s.StableId == stableId ) // Finn meldinger til/fra bruker
            .OrderByDescending(s => s.SentAt)  // Sorter etter nyeste melding først
            //.GroupBy(m => m.SUserId == userId ? m.RUserId : m.SUserId) // Grupper etter samtalepartner
            //.Select(g => g.OrderByDescending(s => s.SentAt).First()) // Velg kun den nyeste meldingen i hver gruppe
            .Select(s => new 
            {
                Content = s.MessageText,
                Timestamp = s.SentAt,
                SenderId = s.UserId,
                SenderAlias = s.User.Alias,
                
            })
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(); // Hent resultatet før vi inkluderer brukere

        return Ok(stablemessages);
    }
    //Sletting av meldinger i stall. Kan kun gjøres hvis userID den som forsøker å slette er admin/moderator for stallen.
}