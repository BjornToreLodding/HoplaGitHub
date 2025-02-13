using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using MyApp.Models;
using MyApp.DTOs;

namespace MyApp.Controllers
{
 
    [Route("userrelations")]
    [ApiController]
        public class UserRelationsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public UserRelationsController(AppDbContext context)
        {
            _context = context;
        }


        [HttpGet("friends/{userId}")]
        public async Task<IActionResult> GetFriends(int userId)
        {
            var friends = await _context.UserRelations
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
            var friendrequests = await _context.UserRelations
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
        [HttpGet("blocked/{userId}")]
        public async Task<IActionResult> GetBlockedUsers(int userId)
        {
            var blockedUsers = await _context.UserRelations
                .Where(fr => fr.FromUserId == userId && fr.Status == "blocked")
                .Select(fr => new 
                {
                    BlockedUserId = fr.ToUserId
                })
                .ToListAsync();

            return Ok(blockedUsers);
        }

        [HttpPost("new/{userId}")]
        public async Task<IActionResult> CreateFriendRequest([FromBody] CreateUserRelationDto requestDto)
        {
            if (requestDto.FromUserId == requestDto.ToUserId)
            {
                return BadRequest(new { message = "You cannot send a friend request to yourself." });
            }

            // Sjekk om det allerede finnes en FriendRequest mellom brukerne
            var existingRequest = await _context.UserRelations
                .Where(fr => (fr.FromUserId == requestDto.FromUserId && fr.ToUserId == requestDto.ToUserId)
                        || (fr.FromUserId == requestDto.ToUserId && fr.ToUserId == requestDto.FromUserId))
                .FirstOrDefaultAsync();

            if (existingRequest != null)
            {
                return BadRequest(new { message = "A FriendRequest already exists between these users." });
            }

            // Oppretter en ny FriendRequest eller blokkering
            var newFriendRequest = new UserRelation
            {
                Id = Guid.NewGuid(),
                FromUserId = requestDto.FromUserId,
                ToUserId = requestDto.ToUserId,
                Status = requestDto.Status.ToLower() == "blocked" ? "blocked" : "pending"
            };

            // Hvis status er "blocked", sørg for at FromUserId er den som blokkerer
            if (newFriendRequest.Status == "blocked" && requestDto.FromUserId != requestDto.BlockingUserId)
            {
                (newFriendRequest.FromUserId, newFriendRequest.ToUserId) = (newFriendRequest.ToUserId, newFriendRequest.FromUserId);
            }

            _context.UserRelations.Add(newFriendRequest);
            await _context.SaveChangesAsync();

            return Ok(new { message = newFriendRequest.Status == "blocked" ? "User has been blocked." : "FriendRequest sent successfully.", newFriendRequest });
        }

        [HttpPut("status/{friendRequestId}")] //kan også blokkere brukere med denne.
        public async Task<IActionResult> UpdateFriendRequestStatus(Guid friendRequestId, [FromBody] UpdateUserRelationStatusDto requestDto)
        {
            var friendRequest = await _context.UserRelations.FindAsync(friendRequestId);

            if (friendRequest == null)
            {
                return NotFound(new { message = "FriendRequest not found" });
            }

            string currentStatus = friendRequest.Status.Trim().ToLower();
            string newStatus = requestDto.Status.Trim().ToLower();

            // Gyldige statusoverganger
            var validStatusChanges = new Dictionary<string, HashSet<string>>(StringComparer.OrdinalIgnoreCase)
            {
                { "pending", new HashSet<string> { "accepted", "declined", "blocked" } },
                { "accepted", new HashSet<string> { "deleted", "blocked" } },
                { "declined", new HashSet<string> { "pending", "blocked" } },
                { "deleted", new HashSet<string> { "pending", "blocked" } },
                // Hvis blocked så kan man enten sende ny friendrequest eller fjerne blokkeringen uten å sende ny request.  
                // declined/deleted i tilfellene man vil fjerne en blokkering uten å sende ny friendrequest.
                { "blocked", new HashSet<string> { "pending", "declined", "deleted" } }                 
            };

            // Blokkering - Sjekk om status er "blocked" og swappe FromUserId / ToUserId
            if (newStatus == "blocked" && friendRequest.Status != "blocked")
            {
                // Bytt rekkefølge hvis den som blokkerer ikke allerede er FromUserId
                if (friendRequest.ToUserId == friendRequest.FromUserId)
                {
                    // Swappe FromUserId og ToUserId
                    (friendRequest.FromUserId, friendRequest.ToUserId) = (friendRequest.ToUserId, friendRequest.FromUserId);
                }
            }

            // Forhindre endringer hvis status allerede er "blocked" (unntatt oppheving)
            if (currentStatus == "blocked" && newStatus != "pending")
            {
                return BadRequest(new { message = "Cannot change status when blocked. Only unblocking is allowed." });
            }

            // Sjekk om statusendringen er gyldig
            if (!validStatusChanges.ContainsKey(currentStatus) || !validStatusChanges[currentStatus].Contains(newStatus))
            {
                return BadRequest(new { message = $"Invalid status change from '{currentStatus}' to '{newStatus}'." });
            }

            // Oppdater statusen
            friendRequest.Status = newStatus;
            _context.UserRelations.Update(friendRequest);
            await _context.SaveChangesAsync();

            return Ok(new { message = $"FriendRequest status updated to '{newStatus}' successfully", friendRequest });
        }

        /* Fungerende versjon. Backup før implementering av blokkering
        public async Task<IActionResult> UpdateFriendRequestStatus(Guid friendRequestId, [FromBody] UpdateFriendRequestStatusDto requestDto)
        {
            var friendRequest = await _context.FriendRequests.FindAsync(friendRequestId);

            if (friendRequest == null)
            {
                return NotFound(new { message = "FriendRequest not found" });
            }

            // Rens og konverter status til små bokstaver
            string currentStatus = friendRequest.Status.Trim().ToLower();
            string newStatus = requestDto.Status.Trim().ToLower();

            // Definer GYLDIGE statusendringer (fra -> til)
            var validStatusChanges = new Dictionary<string, HashSet<string>>(StringComparer.OrdinalIgnoreCase)
            {
                { "pending", new HashSet<string> { "accepted", "declined" } },
                { "accepted", new HashSet<string> { "deleted" } },
                { "declined", new HashSet<string> { "pending" } },
                { "deleted", new HashSet<string> { "pending" } }
            };

            // Sjekk om NÅVÆRENDE status har lovlige endringer
            if (!validStatusChanges.ContainsKey(currentStatus) || !validStatusChanges[currentStatus].Contains(newStatus))
            {
                return BadRequest(new { message = $"Invalid status change from '{currentStatus}' to '{newStatus}'." });
            }

            // Oppdater statusen
            friendRequest.Status = newStatus;
            _context.FriendRequests.Update(friendRequest);
            await _context.SaveChangesAsync();

            return Ok(new { message = $"FriendRequest status updated to '{newStatus}' successfully", friendRequest });
        }
        */

    }
}
