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
        /*
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
        */
        [HttpGet("{userId}")] // Returnerer meldinger mellom to brukere eller siste melding per bruker
        public async Task<IActionResult> GetMessagesBetweenUsers(
            int userId,
            [FromQuery] int? id) // id er optional
        {
            // 🟢 Hvis id er spesifisert: Hent ALLE meldinger mellom userId og id
            if (id.HasValue)
            {
                var messages = await _context.Messages
                    .Include(m => m.Sender)  
                    .Include(m => m.Receiver) 
                    .Where(m => (m.SUserId == userId || m.RUserId == userId) && 
                                (m.SUserId == id.Value || m.RUserId == id.Value))
                    .OrderBy(m => m.SentAt)
                    .Select(m => new 
                    {
                        Content = m.MessageText,
                        Timestamp = m.SentAt,
                        SenderId = m.SUserId,
                        SenderName = m.Sender.Name,
                        ReceiverId = m.RUserId,
                        ReceiverName = m.Receiver.Name
                    })
                    .ToListAsync();

                return Ok(messages);
            }

            // 🟢 Hvis id IKKE er spesifisert: Hent siste melding per unike samtalepartner
            var lastMessages = await _context.Messages
                .Where(m => m.SUserId == userId || m.RUserId == userId) // 🔹 Finn meldinger til/fra bruker
                .OrderByDescending(m => m.SentAt)  // 🔹 Sorter etter nyeste melding først
                .GroupBy(m => m.SUserId == userId ? m.RUserId : m.SUserId) // 🔹 Grupper etter samtalepartner
                .Select(g => g.OrderByDescending(m => m.SentAt).First()) //  // 🔹 Velg kun den nyeste meldingen i hver gruppe
                .ToListAsync(); // 🔹 Hent resultatet før vi inkluderer brukere

            // Nå kan vi hente avsender- og mottakerinfo via en ny query
            var messagesWithUsers = await _context.Messages
                .Where(m => lastMessages.Select(lm => lm.Id).Contains(m.Id)) // 🔹 Finn meldingene vi akkurat fant
                .Include(m => m.Sender) // ✅ Henter brukerinfo
                .Include(m => m.Receiver)
                .OrderByDescending(m => m.SentAt)
                .Select(m => new
                {
                    Content = m.MessageText,
                    Timestamp = m.SentAt,
                    SenderId = m.SUserId,
                    SenderName = m.Sender.Name,
                    ReceiverId = m.RUserId,
                    ReceiverName = m.Receiver.Name
                })
                .ToListAsync();

            return Ok(messagesWithUsers);
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

