// Plassering: Controllers/UserController.cs
using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
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
        public async Task<IActionResult> GetUser(int id)
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
        [HttpGet("{userId}/friends")]
        public async Task<IActionResult> GetFriends(int userId)
        {
        
            var friends = await _context.FriendRequests
                .Where(fr => (fr.Status == "Accepted" || fr.Status == "accepted") && (fr.FromUserId == userId || fr.ToUserId == userId))
                .Select(fr => fr.FromUserId == userId ? fr.ToUserId : fr.FromUserId)
                .ToListAsync();

            return Ok(friends);
        }

        //Denne er flyttet til api/mock/createcontent i MockController.cs
        /*
        [HttpGet("/createcontent")]
        public async Task<IActionResult> CreateDbContent()
        {
            if(_context.Users.Any()) {return NoContent();}
            //legg til innhold her
            //legger til Users
            var users = UserMock.GetUsers();
            //foreach (var user in users)
        //{
            _context.Users.AddRange(users);
            await _context.SaveChangesAsync();  // Viktig! Brukere må eksistere før vi legger til hester.
        //}
            var existingUsers = _context.Users.ToList();
            //legger til Horses
            // 🔹 3. Legg til hester med eksisterende brukere
            var horses = HorseMock.GetHorses(existingUsers);
            _context.Horses.AddRange(horses);

            // 🔹 4. Legg til andre relasjoner (Friendrequests, Messages, etc.)
            var friendRequests = FriendRequestMock.GetFriendRequests();
            _context.FriendRequests.AddRange(friendRequests);
            /*var horses = HorseMock.GetHorses();
            foreach (var horse in horses)
        {
            _context.Horses.Add(horse);
        }
        
            //legger til Friendrequets
            var friendRequets = FriendRequestMock.GetFriendRequests();
            foreach (var friendrequest in friendRequets)
        {
            _context.FriendRequests.Add(friendrequest);
        }
            //legger til Messages
            //legger til Rides
            //legger til RideDetails
            //Legger til Trails
            //legger til Filters
            //legger til Stable
            //legger til StableUsers //kan vurdere å kalle det Stablemembers/Members
            //legger til Stablemessages

            await _context.SaveChangesAsync();
            return Created();
        }
        */

    }
}
