using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.DTOs;
using HoplaBackend.Helpers;
using HoplaBackend.Services;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Configuration.UserSecrets;

namespace HoplaBackend.Controllers;


[Route("userrelations")]
[ApiController]
public class UserRelationsController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;
    private readonly UserRelationService _userRelationService;

    public UserRelationsController(Authentication authentication, AppDbContext context, UserRelationService userRelationService)
    {
        _authentication = authentication;
        _context = context;
        _userRelationService = userRelationService;
    }
    [Authorize]
    [HttpGet("friends")]
    public async Task<IActionResult> GetFriends([FromQuery] Guid? userId)
    { //userId hentes fra token, men for testing så ligger det også en query som lar oss sjekke andre userId enn token.
        if (!userId.HasValue) // Henter userId fra token. 
        {
            //Debugging
            Console.WriteLine("Token claims:");
            foreach (var claim in User.Claims)
            {
                Console.WriteLine($"Type: {claim.Type}, Value: {claim.Value}");
            }

            // Hent brukerens ID fra tokenet
            var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

            if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
            {
                return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
            }         
            userId = parsedUserId;
        }
        var friends = await _context.UserRelations
        //Det skal være lagret som "FRIENDS", men har vært ombygd og kan være noe gammelt friend, friends, Friends, Friend eller FRIEND som jeg ikke har fanget opp.
            .Where(ur => (ur.Status == "FRIENDS") && (ur.FromUserId == userId || ur.ToUserId == userId))
            .Select(ur => new
            {
                FriendId = ur.FromUserId == userId ? ur.ToUserId : ur.FromUserId,
                FriendName = ur.FromUserId == userId ? ur.ToUser.Name : ur.FromUser.Name,
                FriendAlias = ur.FromUserId == userId ? ur.ToUser.Alias : ur.FromUser.Alias,
                FriendPictureURL = ur.FromUserId == userId 
                ? (ur.ToUser.PictureUrl.Contains("http") ? ur.ToUser.PictureUrl  : "https://files.hopla.no/" + ur.ToUser.PictureUrl) + "?w=64&h=64&fit=crop" 
                : (ur.FromUser.PictureUrl.Contains("http") ? ur.FromUser.PictureUrl  : "https://files.hopla.no/" + ur.FromUser.PictureUrl) + "?w=64&h=64&fit=crop",
                
            })
            .OrderBy(f => f.FriendAlias) // Sorter etter FriendAlias
            .ToListAsync();

        return Ok(friends);
    }

    [Authorize]
    [HttpGet("following")]
    public async Task<IActionResult> GetFollowingUsers([FromQuery] Guid? userId)
    {  //userId hentes fra token, men for testing så ligger det også en query som lar oss sjekke andre userId enn token.
        if (!userId.HasValue) // Henter userId fra token. 
            {
            //Debugging, fjernes når alt virker
            Console.WriteLine("Token claims:");
            foreach (var claim in User.Claims)
            {
                Console.WriteLine($"Type: {claim.Type}, Value: {claim.Value}");
            }

            // Hent brukerens ID fra tokenet
            var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");
            if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
            {
                return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
            }
            userId = parsedUserId;            
        }

        // Hent alle brukere som denne brukeren følger
        var following = await _context.UserRelations
            .Where(ur =>
                ur.FromUserId == userId &&
                ur.Status == "FOLLOWING" &&
                // Ikke inkluder hvis de er venner
                !_context.UserRelations.Any(fr =>
                    fr.Status == "FRIENDS" &&
                    (
                        (fr.FromUserId == userId && fr.ToUserId == ur.ToUserId) ||
                        (fr.FromUserId == ur.ToUserId && fr.ToUserId == userId)
                    )
                )
            )
            .Include(ur => ur.ToUser)
            .OrderBy(ur => ur.ToUser.Alias)
            .Select(ur => new
            {
                    FollowingUserId = ur.ToUserId,
                    FollowingUserName = ur.ToUser.Name,
                    FollowingUserAlias = ur.ToUser.Alias,
                    FollowingUserPicture = (ur.ToUser.PictureUrl.Contains("http") ? ur.ToUser.PictureUrl  : "https://files.hopla.no/" + ur.ToUser.PictureUrl) + "?w=64&h=64&fit=crop"
                })
            .ToListAsync();


        return Ok(following);
    }
    [Authorize]
    [HttpGet("requests")]
    public async Task<IActionResult> GetFriendRequestss([FromQuery] Guid? userId)
    {
    
        //Debugging, fjernes når alt virker
        Console.WriteLine("Token claims:");
        foreach (var claim in User.Claims)
        {
            Console.WriteLine($"Type: {claim.Type}, Value: {claim.Value}");
        }

        // Hent brukerens ID fra tokenet
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        if (!userId.HasValue) // Henter userId fra token. 
        {
            userId = parsedUserId;
        }
    
        var friendrequests = await _context.UserRelations
            //.Include(fr => fr.FromUserId)
            .Where(ur => (ur.Status == "PENDING" || ur.Status == "Pending") && ur.ToUserId == userId)
            .Select(ur => new
            {
                id = ur.Id,
                fromUserId = ur.FromUserId,
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

        
    
    [Authorize]
    [HttpGet("blocked")]
    public async Task<IActionResult> GetBlockedUsers()
    {
        // Hent brukerens ID fra tokenet
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");
        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        var blockedUsers = await _context.UserRelations
            .Where(ur => ur.FromUserId == parsedUserId && ur.Status == "blocked")
            .Select(ur => new 
            {   
                ur.Id,
                BlockedUserId = ur.ToUserId,
                BlockedUserName = ur.ToUser.Name,
                BlockedUserAlias = ur.ToUser.Alias
            })
            .ToListAsync();

        return Ok(blockedUsers);
    }
    [Authorize]
    [HttpPost("pending")]
    public async Task<IActionResult> SendFriendRequest([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Kan ikke sende venneforespørsel til deg selv" });

        var relations = await GetRelations(userId, request.TargetUserId);

        if (relations.Any(r => r.Status is "BLOCK" or "PENDING" or "FRIENDS"))
            return BadRequest(new { message = "Venneforespørsel er ikke tillatt i denne relasjonsstatusen" });

        var relation = new UserRelation
        {
            FromUserId = userId,
            ToUserId = request.TargetUserId,
            Status = "PENDING",
            CreatedAt = DateTime.UtcNow
        };

        _context.UserRelations.Add(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Venneforespørsel sendt" });
    }

    [Authorize]
    [HttpPost("block")]
    public async Task<IActionResult> BlockUser([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Kan ikke blokkere deg selv" });

        var relations = await GetRelations(userId, request.TargetUserId);

        if (relations.Any(r =>
                r.FromUserId == userId &&
                r.ToUserId == request.TargetUserId &&
                r.Status == "BLOCK"))
            return BadRequest(new { message = "Bruker er allerede blokkert" });

        _context.UserRelations.RemoveRange(relations); // Fjerner alle tidligere relasjoner

        var relation = new UserRelation
        {
            FromUserId = userId,
            ToUserId = request.TargetUserId,
            Status = "BLOCK",
            CreatedAt = DateTime.UtcNow
        };

        _context.UserRelations.Add(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Bruker blokkert" });
    }

    [Authorize]
    [HttpPost("follow")]
    public async Task<IActionResult> FollowUser([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Kan ikke følge deg selv" });

        var relations = await GetRelations(userId, request.TargetUserId);

        if (relations.Any(r =>
                r.FromUserId == userId && r.ToUserId == request.TargetUserId && r.Status == "FOLLOWING"))
            return BadRequest(new { message = "Du følger allerede denne brukeren" });

        if (relations.Any(r =>
                (r.Status == "FRIENDS") ||
                (r.FromUserId == request.TargetUserId && r.ToUserId == userId && r.Status == "BLOCK")))
            return BadRequest(new { message = "Kan ikke følge bruker grunnet blokkering eller vennskap" });

        if (relations.Any(r => r.Status == "BLOCK" && r.FromUserId == userId && r.ToUserId == request.TargetUserId))
            _context.UserRelations.RemoveRange(relations);
        var relation = new UserRelation
        {
            FromUserId = userId,
            ToUserId = request.TargetUserId,
            Status = "FOLLOWING",
            CreatedAt = DateTime.UtcNow
        };

        _context.UserRelations.Add(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Du følger nå brukeren" });
    }

    private async Task<List<UserRelation>> GetRelations(Guid userId, Guid targetUserId)
    {
        return await _context.UserRelations
            .Where(r =>
                (r.FromUserId == userId && r.ToUserId == targetUserId) ||
                (r.FromUserId == targetUserId && r.ToUserId == userId))
            .ToListAsync();
    }
    /*
    [Authorize]
    [HttpPost("following")]
    public async Task<IActionResult> FollowUser([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Kan ikke følge deg selv" });

        var existing = await _context.UserRelations
            .FirstOrDefaultAsync(r => r.FromUserId == userId && r.ToUserId == request.TargetUserId);

        if (existing != null)
            return BadRequest(new { message = "Relasjon finnes allerede" });

        var relation = new UserRelation
        {
            FromUserId = userId,
            ToUserId = request.TargetUserId,
            Status = "FOLLOWING",
            CreatedAt = DateTime.UtcNow
        };

        _context.UserRelations.Add(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Følger bruker" });
    }

    [Authorize]
    [HttpPost("pending")]
    public async Task<IActionResult> SendFriendRequest([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Kan ikke sende venneforespørsel til deg selv" });

        var existing = await _context.UserRelations
            .FirstOrDefaultAsync(r =>
                (r.FromUserId == userId && r.ToUserId == request.TargetUserId) ||
                (r.FromUserId == request.TargetUserId && r.ToUserId == userId));

        if (existing != null)
            return BadRequest(new { message = "Relasjon finnes allerede" });

        var relation = new UserRelation
        {
            FromUserId = userId,
            ToUserId = request.TargetUserId,
            Status = "PENDING",
            CreatedAt = DateTime.UtcNow
        };

        _context.UserRelations.Add(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Venneforespørsel sendt" });
    }

    [Authorize]
    [HttpPost("block")]
    public async Task<IActionResult> BlockUser([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Kan ikke blokkere deg selv" });

        var existing = await _context.UserRelations
            .FirstOrDefaultAsync(r => r.FromUserId == userId && r.ToUserId == request.TargetUserId);

        if (existing != null)
            return BadRequest(new { message = "Relasjon finnes allerede" });

        var relation = new UserRelation
        {
            FromUserId = userId,
            ToUserId = request.TargetUserId,
            Status = "BLOCK",
            CreatedAt = DateTime.UtcNow
        };

        _context.UserRelations.Add(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Bruker blokkert" });
    }
    */


    /*
    Tillatte kombinasjoner
    
    PENDING     ->  PUT     FRIENDS (slett evt FOLLOWING begge veier.)
    PENDING     ->  PUT     BLOCKED
    PENDING     ->  DELETE
    FOLLOWING   ->  POST    PENDING
    FOLLOWING   ->  PUT     BLOCKED
    FOLLOWING   ->  DELETE
    BLOCKED     ->  PUT     PENDING
    BLOCKED     ->  PUT     FOLLOWING
    */

    [Authorize]
    [HttpPut]
    public async Task<IActionResult> UpdateRelationStatus([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);
        if (userId == request.TargetUserId)
            return BadRequest(new { message = "Ugyldig målbruker" });

        var allowedStatuses = new[] { "PENDING", "FRIENDS", "BLOCK" }; //Må se på tillatte kombinasjoner

        if (!allowedStatuses.Contains(request.Status?.ToUpperInvariant()))
            return BadRequest(new { message = "Ugyldig statusverdi. Må være FOLLOWING, PENDING eller BLOCK." });
        //if (string.IsNullOrEmpty(request.Status))   return BadRequest(new { message = "Status må angis" });

        // Finn relasjonen begge veier
        var relation = await _context.UserRelations.FirstOrDefaultAsync(r =>
            (r.FromUserId == userId && r.ToUserId == request.TargetUserId) ||
            (r.FromUserId == request.TargetUserId && r.ToUserId == userId));

        if (relation == null)
            return NotFound(new { message = "Relasjon ikke funnet" });

        if (request.Status == "FRIENDS")
        {
            if (relation.Status != "PENDING" || relation.ToUserId != userId)
                return BadRequest(new { message = "Kan kun godta forespørsler som er sendt til deg" });

            relation.Status = "FRIENDS";

            // Fjern eventuell FOLLOWING i begge retninger
            var toRemove = await _context.UserRelations
                .Where(r =>
                    (r.Status == "FOLLOWING") &&
                    (
                        (r.FromUserId == userId && r.ToUserId == request.TargetUserId) ||
                        (r.FromUserId == request.TargetUserId && r.ToUserId == userId)
                    )
                )
                .ToListAsync();

            _context.UserRelations.RemoveRange(toRemove);
        }
        else if (request.Status == "BLOCK")
        {
            // Overstyr relasjonen og sett den ensidig
            relation.FromUserId = userId;
            relation.ToUserId = request.TargetUserId;
            relation.Status = "BLOCK";
        }
        else
        {
            relation.Status = request.Status;
        }

        await _context.SaveChangesAsync();
        return Ok(new { message = "Status oppdatert", status = request.Status });
    }


    [Authorize]
    [HttpDelete]
    public async Task<IActionResult> DeleteRelation([FromBody] UserRelationRequest request)
    {
        var userId = _authentication.GetUserIdFromToken(User);

        var relation = await _context.UserRelations
            .FirstOrDefaultAsync(r =>
                (r.FromUserId == userId && r.ToUserId == request.TargetUserId) ||
                (r.Status == "PENDING" || r.Status == "FRIENDS") && // Sjekk begge veier
                (r.FromUserId == request.TargetUserId && r.ToUserId == userId));

        if (relation == null)
            return NotFound(new { message = "Relasjon ikke funnet" });

        _context.UserRelations.Remove(relation);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Relasjon fjernet" });
    }

}

/*
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
  */