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

        [HttpGet("all/{id}")] //returnerer alle meldinger til bruker sortert på sist sendte melding.
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
                //ikke lagt arbeid i å få denne til å sortere
                name = user.Name,
                
            });
        }
        [HttpGet("{userId}")] // Returnerer meldinger mellom 2 brukere, sortert på tidspunkt
        public async Task<IActionResult> GetMessagesBetweenUsers(
            int userId,
            [FromQuery] int? id
            ) 
        {
            var messages = await _context.Messages
            .Include(m => m.Sender)  // ✅ Henter brukerobjektet for avsender
            .Include(m => m.Receiver) // ✅ Henter brukerobjektet for mottaker
            .Where(m => (m.SUserId == userId || m.RUserId == userId) && (m.SUserId == id || m.RUserId == id))
            .OrderBy(m => m.SentAt)
            .Select(m => new 
            {
                //Id = m.Id,
                Content = m.MessageText,
                Timestamp = m.SentAt,
                SenderId = m.SUserId,
                SenderName = m.Sender.Name,  // ✅ Henter navn på avsender
                ReceiverId = m.RUserId,
                ReceiverName = m.Receiver.Name  // ✅ Henter navn på mottaker
            })
            .ToListAsync();

            return Ok(messages);
        }

        /*[HttpGet("{userId}/{id}")] //returnerer alle meldinger som er sendt mellom 2 brukere. Trenger bedre navn
        public async Task<IActionResult> GetMessage(int userId, int id) //Denne lages senere
        {
        
            var messages = await _context.Messages
                .Where(m => (m.SUserId == userId || m.RUserId == userId) && (m.SUserId == id || m.RUserId == id))
                .Select(m => m.SUserId == userId ? m.SUserId : m.RUserId)
                .ToListAsync();

            return Ok(messages);
        }
        */
        
    }
}

