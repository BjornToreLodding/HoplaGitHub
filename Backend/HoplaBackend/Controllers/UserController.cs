// Plassering: Controllers/UserController.cs
using System.Drawing;
using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Serilog;
using HoplaBackend.Data;
using HoplaBackend.DTOs;
using HoplaBackend.Models;
using HoplaBackend.Helpers; //Denne har blitt grå og må legges til med linja under.
using HoplaBackend.Services;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using Microsoft.Extensions.Configuration.UserSecrets;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Microsoft.AspNetCore.Authentication;
using Microsoft.IdentityModel.Tokens;
using System.Security.Cryptography;


namespace HoplaBackend.Controllers;

[Route("users")]
[ApiController]
public class UserController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly Authentication _authentication;
    private readonly EmailService _emailService;
    private readonly IConfiguration _configuration;
    private readonly UserRelationService _userRelationService;
    private readonly UserHikeService _userHikeService;
    public UserController(Authentication authentication, AppDbContext context, EmailService emailService, IConfiguration configuration, UserRelationService userRelationService, UserHikeService userHikeService)
    {
        _authentication = authentication;
        _context = context;
        _emailService = emailService;
        _configuration = configuration;

        _userRelationService = userRelationService;
        _userHikeService = userHikeService;
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Password))
            return BadRequest("E-post og passord er påkrevd.");

        var existingUser = await _context.Users.AnyAsync(u => u.Email == request.Email);
        if (existingUser)
            return BadRequest("E-postadressen er allerede registrert.");

        var existingVerification = await _context.EmailVerifications.AnyAsync(ev => ev.Email == request.Email && !ev.IsUsed);
        if (existingVerification)
            return BadRequest("En verifikasjonsprosess er allerede i gang. Sjekk e-posten din og evt søppelpost.");

        // Hash passordet
        var passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

        // Generer token
        var token = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));

        var verification = new EmailVerification
        {
            Email = request.Email,
            PasswordHash = passwordHash,  // 📌 Lagres her til e-posten er bekreftet
            Token = token,
            ExpiryDate = DateTime.UtcNow.AddHours(24)
        };

        _context.EmailVerifications.Add(verification);
        await _context.SaveChangesAsync();

        // Send e-post
        var confirmEmailUrl = _configuration["ConfirmEmailUrl"];
        Console.WriteLine(confirmEmailUrl);

        var confirmationLink = $"{_configuration["ConfirmEmailUrl"]}/users/confirm-email?token={Uri.EscapeDataString(token)}";
        await _emailService.SendEmailAsync(request.Email, "Bekreft registrering",
            $"Klikk på lenken for å fullføre registreringen: <a href='{confirmationLink}'>Bekreft e-post</a>");

        return Ok("E-post sendt. Sjekk innboksen og trykk på lenken for å bekrefte registreringen. Sjekk evt søppelpost.");
    }
    [HttpGet("confirm-email")]
    public async Task<IActionResult> ConfirmEmail([FromQuery] string token)
    {
        var verification = await _context.EmailVerifications
            .FirstOrDefaultAsync(ev => ev.Token == token && !ev.IsUsed && ev.ExpiryDate > DateTime.UtcNow);

        if (verification == null)
            return BadRequest("Ugyldig eller utløpt token.");

        // Sjekk om brukeren allerede eksisterer (kan skje hvis de klikker på lenken flere ganger)
        var existingUser = await _context.Users.FirstOrDefaultAsync(u => u.Email == verification.Email);
        if (existingUser != null)
            return BadRequest("E-posten er allerede bekreftet.");

        // Opprett ny bruker i Users-tabellen
        var newUser = new User
        {
            Email = verification.Email,
            PasswordHash = verification.PasswordHash,
        };

        _context.Users.Add(newUser);
        verification.IsUsed = true;  // Marker token som brukt
        await _context.SaveChangesAsync();

        return Ok("E-post bekreftet! Du kan nå gå tilbake til appen og logge inn med epost og passord.");
    }
    [Authorize]    
    [HttpPut("update")]
    public async Task<IActionResult> UpdateUser([FromBody] UpdateUserDto request)
    {
        Console.WriteLine("users/update åpnet");
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }
        Console.WriteLine(request.Name);
        Console.WriteLine(request.Alias);
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == parsedUserId);
        if (user == null)
            return NotFound("Bruker ikke funnet.");
        Console.WriteLine("users/update checkpoint bruker ikke funnet");

        if (string.IsNullOrWhiteSpace(request.Name) || string.IsNullOrWhiteSpace(request.Alias))
            return BadRequest("Name or Alias cannot be empty");
        Console.WriteLine("users/update checkpoint Name or alias cannot be empty");

        // Oppdater informasjon
        if (!string.IsNullOrWhiteSpace(request.Name))
            user.Name = request.Name;

        if (!string.IsNullOrWhiteSpace(request.Alias))
            user.Alias = request.Alias;

        Console.WriteLine("users/update checkpoint før save og return ok");


        await _context.SaveChangesAsync();

        return Ok("Brukerinformasjon oppdatert.");
    }

/*    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Email))
            return BadRequest("E-postadresse er påkrevd.");

        var userExists = await _context.Users.AnyAsync(u => u.Email == request.Email);
        if (userExists)
            return BadRequest("E-postadressen er allerede registrert.");

        // Generer token
        var token = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));

        // Logg FrontendUrl for å sikre at den blir lest riktig
        var frontendUrl = _configuration["FrontendUrl"];
        Console.WriteLine($"FrontendUrl: {frontendUrl}");

        // Lagre i databasen
        var verification = new EmailVerification
        {
            Email = request.Email,
            Token = token,
            ExpiryDate = DateTime.UtcNow.AddHours(24)
        };

        _context.EmailVerifications.Add(verification);
        await _context.SaveChangesAsync();

        // Send e-post
        var confirmationLink = $"{_configuration["FrontendUrl"]}/users/complete-registration?token={token}";
        await _emailService.SendEmailAsync(request.Email, "Bekreft registrering",
            $"Klikk på lenken for å fullføre registreringen: <a href='{confirmationLink}'>Bekreft e-post</a>");

        return Ok("E-post sendt.");
    }
    //[HttpPost("complete-registration")]
    [HttpGet("complete-registration")]
    
    public async Task<IActionResult> CompleteRegistration([FromQuery] string tokenrequest) 
    {
        Console.WriteLine("complete-registration-endpoint enter");
        //var verification = await _context.EmailVerifications
        //    .FirstOrDefaultAsync(ev => ev.Token == request.Token && !ev.IsUsed && ev.ExpiryDate > DateTime.UtcNow);
        var verification = await _context.EmailVerifications
            .FirstOrDefaultAsync(ev => ev.Token == tokenrequest && !ev.IsUsed && ev.ExpiryDate > DateTime.UtcNow);
        if (verification == null)
            return BadRequest("Ugyldig eller utløpt token.");
        Console.WriteLine("complete-registration-endpoint checkpoint1");

        // Opprett brukeren
        var newUser = new User
        {
            Email = verification.Email,
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.Password),
            IsEmailVerified = true //Rød Strek IsEmailVerified
        };
        Console.WriteLine("complete-registration-endpoint checkpoint2");

        _context.Users.Add(newUser);
        verification.IsUsed = true;

        await _context.SaveChangesAsync();
        Console.WriteLine("complete-registration-endpoint checkpoint3");

        return Ok("Bruker registrert.");
    }
*/
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
        Console.WriteLine(request.Email);
        Console.WriteLine(user.Email);
        Console.WriteLine(request.Password);
        Console.WriteLine(user.PasswordHash);
        Console.WriteLine(Authentication.VerifyPassword(request.Password, user.PasswordHash));

        if (user == null || !Authentication.VerifyPassword(request.Password, user.PasswordHash))
        {
            return Unauthorized(new { message = "Ugyldig e-post eller passord" });
        }
        Console.WriteLine($"Mottatt forespørsel for ID: {request.Email}");
        Console.WriteLine(user);
        Console.WriteLine(user.Id);

        var token = _authentication.GenerateJwtToken(user);
        Console.WriteLine(token);        //return Ok(new { token });
        string loginRedirect = user.IsRegistrationCompleted ? "profile" : "update";
        
        return Ok(new
        {
            token,
            userId = user.Id,
            //email = user.Email,
            name = user.Name,
            alias = user.Alias,
            telephone = user.Telephone,
            description = user.Description,
            dob = user.Dob,
            pictureUrl = !string.IsNullOrEmpty(user.PictureUrl) ? user.PictureUrl + "?w=200&h=200&fit=crop" : "",
            redirect = loginRedirect
        });
        /*
        return Ok(new
        {
            token,
            userId = user.Id,
            email = user.Email,
            name = user.Name,
            alias = user.Alias,
            PictureUrl = !string.IsNullOrEmpty(user.PictureUrl) ? user.PictureUrl + "?w=200&h=200&fit=crop" : ""
        });
        */
    }
    [HttpPost("login/test")]
    public async Task<IActionResult> Logintest([FromBody] LoginTest request)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == request.Id);
        Console.WriteLine($"Mottatt forespørsel for ID: {request.Id}");
        Console.WriteLine(user);
        Console.WriteLine(user.Id);

        if (user == null)
        {
            return Unauthorized(new { message = "Feil bruker" });
        }

        var token = _authentication.GenerateJwtToken(user);
        Console.WriteLine(token);
        Console.WriteLine(user.Name);
        Console.WriteLine(user.Alias);
        Console.WriteLine(user.PictureUrl);
        return Ok(new
        {
            token,
            userId = user.Id,
            name = user.Name,
            alias = user.Alias,
            PictureUrl = user.PictureUrl + "?w=50&h=50&fit=crop"
        });
    }

    [Authorize]
    [HttpGet("profile")]
    public async Task<IActionResult> GetUserProfile([FromQuery] Guid? userId)
    {
        Console.WriteLine(userId);
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
        if (!userId.HasValue) // Query parameter ikke spesifisert. Returner egen bruker
        {
            var user = await _context.Users
                .Where(u => u.Id == parsedUserId)
                .Select(u => new
                {
                    u.Alias,
                    u.Name,
                    u.Email,
                    //u.PictureUrl + "?h={pictureHeight}&w={pictureWidth}&fit=crop" //Denne implementeres senere
                    PictureUrl = !string.IsNullOrEmpty(u.PictureUrl)
                        ? $"{u.PictureUrl}?h=200&w=200&fit=crop"
                        : ""
                })
                .FirstOrDefaultAsync();
            if (user == null)
            {
                return Unauthorized(new { message = "Bruker ikke funnet" });
            }
            return Ok(user);
        }
        else
        {
            var user = await _context.Users.FindAsync(userId);
            if (user == null)
            {
                return NotFound(); // Returnerer 404 hvis brukeren ikke finnes
            }
            Console.WriteLine(_userRelationService.RelationStatus(parsedUserId, userId.Value));
            Console.WriteLine(_userRelationService.RelationStatus(userId.Value, parsedUserId));

            string relationStatus = _userRelationService.RelationStatus(parsedUserId, userId.Value);
            var userHikes = await _userHikeService.GetUserHikes(userId.Value, 1,3);
            Console.WriteLine("---");
            Console.WriteLine(userHikes);
            Console.WriteLine("---");
                
            if (relationStatus.ToLower() == "friend" || relationStatus.ToLower() == "friends")
            {
                return Ok(new
                {
                    id = user.Id,
                    name = user.Name,
                    PictureUrl = user.PictureUrl + "?w=200&h=200&fit=crop",
                    alias = user.Alias,
                    description = user.Description,
                    dob = user.Dob,
                    created_at = user.CreatedAt,
                    //Må migrere og oppdatere databasen før jeg kan legge til disse
                    friendsCount = 1,
                    horseCount = 1,
                    relationStatus,
                    userHikes,
                    page = 1,
                    size = 3
                });
            } else 
            {
                
                return Ok(new
                {
                    id = user.Id,
                    name = user.Name,
                    PictureUrl = user.PictureUrl + "?w=200&h=200&fit=crop",
                    alias = user.Alias,
                    description = user.Description,
                    created_at = user.CreatedAt,
                    relationStatus,
                    userHikes,
                    page = 1,
                    size = 3
                });
            }
        }
    }

    //Admin funksjon eller bygges om evt utvides til søkefunksjon
    //[Authorize]
    [HttpGet("all")]
    public async Task<IActionResult> GetUsers()
    {
        var settingsDict = await _context.SystemSettings
            .Where(s => s.Key == "UserProfilePictureList-height" || s.Key == "UserProfilePictureList-width")
            .ToDictionaryAsync(s => s.Key, s => s.Value);

        settingsDict.TryGetValue("UserProfilePictureList-height", out var heightValue);
        settingsDict.TryGetValue("UserProfilePictureList-width", out var widthValue);

        // Konverter til int med fallback-verdi
        var pictureHeight = int.TryParse(heightValue, out var height) ? height : 200;
        var pictureWidth = int.TryParse(widthValue, out var width) ? width : 200;

        var users = await _context.Users
            .Select(u => new
            {
                u.Id,
                u.Name,
                PictureUrl = !string.IsNullOrEmpty(u.PictureUrl)
                    ? $"{u.PictureUrl}?h={pictureHeight}&w={pictureWidth}&fit=crop"
                    : "",
                u.Alias
            })
            .OrderBy(u => u.Id) // Sorterer etter Guid
            .ToListAsync();

        if (!users.Any())
        {
            Log.Warning("⚠️ Ingen brukere funnet!");
            return NotFound(new { message = "No users found." });
        }

        Log.Information("📢 GetAllUsers() ble kalt! Antall brukere: {UserCount}", users.Count);

        return Ok(users);
    }
    //[Authorize]
    [HttpGet("int/{userId}")]
    public async Task<IActionResult> GetIntUser(int userId)
    {
        //var endpointName = ControllerContext.ActionDescriptor.ActionName;
        var controllerName = ControllerContext.ActionDescriptor.ControllerName;
        Guid newGuid = CustomConvert.IntToGuid(controllerName, userId);

        return await GetUser(newGuid, false);
    }
    [Authorize]
    [HttpGet("aut/int/{userId}")]
    public async Task<IActionResult> CheckAutAndGetIntUser(int userId)
    {
        //var endpointName = ControllerContext.ActionDescriptor.ActionName;
        var controllerName = ControllerContext.ActionDescriptor.ControllerName;
        Guid newGuid = CustomConvert.IntToGuid(controllerName, userId);

        return await GetUser(newGuid, false);
    }
    [Authorize]
    [HttpGet("myprofile")]
    public async Task<IActionResult> GetMyProfile()
    {
        Console.WriteLine("Token claims:");
        foreach (var claim in User.Claims)
        {
            Console.WriteLine($"Type: {claim.Type}, Value: {claim.Value}");
        }

        // Hent brukerens ID fra tokenet
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid userId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        var user = await _context.Users
            .Where(u => u.Id == userId)
            .Select(u => new
            {
                u.Alias,
                u.Name,
                u.Email,
                //u.PictureUrl + "?h={pictureHeight}&w={pictureWidth}&fit=crop" //Denne implementeres senere
                PictureUrl = !string.IsNullOrEmpty(u.PictureUrl)
                    ? $"{u.PictureUrl}?h=200&w=200&fit=crop"
                    : ""
            })
            .FirstOrDefaultAsync();

        if (user == null)
        {
            return Unauthorized(new { message = "Bruker ikke funnet" });
        }

        return Ok(user);
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


    //[Authorize]
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

    [Authorize]
    [HttpPut("change-password")]
    public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest request)
    {
        Console.WriteLine("Token claims:");
        foreach (var claim in User.Claims)
        {
            Console.WriteLine($"Type: {claim.Type}, Value: {claim.Value}");
        }

        // Hent brukerens ID fra tokenet ved å bruke `ClaimTypes.NameIdentifier`
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid userId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == userId);

        if (user == null)
        {
            return Unauthorized(new { message = "Bruker ikke funnet" });
        }

        if (!Authentication.VerifyPassword(request.OldPassword, user.PasswordHash))
        {
            return Unauthorized(new { message = "Feil passord" });
        }

        user.PasswordHash = Authentication.HashPassword(request.NewPassword);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Passordet er endret!" });
    }
    // 📌 2.1: Be om passordtilbakestilling
    [HttpPost("reset-password-request")]
    public async Task<IActionResult> RequestPasswordReset([FromBody] RequestPasswordReset request)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
        if (user == null)
            return BadRequest("E-postadressen finnes ikke.");

        // Generer engangs-token for passordtilbakestilling
        var token = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));

        var passwordReset = new PasswordReset
        {
            Email = request.Email,
            Token = token,
            ExpiryDate = DateTime.UtcNow.AddMinutes(15)
        };

        _context.PasswordResets.Add(passwordReset); //Rød strek under PasswordResets
        await _context.SaveChangesAsync();

        // Lag lenke til nettsiden for tilbakestilling
        var resetLink = $"{_configuration["PasswordResetUrl"]}/resetpassword.html?token={Uri.EscapeDataString(token)}";
        await _emailService.SendEmailAsync(request.Email, "Tilbakestill passord",
            $"Klikk på lenken for å tilbakestille passordet: <a href='{resetLink}'>Tilbakestill passord</a>");

        return Ok("E-post sendt. Sjekk innboksen din.");
    }

    // 📌 2.2: Endepunkt for å sette nytt passord
    [HttpPost("reset-password")]
    public async Task<IActionResult> ResetPassword([FromBody] ResetPasswordRequest request)
    {
        var passwordReset = await _context.PasswordResets
            .FirstOrDefaultAsync(pr => pr.Token == request.Token && !pr.IsUsed && pr.ExpiryDate > DateTime.UtcNow);

        if (passwordReset == null)
            return BadRequest("Ugyldig eller utløpt token.");

        var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == passwordReset.Email);
        if (user == null)
            return NotFound("Bruker ikke funnet.");

        // Oppdater passordet
        user.PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.NewPassword);
        passwordReset.IsUsed = true;

        await _context.SaveChangesAsync();

        return Ok(new { message = "Passordet er endret. Du kan nå logge inn med ditt nye passord." });

    }
/*
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
        // PictureUrl 
        // Admin = false
        // Premium = false
        // VerifiedTrail = false

        _context.Users.Update(userData);
        await _context.SaveChangesAsync();

        return Ok(new { message = $"User userId was updated", userData });
    }
    */

    [Authorize]
    [HttpDelete("delete")]
    public async Task<IActionResult> DeleteUser([FromBody] DeleteRequest password)
    {
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
        var userId = await _context.Users.FindAsync(parsedUserId);

        // Sjekk om brukeren finnes
        if (userId == null)
        {
            return NotFound(new { message = "User not found." });
        }
        // Slett brukeren
        //var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
        if (!Authentication.VerifyPassword(password.Password, userId.PasswordHash))
        {
            return Unauthorized(new { message = "Feil passord" });
        }
        _context.Users.Remove(userId);
        await _context.SaveChangesAsync();

        return Ok(new { message = "User removed successfully." });
    }
}

/*
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterRequest request)
    {
        // 🔹 Sjekk om e-posten allerede finnes
        var existingUser = await _context.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
        if (existingUser != null)
        {
            return BadRequest(new { message = "E-post er allerede i bruk" });
        }

        // 🔹 Hashe passordet før vi lagrer det
        string hashedPassword = BCrypt.Net.BCrypt.HashPassword(request.Password);

        var newUser = new User
        {
            Email = request.Email,
            PasswordHash = hashedPassword // Lagre det hash'ede passordet
        };

        _context.Users.Add(newUser);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Bruker registrert!" });
    }

*/