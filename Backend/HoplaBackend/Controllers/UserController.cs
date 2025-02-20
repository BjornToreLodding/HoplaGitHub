// Plassering: Controllers/UserController.cs
using System.Drawing;
using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Serilog;
using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Models;
using HoplaBackend.Helpers;
using Helpers;


namespace HoplaBackend.Controllers;

[Route("users")]
[ApiController]
public class UserController : ControllerBase
{
    private readonly AppDbContext _context;

    public UserController(AppDbContext context)
    {
        _context = context;
    }
    [HttpGet("all")]
    public async Task<IActionResult> GetUsers()
    {
        var users = await _context.Users
            .Select(u => new
            {
                u.Id,
                u.Name,
                u.Alias
            })
            .ToListAsync();

        if (!users.Any())
        {
            Log.Warning("⚠️ Ingen brukere funnet!");
            return NotFound(new { message = "No users found." });
        }

        Log.Information("📢 GetAllUsers() ble kalt! Antall brukere: {UserCount}", users.Count);

        return Ok(users);
    }

    [HttpGet("int/{userId}")] 
    public async Task<IActionResult> GetIntUser(int userId)
    {
        //var endpointName = ControllerContext.ActionDescriptor.ActionName;
        var controllerName = ControllerContext.ActionDescriptor.ControllerName;
        Guid newGuid = CustomConvert.IntToGuid(controllerName, userId);
    
        return await GetUser(newGuid, false);
    }
    [HttpGet("{userId}")]
    public async Task<IActionResult> GetUser(
        Guid userId,
        [FromQuery] bool changepassword)
    {
        
        var user = await _context.Users.FindAsync(userId);

        if (user == null)
        {
            return NotFound(); // Returnerer 404 hvis brukeren ikke finnes
        }

        return Ok(new
        {
            id = user.Id,
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
    [HttpPut("{userId}")] //Denne håndterer alle statusendringene.
    public async Task<IActionResult> UpdateFriendRequestStatus(Guid userId, [FromBody] UpdateUserDto requestDto)
    {
        var userData = await _context.Users.FindAsync(userId);
        Console.ForegroundColor = ConsoleColor.Red;
        if (userData == null)
        {
            return NotFound(new { message = "User is doesn't exist" });
        }
        var newUserData = requestDto;
        if (newUserData.Name != userData.Name && newUserData.Name != "" && newUserData.Name != null)
        {
            Console.WriteLine("different Name");
            userData.Name = newUserData.Name;
        }
        if (newUserData.Alias != userData.Alias && newUserData.Alias != "" && newUserData.Alias != null)
        {
            Console.WriteLine("different Alias");
            userData.Alias = newUserData.Alias;
        } 
        if (newUserData.Email != userData.Email && newUserData.Email != "" && newUserData.Email != null)
        {
            Console.WriteLine("different Email");
            userData.Email = newUserData.Email;
        }
        if (newUserData.PasswordHash != userData.PasswordHash && newUserData.PasswordHash != "" && newUserData.PasswordHash != null)
        {
            Console.WriteLine("different PW");
            //Sjekk om diverse betingelser for passord er oppfylt kommer evt senere.
            userData.PasswordHash = newUserData.PasswordHash;
        }
        Console.ResetColor();
        // Oppdater informasjonen om brukeren.
        // Name
        // Alias
        // Email
        // PasswordHash 
        // ProfilePictureUrl 
        // Admin = false
        // Premium = false
        // VerifiedTrail = false
        
        _context.Users.Update(userData);
        await _context.SaveChangesAsync();

        return Ok(new { message = $"User userId was updated", userData });
    }

    [HttpDelete("delete/{userId}")]
    public async Task<IActionResult> DeleteUser(Guid userId)
    {
        var userData = await _context.Users.FindAsync(userId);

        // Sjekk om brukeren finnes
        if (userData == null)
        {
            return NotFound(new { message = "User not found." });
        }

        // Slett brukeren
        _context.Users.Remove(userData);
        await _context.SaveChangesAsync();

        return Ok(new { message = "User removed successfully." });
    }
}
