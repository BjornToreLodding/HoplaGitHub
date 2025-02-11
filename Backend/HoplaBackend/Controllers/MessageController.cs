//using MyApp.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.Models;

namespace MyApp.Controllers;


[Route("message")]
[ApiController]
public class MessageController : ControllerBase
{
    private readonly AppDbContext _context;

    public MessageController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet("{userId}")] // Returnerer meldinger mellom to brukere eller siste melding per bruker
    public async Task<IActionResult> GetMessagesBetweenUsers(
        int userId,
        [FromQuery] int? id) // id er optional, men hvis spesifisert så returneres alle meldinger mellom userId og Id
    {
        // Hvis id er spesifisert: Hent ALLE meldinger mellom userId og id
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

        // Hvis id IKKE er spesifisert: Hent siste melding per unike samtalepartner
        var lastMessages = await _context.Messages
            .Where(m => m.SUserId == userId || m.RUserId == userId) // Finn meldinger til/fra bruker
            .OrderByDescending(m => m.SentAt)  // Sorter etter nyeste melding først
            .GroupBy(m => m.SUserId == userId ? m.RUserId : m.SUserId) // Grupper etter samtalepartner
            .Select(g => g.OrderByDescending(m => m.SentAt).First()) // Velg kun den nyeste meldingen i hver gruppe
            .ToListAsync(); // Hent resultatet før vi inkluderer brukere

        // Nå kan vi hente avsender- og mottakerinfo via en ny query
        var messagesWithUsers = await _context.Messages
            .Where(m => lastMessages.Select(lm => lm.Id).Contains(m.Id)) // Finn meldingene vi akkurat fant
            .Include(m => m.Sender) // Henter brukerinfo
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
    /*
    [HttpPost("send")]
    public async Task<IActionResult> SendMessage([FromBody] MessageDto messageDto)
    {
        // Sjekk om avsender og mottaker eksisterer i databasen
        var senderExists = await _context.Users.AnyAsync(u => u.Id == messageDto.SenderId);
        var receiverExists = await _context.Users.AnyAsync(u => u.Id == messageDto.ReceiverId);

        if (!senderExists || !receiverExists)
        {
            return BadRequest("Sender or Receiver does not exist.");
        }

        // Opprett en ny melding basert på input fra `messageDto`
        var message = new Message
        {
            SUserId = messageDto.SenderId,
            RUserId = messageDto.ReceiverId,
            MessageText = messageDto.Content,
            SentAt = DateTime.UtcNow // Setter tidspunkt automatisk
        };

        // Lagre meldingen i databasen
        _context.Messages.Add(message);
        await _context.SaveChangesAsync();

        // Returner en suksessrespons med den lagrede meldingen
        return CreatedAtAction(nameof(GetMessageById), new { id = message.Id }, new
        {
            //MessageId = message.Id,
            SenderId = message.SUserId,
            ReceiverId = message.RUserId,
            Content = message.MessageText,
            Timestamp = message.SentAt
        });
    }
    */
    /*
    // Hjelpemetode for å hente en melding etter meldingsID, når det sendes melding.
    [HttpGet("{id}")] //Skrives om til query og integreres kanskje i den andre funksjonen.
    public async Task<IActionResult> GetMessageById(int id)
    {
        var message = await _context.Messages
            .Include(m => m.Sender)
            .Include(m => m.Receiver)
            .Where(m => m.Id == id) //
            .Select(m => new
            {
                MessageId = m.Id,
                Content = m.MessageText,
                Timestamp = m.SentAt,
                SenderId = m.SUserId,
                SenderName = m.Sender.Name,
                ReceiverId = m.RUserId,
                ReceiverName = m.Receiver.Name
            })
            .FirstOrDefaultAsync();

        if (message == null)
        {
            return NotFound();
        }

        return Ok(message);
    }
    */

    
}