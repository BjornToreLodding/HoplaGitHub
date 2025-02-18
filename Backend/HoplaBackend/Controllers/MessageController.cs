//using MyApp.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.DTOs;
using MyApp.Models;

namespace MyApp.Controllers;


[Route("messages")]
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
        Guid userId,
        [FromQuery] Guid? id) // id er optional, men hvis spesifisert så returneres alle meldinger mellom userId og Id
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
    
    [HttpPost("send")]
    public async Task<IActionResult> SendMessage([FromBody] CreateMessageDto requestDto)
    {
        if (requestDto.SenderId == requestDto.ReceiverId)
        {
            return BadRequest(new { message = "You cannot send a message to yourself." });
        }

        var sender = await _context.Users.FindAsync(requestDto.SenderId);
        var receiver = await _context.Users.FindAsync(requestDto.ReceiverId);

        if (sender == null || receiver == null)
        {
            return NotFound(new { message = "Sender or receiver not found." });
        }
        Console.WriteLine("milestone1");
        var newMessage = new Message
        {
            Id = Guid.NewGuid(),
            SUserId = requestDto.SenderId,
            RUserId = requestDto.ReceiverId,
            MessageText = requestDto.Content,
            SentAt = DateTime.UtcNow
        };
        Console.WriteLine("milestone2");
        _context.Messages.Add(newMessage);
        await _context.SaveChangesAsync();

        // Hent meldingen på nytt med `Include()` for å få med `Sender` og `Receiver`
        var savedMessage = await _context.Messages
            .Include(m => m.Sender)   // Sørg for at senderens info lastes inn
            .Include(m => m.Receiver) // Sørg for at mottakerens info lastes inn
            .FirstOrDefaultAsync(m => m.Id == newMessage.Id);

        if (savedMessage == null)
            {
                return StatusCode(500, new { message = "Error retrieving saved message from database." });
            }
        return Ok(new
        {
            id = savedMessage.Id,
            senderId = savedMessage.Sender.Id,
            senderName = savedMessage.Sender?.Name,
            senderAlias = savedMessage.Sender?.Alias,
            reveiverId = savedMessage.Receiver.Id,
            receiverName = savedMessage.Receiver?.Name,
            receiverAlias = savedMessage.Receiver?.Alias,
            messageText = savedMessage.MessageText,
            sentAt = savedMessage.SentAt
        });
    }

    
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