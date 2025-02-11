using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.Models;

namespace MyApp.Controllers
{
 
    [Route("message")]
    [ApiController]
    public class MessageController : ControllerBase
    {
        private readonly AppDbContext _context;

        public MessageController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("{id}")] //returnerer alle meldinger til bruker sortert på sist sendte melding.
        //Denne lages senere
        public async Task<IActionResult> GetAllMessages(int id)
        {
            var user = await _context.Users.FindAsync(id);

            if (user == null)
            {
                return NotFound(); // Returnerer 404 hvis brukeren ikke finnes
            }

            return Ok(new
            {
                //list opp alle som det er sendt melding med, sortert etter sist sendte melding.
                name = user.Name,
                
            });
        }
        [HttpGet("{userId}/{id}")] //returnerer alle meldinger som er sendt mellom 2 brukere.
        public async Task<IActionResult> GetMessage(int userId) //Denne lages senere
        {
        
            var friends = await _context.FriendRequests
                .Where(fr => (fr.Status == "Accepted" || fr.Status == "accepted") && (fr.FromUserId == userId || fr.ToUserId == userId))
                .Select(fr => fr.FromUserId == userId ? fr.ToUserId : fr.FromUserId)
                .ToListAsync();

            return Ok(friends);
        }
        
    }
}

