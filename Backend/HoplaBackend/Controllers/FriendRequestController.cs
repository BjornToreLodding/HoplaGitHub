using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.Models;

namespace MyApp.Controllers
{
 
    [Route("friends")]
    [ApiController]
        public class FriendsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public FriendsController(AppDbContext context)
        {
            _context = context;
        }


        [HttpGet("{userId}")]
        public async Task<IActionResult> GetFriends(int userId)
        {
            var friends = await _context.FriendRequests
                .Where(fr => (fr.Status == "Accepted" || fr.Status == "accepted") && (fr.FromUserId == userId || fr.ToUserId == userId))
                .Select(fr => new
                {
                    FriendId = fr.FromUserId == userId ? fr.ToUserId : fr.FromUserId,
                    FriendName = fr.FromUserId == userId ? fr.ToUser.Name : fr.FromUser.Name,
                    FriendAlias = fr.FromUserId == userId ? fr.ToUser.Alias : fr.FromUser.Alias 
                })
                .ToListAsync();

            return Ok(friends);
        }

        [HttpGet("requests/{userId}")]
        public async Task<IActionResult> GetFriendRequestss(int userId)
        {
            var friendrequests = await _context.FriendRequests
                //.Include(fr => fr.FromUserId)
                .Where(fr => (fr.Status == "pending" || fr.Status == "Pending") && fr.ToUserId == userId)
                .Select(fr => new
                {
                    id = fr.Id,
                    fromUserId =  fr.FromUserId,
                    fromUserAlias = fr.FromUser.Alias,
                    fromUserName = fr.FromUser.Name,
                })
                .ToListAsync();

            if (friendrequests == null)
            {
                return NotFound(); // Returnerer 404 hvis hesten ikke finnes
            }
            return Ok(friendrequests);
        }

    }
}
