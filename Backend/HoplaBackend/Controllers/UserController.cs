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

using RestSharp; // RestSharp v112.1.0
using RestSharp.Authenticators;
using Microsoft.AspNetCore.Http.HttpResults;


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

    // 0.1 Registrer. Skriv inn epost og passord. 
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterRequest request)
    {
        if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Password))
            return BadRequest("E-post og passord er påkrevd.");

        var existingUser = await _context.Users.AnyAsync(u => u.Email == request.Email);
        if (existingUser)
            return BadRequest("E-postadressen er allerede registrert."); //Endres til noe annet for å ikke avsløre at epostadressen finnes.

        var existingVerification = await _context.EmailVerifications.AnyAsync(ev => ev.Email == request.Email && !ev.IsUsed && ev.ExpiryDate < DateTime.UtcNow);
        if (existingVerification)
            return BadRequest("En verifikasjonsprosess er allerede i gang. Sjekk e-posten din og evt søppelpost.");

        // Hash passordet
        var passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

        // Generer token
        var token = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));

        var verification = new EmailVerification
        {
            Email = request.Email,
            PasswordHash = passwordHash,  // Lagres her til e-posten er bekreftet
            Token = token,
            ExpiryDate = DateTime.UtcNow.AddHours(24),
            //IsUsed = true
        };

        _context.EmailVerifications.Add(verification);
        await _context.SaveChangesAsync();

        // Send e-post
        var confirmEmailUrl = _configuration["ConfirmEmailUrl"];
        Console.WriteLine(confirmEmailUrl);

        var confirmationLink = $"{_configuration["ConfirmEmailUrl"]}?token={Uri.EscapeDataString(token)}";
        await _emailService.SendEmailAsync(request.Email, "Bekreft registrering",
            $"Klikk på lenken for å fullføre registreringen: <a href='{confirmationLink}'>Bekreft e-post</a>. Dette må gjøres innen 24 timer fra da du opprettet brukernavnet.");

        return Ok("E-post sendt. Sjekk innboksen og trykk på lenken for å bekrefte registreringen. Sjekk evt søppelpost. Eposten må verifiseres innen 24 timer");
    }

    // 0.2 Endpoint for fullføring av registrering.
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

    // 1.1 login med epost og passord. Mottar token slik at brukeren kan bruke programmet.
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == request.Email);
        if (user == null || !Authentication.VerifyPassword(request.Password, user.PasswordHash))
        {
            return Unauthorized(new { message = "Ugyldig e-post eller passord" });
        }
        Console.WriteLine(request.Email);
        Console.WriteLine(user.Email);
        Console.WriteLine(request.Password);
        Console.WriteLine(user.PasswordHash);
        Console.WriteLine(user.IsDeleted);
        Console.WriteLine(Authentication.VerifyPassword(request.Password, user.PasswordHash));
        
        if (user.IsDeleted) 
        {
            return Unauthorized("Kontoen er deaktivert. For å reaktivere kontoen må du gå til https://activate.hopla.no");
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
            pictureUrl = !string.IsNullOrEmpty(user.PictureUrl) ? (user.PictureUrl.Contains("http") ? user.PictureUrl  : "https://files.hopla.no/" + user.PictureUrl) + "?w=200&h=200&fit=crop" : "",
            redirect = loginRedirect
        });
    }

    //1.2.a Blir redirectet hit hvis brukeren har registrert Navn og Alias. 
    // Denne funksjonen brukes også til å sjekke andre brukeres profiler hvis query userId er spesifisert.
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
        if (!userId.HasValue) 
        {
            // Hvis Query parameter ikke spesifisert. Returner egen bruker
            var user = await _context.Users
                .Where(u => u.Id == parsedUserId)
                .Select(u => new
                {
                    u.Alias,
                    u.Name,
                    u.Email,
                    //u.PictureUrl + "?h={pictureHeight}&w={pictureWidth}&fit=crop" //Denne implementeres senere
                    PictureUrl = !string.IsNullOrEmpty(u.PictureUrl) ? (u.PictureUrl.Contains("http") ? u.PictureUrl : "https://hopla.imgix.net/" + u.PictureUrl) + "?w=200&h=200&fit=crop" : ""
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
                // Når man er venner med vedkommende gis man litt mer informasjon
                return Ok(new
                {
                    id = user.Id,
                    name = user.Name,
                    PictureUrl = !string.IsNullOrEmpty(user.PictureUrl) ? (user.PictureUrl.Contains("http") ? user.PictureUrl : "https://hopla.imgix.net/" + user.PictureUrl) + "?w=200&h=200&fit=crop" : "",
                    alias = user.Alias,
                    description = user.Description,
                    dob = user.Dob,
                    created_at = user.CreatedAt,
                    //Må migrere og oppdatere databasen før jeg kan legge til disse
                    friendsCount = user.FriendsCount,
                    horseCount = user.HorseCount,
                    relationStatus,
                    userHikes,
                    page = 1,
                    size = 3
                });
            } else 
            {
                
                // For alle andre tilfeller enn venner
                return Ok(new
                {
                    id = user.Id,
                    name = user.Name,
                    PictureUrl = !string.IsNullOrEmpty(user.PictureUrl) ? (user.PictureUrl.Contains("http") ? user.PictureUrl : "https://hopla.imgix.net/" + user.PictureUrl) + "?w=200&h=200&fit=crop" : "",
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
    // 1.2.b Blir redirectet hit for videre oppdatering av brukerprofilen hvis bool IsRegistrationCompleted == false.
    // Her kan alt bortsett fra email, passord og profilbilde. Endres. 
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

        //Alias og Navn er påkrevd. 
        if (string.IsNullOrWhiteSpace(request.Name) || string.IsNullOrWhiteSpace(request.Alias))
            return BadRequest("Name or Alias cannot be empty");
        Console.WriteLine("users/update checkpoint Name or alias cannot be empty");

        // Oppdater informasjon
        /*if (!string.IsNullOrWhiteSpace(request.Name))
            user.Name = request.Name;

        if (!string.IsNullOrWhiteSpace(request.Alias))
            user.Alias = request.Alias;
        if (!string.IsNullOrWhiteSpace(request.Description))
            user.Description = request.Description;
        if (!string.IsNullOrWhiteSpace(request.Telephone))
            user.Telephone = request.Telephone;
        if (request.Dob.HasValue)
            user.Dob = request.Dob;
        */        
        if (request.Name != null) user.Name = request.Name;
        if (request.Alias != null) user.Alias = request.Alias;
        if (request.Description != null) user.Description = request.Description;
        if (request.Telephone != null) user.Telephone = request.Telephone;

        // Hvis Dob ikke endres, blir den fjernet.
        DateOnly? dob = null;
        if (request.Year.HasValue && request.Month.HasValue && request.Day.HasValue)
        {
            try
            {
                dob = new DateOnly(request.Year.Value, request.Month.Value, request.Day.Value);
            }
            catch
            {
                return BadRequest(new { error = "Ugyldig fødselsdato." });
            }
        }
        user.Dob = dob;
        Console.WriteLine("users/update checkpoint før save og return ok");


        await _context.SaveChangesAsync();

        return Ok("Brukerinformasjon oppdatert.");
    }

    // update email
    // 1.2.c1 change-email
    [Authorize]
    [HttpPost("change-email")]
    public async Task<IActionResult> ChangeEmail([FromBody] ChangeEmailRequest request)
    {
        // Hent brukerens ID fra tokenet
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        Console.WriteLine($"Hentet bruker-ID fra token: {userIdString}");

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
        {
            return Unauthorized(new { message = "Ugyldig token eller bruker-ID" });
        }

        if (string.IsNullOrWhiteSpace(request.NewEmail) || string.IsNullOrWhiteSpace(request.Password))
            return BadRequest("E-post og passord er påkrevd.");

        // Sjekk om den nye e-postadressen allerede er registrert
        var existingUser = await _context.Users.AnyAsync(u => u.Email == request.NewEmail);
        if (existingUser)
            return BadRequest("E-postadressen er allerede registrert."); // Unngå å avsløre om e-posten finnes.

        // Sjekk om det allerede finnes en verifikasjon for denne e-posten
        var existingVerification = await _context.EmailVerifications
            .AnyAsync(ev => ev.Email == request.NewEmail && !ev.IsUsed && ev.ExpiryDate > DateTime.UtcNow);
        if (existingVerification)
            return BadRequest("En verifikasjonsprosess er allerede i gang. Sjekk e-posten din og evt. søppelpost.");

        // Hent brukerens nåværende e-post basert på ID-en fra tokenet
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == parsedUserId);
        if (user == null)
            return Unauthorized("Brukeren finnes ikke.");

        // Hash passordet
        var passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

        // Generer token til epost
        var token = Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));

        var verification = new EmailVerification
        {
            Email = request.NewEmail,
            OldEmail = user.Email, // ✅ Nå henter vi riktig gammel e-postadresse
            PasswordHash = passwordHash,  // Lagres her, men ikke nødvendig.
            Token = token,
            ExpiryDate = DateTime.UtcNow.AddHours(24),
        };
        _context.EmailVerifications.Add(verification);
        await _context.SaveChangesAsync();

        // Send e-post
        var confirmNewEmailUrl = _configuration["ConfirmEmailUrl"];
        Console.WriteLine(confirmNewEmailUrl);

        var confirmationLink = $"{_configuration["ConfirmNewEmailUrl"]}?token={Uri.EscapeDataString(token)}";
        await _emailService.SendEmailAsync(request.NewEmail, "Bekreft registrering",
            $"Klikk på lenken for å fullføre registreringen: <a href='{confirmationLink}'>Bekreft e-post</a>. Dette må gjøres innen 24 timer fra da du opprettet brukernavnet.");

        return Ok("E-post sendt. Sjekk innboksen og trykk på lenken for å bekrefte registreringen. Sjekk evt søppelpost. Eposten må verifiseres innen 24 timer");
    }

    // 1.2.c2 Trinn to av bytte epostadresse. Burde egentlig vært Put, men litt vanskelig når det er via epostlink.
    [HttpGet("confirm-new-email")]
    public async Task<IActionResult> ConfirmNewEmail([FromQuery] string token)
    {
        var verification = await _context.EmailVerifications
            .FirstOrDefaultAsync(ev => ev.Token == token && !ev.IsUsed && ev.ExpiryDate > DateTime.UtcNow);

        if (verification == null)
            return BadRequest("Ugyldig eller utløpt token.");

        // Hent brukeren basert på den gamle e-posten
        var existingUser = await _context.Users.FirstOrDefaultAsync(u => u.Email == verification.OldEmail);
        if (existingUser == null)
            return BadRequest("Brukeren ble ikke funnet.");

        // Sjekk om den nye e-posten allerede finnes hos en annen bruker
        var emailTaken = await _context.Users.AnyAsync(u => u.Email == verification.Email);
        if (emailTaken)
            return BadRequest("E-posten er allerede registrert hos en annen bruker.");

        // Oppdater brukerens e-postadresse
        existingUser.Email = verification.Email;

        // Marker tokenet som brukt
        verification.IsUsed = true;

        // Lagre endringene i databasen
        await _context.SaveChangesAsync();

        return Ok("E-post bekreftet! Du kan nå logge inn med din nye e-postadresse.");
    }
    // 1.2.d change-password
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
        bool isValid = PasswordValidator.IsValidPassword(request.NewPassword);
        
        if (!isValid)
        {
            return Unauthorized(new { message = "Passordet er avvist fordi det er for kort eller ikke inneholder minst en liten bokstav, en stor bokstav, tall eller spesialtegn "});
        }
        if (request.NewPassword != request.ConfirmPassword) 
        {
            return Unauthorized(new { message = "New password doesn't match with comfirmed password." });
        }
        user.PasswordHash = Authentication.HashPassword(request.NewPassword);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Passordet er endret!" });
    }


    // 2.1: Be om passordtilbakestilling
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
            ExpiryDate = DateTime.UtcNow.AddMinutes(240)
        };

        _context.PasswordResets.Add(passwordReset); //Rød strek under PasswordResets
        await _context.SaveChangesAsync();

        // Lag lenke til nettsiden for tilbakestilling
        var resetLink = $"{_configuration["PasswordResetUrl"]}/index.html?token={Uri.EscapeDataString(token)}";
        await _emailService.SendEmailAsync(request.Email, "Tilbakestill passord",
            $"Klikk på lenken for å tilbakestille passordet: <a href='{resetLink}'>Tilbakestill passord</a> Dette må gjøres innen 4 timer fra da du tilbakestilte passordet");

        return Ok("E-post sendt. Sjekk innboksen og trykk på lenken for å tilbakestille passordet. Sjekk evt søppelpost og Other/Annet mappen. Passordet må tilbakestilles innen 24 timer");
    }

    // Denne kan kun gjøres på web! https://password.hopla.no
    // 2.2: Endepunkt for å sette nytt passord
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


    [Authorize]
    [HttpPatch("delete")]
    public async Task<IActionResult> DeleteUser([FromBody] DeleteRequest request)
    {
        // Debugging: Logg alle claims for å sjekke hva tokenet inneholder
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

        // Hent brukeren fra databasen
        var user = await _context.Users.FindAsync(parsedUserId);

        // Sjekk om brukeren finnes
        if (user == null)
        {
            return NotFound(new { message = "Bruker ikke funnet." });
        }

        // Sjekk passord
        if (!Authentication.VerifyPassword(request.Password, user.PasswordHash))
        {
            return Unauthorized(new { message = "Feil passord." });
        }

        // Merk brukeren som slettet
        user.IsDeleted = true;

        // Oppdater entiteten eksplisitt for at EF skal fange opp endringen
        _context.Users.Update(user);
        await _context.SaveChangesAsync();

        return Ok(new { message = "Bruker deaktivert." });
    }

    // Klasse for å motta passord fra forespørselen
    public class DeleteRequest
    {
        public string Password { get; set; }
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
                PictureUrl = !string.IsNullOrEmpty(u.PictureUrl) ? (u.PictureUrl.Contains("http") ? u.PictureUrl : "https://hopla.imgix.net/" + u.PictureUrl) + "?w=64&h=64&fit=crop" : "",
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

    // Denne kan fjernes når profile brukes av både android og iOs
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
                PictureUrl = !string.IsNullOrEmpty(u.PictureUrl) ? (u.PictureUrl.Contains("http") ? u.PictureUrl : "https://hopla.imgix.net/" + u.PictureUrl) + "?w=200&h=200&fit=crop" : "",
            })
            .FirstOrDefaultAsync();

        if (user == null)
        {
            return Unauthorized(new { message = "Bruker ikke funnet" });
        }

        return Ok(user);
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
   //
    // Disse brukes kanskje på websiden fra gammelt av. Skal fjernes!!
    //
    // Denne brukes av nettside for backendtesting for enklere innlogging. Skal fjernes til lansering
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
            PictureUrl =  !string.IsNullOrEmpty(user.PictureUrl) ? (user.PictureUrl.Contains("http") ? user.PictureUrl : "https://hopla.imgix.net/" + user.PictureUrl) + "?w=200&h=200&fit=crop" : "" //user.PictureUrl + "?w=50&h=50&fit=crop"
        });
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
}
