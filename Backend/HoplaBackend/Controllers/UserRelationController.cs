using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.DTOs;

namespace HoplaBackend.Controllers
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
        public async Task<IActionResult> GetFriends(Guid userId)
        {
            var friends = await _context.UserRelations
                .Where(ur => (ur.Status == "Accepted" || ur.Status == "accepted") && (ur.FromUserId == userId || ur.ToUserId == userId))
                .Select(ur => new
                {
                    FriendId = ur.FromUserId == userId ? ur.ToUserId : ur.FromUserId,
                    FriendName = ur.FromUserId == userId ? ur.ToUser.Name : ur.FromUser.Name,
                    FriendAlias = ur.FromUserId == userId ? ur.ToUser.Alias : ur.FromUser.Alias 
                })
                .ToListAsync();

            return Ok(friends);
        }

        [HttpGet("requests/{userId}")]
        public async Task<IActionResult> GetFriendRequestss(Guid userId)
        {
            var friendrequests = await _context.UserRelations
                //.Include(fr => fr.FromUserId)
                .Where(ur => (ur.Status == "pending" || ur.Status == "Pending") && ur.ToUserId == userId)
                .Select(ur => new
                {
                    id = ur.Id,
                    fromUserId =  ur.FromUserId,
                    fromUserAlias = ur.FromUser.Alias,
                    fromUserName = ur.FromUser.Name,
                })
                .ToListAsync();

            if (friendrequests == null)
            {
                return NotFound(); // Returnerer 404 hvis hesten ikke finnes
            }
            return Ok(friendrequests);
        }
        [HttpGet("blocked/{userId}")]
        public async Task<IActionResult> GetBlockedUsers(Guid userId)
        {
            var blockedUsers = await _context.UserRelations
                .Where(ur => ur.FromUserId == userId && ur.Status == "blocked")
                .Select(ur => new 
                {
                    BlockedUserId = ur.ToUserId,
                    BlockedUserName = ur.ToUser.Name,
                    BlockedUserAlias = ur.ToUser.Alias
                })
                .ToListAsync();

            return Ok(blockedUsers);
        }

        //
        //Det ble mye feil på denne ved endring fra id til Guid. Deaktiverer den enn så lenge for å gjøre resten av konverteringen ferdig
        //
        
        [HttpPost("friendrequest")]
        public async Task<IActionResult> CreateFriendRequest([FromBody] CreateUserRelationDto requestDto)
        {
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

            // Opprett venneforespørsel
            var newRelation = new UserRelation
            {
                Id = Guid.NewGuid(),
                FromUserId = requestDto.FromUserId,
                ToUserId = requestDto.ToUserId,
                Status = "pending"
            };

            _context.UserRelations.Add(newRelation);
            await _context.SaveChangesAsync();
            return Ok(newRelation);
            //return CreatedAtAction(nameof(GetRelationById), new { id = newRelation.Id }, newRelation);
        }
        [HttpPost("block")]
        public async Task<IActionResult> BlockUser([FromBody] CreateUserRelationDto requestDto)
        {
            if (requestDto.FromUserId == requestDto.ToUserId)
            {
                return BadRequest(new { message = "You cannot block yourself." });
            }

            // Sjekk om FromUserId allerede har blokkert ToUserId
            var existingRelation = await _context.UserRelations
                .FirstOrDefaultAsync(ur => ur.FromUserId == requestDto.FromUserId && ur.ToUserId == requestDto.ToUserId);

            if (existingRelation != null)
            {
                if (existingRelation.Status == "blocked")
                {
                    return Conflict(new { 
                        message = "You have already blocked this user."
                    });
                }

                // Oppdater eksisterende relasjon til "blocked"
                existingRelation.Status = "blocked";
                _context.UserRelations.Update(existingRelation);
                await _context.SaveChangesAsync();

                return Ok(new { message = "User successfully blocked.", existingRelation });
            }

            // Sjekk om det finnes en blokkering motsatt vei (ToUserId → FromUserId)
            var reverseRelation = await _context.UserRelations
                .FirstOrDefaultAsync(ur => ur.FromUserId == requestDto.ToUserId && ur.ToUserId == requestDto.FromUserId);

            if (reverseRelation != null)
            {
                // Bytt om FromUserId og ToUserId for å vise hvem som har blokkert
                reverseRelation.FromUserId = requestDto.FromUserId;
                reverseRelation.ToUserId = requestDto.ToUserId;
                reverseRelation.Status = "blocked";

                _context.UserRelations.Update(reverseRelation);
                await _context.SaveChangesAsync();

                return Ok(new { message = "Block status updated.", reverseRelation });
            }

            // Hvis ingen relasjon finnes, opprett en ny blokkering
            var newBlockRelation = new UserRelation
            {
                Id = Guid.NewGuid(),
                FromUserId = requestDto.FromUserId,
                ToUserId = requestDto.ToUserId,
                Status = "blocked"
            };

            _context.UserRelations.Add(newBlockRelation);
            await _context.SaveChangesAsync();

            return Ok(newBlockRelation);
            //return CreatedAtAction(nameof(GetRelationById), new { id = newBlockRelation.Id }, newBlockRelation);
        }
        
        /*
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
        */
        [HttpPut("{userRelationId}")] //Denne håndterer alle statusendringene.
        public async Task<IActionResult> UpdateFriendRequestStatus(Guid userRelationId, [FromBody] UpdateUserRelationStatusDto requestDto)
        {
            var userRelation = await _context.UserRelations.FindAsync(userRelationId);

            if (userRelation == null)
            {
                return NotFound(new { message = "FriendRequest not found" });
            }

            string currentStatus = userRelation.Status.Trim().ToLower();
            string newStatus = requestDto.Status.Trim().ToLower();

            // Gyldige statusoverganger
            var validStatusChanges = new Dictionary<string, HashSet<string>>(StringComparer.OrdinalIgnoreCase)
            {
                { "pending", new HashSet<string> { "accepted", "declined", "blocked" } },
                { "accepted", new HashSet<string> { "deleted", "blocked" } },
                { "declined", new HashSet<string> { "pending", "blocked" } },
                { "deleted", new HashSet<string> { "pending", "blocked" } },
                { "blocked", new HashSet<string> { "pending", "declined", "deleted" } } // Unblocking scenarios
            };

            // Forhindre endringer hvis status allerede er "blocked" (unntatt oppheving)
            if (currentStatus == "blocked" && newStatus != "pending" && newStatus != "declined" && newStatus != "deleted")
            {
                return BadRequest(new { message = "Cannot change status when blocked. Only unblocking is allowed." });
            }

            // Sjekk om statusendringen er gyldig
            if (!validStatusChanges.ContainsKey(currentStatus) || !validStatusChanges[currentStatus].Contains(newStatus))
            {
                return BadRequest(new { message = $"Invalid status change from '{currentStatus}' to '{newStatus}'." });
            }

            // Håndtering av blokkering (krever FromUserId og ToUserId)
            if (newStatus == "blocked")
            {
                if (requestDto.FromUserId == null || requestDto.ToUserId == null)
                {
                    return BadRequest(new { message = "FromUserId and ToUserId are required when blocking a user." });
                }

                // Hvis relasjonen allerede er blokkert, avbryt
                if (currentStatus == "blocked")
                {
                    return Conflict(new { message = "This user is already blocked." });
                }

                // Bytt ToUserId og FromUserId om nødvendig
                if (userRelation.ToUserId == requestDto.FromUserId)
                {
                    (userRelation.FromUserId, userRelation.ToUserId) = (userRelation.ToUserId, userRelation.FromUserId);
                }
            }

            // Oppdater statusen
            userRelation.Status = newStatus;
            _context.UserRelations.Update(userRelation);
            await _context.SaveChangesAsync();

            return Ok(new { message = $"FriendRequest status updated to '{newStatus}' successfully", userRelation });
        }
        
        /* Fungerende versjon av den ovenfor.
        [HttpPut("{userRelationId}")] //kan også blokkere brukere med denne.
        public async Task<IActionResult> UpdateFriendRequestStatus(Guid userRelationId, [FromBody] UpdateUserRelationStatusDto requestDto)
        {
            var userRelation = await _context.UserRelations.FindAsync(userRelationId);

            if (userRelation == null)
            {
                return NotFound(new { message = "FriendRequest not found" });
            }

            string currentStatus = userRelation.Status.Trim().ToLower();
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
            if (newStatus == "blocked" && userRelation.Status != "blocked")
            {
                // Bytt rekkefølge hvis den som blokkerer ikke allerede er FromUserId
                if (userRelation.ToUserId == userRelation.FromUserId)
                {
                    // Swappe FromUserId og ToUserId
                    (userRelation.FromUserId, userRelation.ToUserId) = (userRelation.ToUserId, userRelation.FromUserId);
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
            userRelation.Status = newStatus;
            _context.UserRelations.Update(userRelation);
            await _context.SaveChangesAsync();

            return Ok(new { message = $"FriendRequest status updated to '{newStatus}' successfully", userRelation });
        }
        */

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
        [HttpDelete("{fromUserId}/{toUserId}")]
        public async Task<IActionResult> UnblockUser(Guid fromUserId, Guid toUserId)
        {
            var relation = await _context.UserRelations
                .FirstOrDefaultAsync(ur => ur.FromUserId == fromUserId && ur.ToUserId == toUserId && ur.Status == "blocked");

            if (relation == null)
            {
                return NotFound(new { message = "No blocking relation found." });
            }

            // Slett blokkeringen
            _context.UserRelations.Remove(relation);
            await _context.SaveChangesAsync();

            return Ok(new { message = "Block removed successfully." });
        }


    }
}
