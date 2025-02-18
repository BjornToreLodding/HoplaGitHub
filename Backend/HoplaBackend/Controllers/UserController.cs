// Plassering: Controllers/UserController.cs
using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.DTOs;
using MyApp.Models;

namespace MyApp.Controllers
{
 
    [Route("users")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly AppDbContext _context;

        public UserController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetUser(Guid id)
        {
            var user = await _context.Users.FindAsync(id);

            if (user == null)
            {
                return NotFound(); // Returnerer 404 hvis brukeren ikke finnes
            }

            return Ok(new
            {
                name = user.Name,
                email = user.Email,
                password_hash = user.PasswordHash,
                created_at = user.CreatedAt
            });
        }
        [HttpPost("new")]
        public async Task<IActionResult> CreateUser([FromBody] CreateUserDto requestDto)
        {
            /*
            if (requestDto.FromUserId == requestDto.ToUserId)
            {
                return BadRequest(new { message = "You cannot send a friend request to yourself." });
            }

            // Sjekk om relasjonen allerede eksisterer
            var existingRelation = await _context.UserRelations
                .FirstOrDefaultAsync(ur => 
                    (ur.FromUserId == requestDto.FromUserId && ur.ToUserId == requestDto.ToUserId) ||
                    (ur.FromUserId == requestDto.ToUserId && ur.ToUserId == requestDto.FromUserId));

            if (existingRelation != null)
            {
                return Conflict(new { message = "A relation already exists between these users." });
            }
            */
            // Opprett venneforespørsel
            var userData = new User
            {
                Id = Guid.NewGuid(),
                Name = requestDto.Name,
                Alias = requestDto.Alias,
                Email = requestDto.Email,
                PasswordHash = requestDto.PasswordHash
            };

            _context.Users.Add(userData);
            await _context.SaveChangesAsync();
            return Ok(userData);
        }

    }
}