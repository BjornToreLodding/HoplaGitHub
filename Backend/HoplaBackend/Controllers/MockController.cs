//using MyApp.Mock;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;

namespace MyApp.Models;

[ApiController]
[Route("api/mock")]
public class MockController : ControllerBase
{
    private readonly AppDbContext _context;

    public MockController(AppDbContext context)
    {
        _context = context;
    }

    [HttpPost("cleardatabase")]
    public async Task<IActionResult> ClearDatabase()
    {
        _context.Users.RemoveRange(_context.Users);
        _context.Horses.RemoveRange(_context.Horses);
        _context.UserRelations.RemoveRange(_context.UserRelations);
        _context.Messages.RemoveRange(_context.Messages);
        _context.Stables.RemoveRange(_context.Stables);
        _context.StableMessages.RemoveRange(_context.StableMessages);
        _context.UserRelations.RemoveRange(_context.UserRelations);
        _context.Rides.RemoveRange(_context.Rides); 
        //_context.RideDetails.RemoveRange(_context.RideDetails);
        //_context.RideReviews.RemoveRange(_context.RideReviews);
        _context.Trails.RemoveRange(_context.Trails);
        //_context.TrailReviews(_context.TrailReviews);
        //_context.Filters(_context.Filters);

        await _context.SaveChangesAsync();

        //En merkelig feil, gjør at sekvensene for Id på Stables og Rides ikke starter på 1 etter cleardatabase og createcontent. 
        // Da blir det feil når Mock skal opprettes for StableMessages og Trails, da Id refererer til noe som ikke eksisterer
        //Det er ikke nødvendig og kjøre dette på Users og Horses, men de er med da det virker mest logisk.
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Users\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Horses\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Rides\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Stables\" RESTART IDENTITY CASCADE");

        // Nullstill sekvensene manuelt (i tilfelle PostgreSQL ikke gjør det automatisk)
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Users_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Horses_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Rides_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Stables_Id_seq\" RESTART WITH 1");

        return Ok("Database cleared and IDs reset.");    
        }

    [HttpPost("createusers")]
    public async Task<IActionResult> CreateUsers()
    {
        if (_context.Users.Any()) { return NoContent(); }

        var users = UserMock.CreateUsersMock();
        _context.Users.AddRange(users);
        await _context.SaveChangesAsync();

        return Created();
    }

    [HttpPost("createhorses")]
    public async Task<IActionResult> CreateHorses()
    {
        if (_context.Horses.Any()) { return NoContent(); }

        var existingUsers = _context.Users.ToList();
        var horses = HorseMock.CreateHorsesMock(existingUsers);
        _context.Horses.AddRange(horses);
        await _context.SaveChangesAsync();

        return Created();
    }
    [HttpPost("createuserrelations")]
    public async Task<IActionResult> CreateUserRelations()
    {
        if (_context.UserRelations.Any()) { return NoContent(); }

        var existingUsers = _context.Users.ToList();
        var friendRequests = UserRelationMock.CreateUserRelationMock(existingUsers);
        _context.UserRelations.AddRange(friendRequests);
        await _context.SaveChangesAsync();

        return Created();
    }
    [HttpPost("createmessages")]
    public async Task<IActionResult> CreateMessages()
    {
        if (_context.Messages.Any()) { return NoContent(); }
        var messages = MessageMock.CreateMessagesMock();  
        _context.Messages.AddRange(messages);
        await _context.SaveChangesAsync();

        return Created();
    }


    [HttpPost("createstables")]
    public async Task<IActionResult> CreateStables()
    {
        if (_context.Stables.Any()) { return NoContent(); }
        var stables = StableMock.GetStablesMock();
        _context.Stables.AddRange(stables);
        await _context.SaveChangesAsync();

        return Created();
    }

    [HttpPost("createstablemessages")]
    public async Task<IActionResult> CreateStableMessages()
    {
        if (_context.StableMessages.Any()) { return NoContent(); }
        //Av en eller annen grunn så er det rød strek under StableMessageMock
        //Oisann, nå er den borte. Kanskje det hjalp med reboot?
        var stableMessages = StableMessageMock.CreateStableMessagesMock();
        _context.StableMessages.AddRange(stableMessages);
        await _context.SaveChangesAsync();

        return Created();
    }

    [HttpPost("createrides")]
    public async Task<IActionResult> CreateRides()
    {
        if (_context.Rides.Any()) { return NoContent(); }
        var rides = RideMock.CreateRidesMock();
        _context.Rides.AddRange(rides);
        await _context.SaveChangesAsync();

        return Created();
    }

    
    [HttpPost("createtrails")]
    public async Task<IActionResult> CreateTrails()
    {
        if (_context.Trails.Any()) 
        { 
            return NoContent(); 
        }

        // Hent eksisterende rides fra databasen
        var existingRides = _context.Rides.ToList();

        if (!existingRides.Any())
        {
            return BadRequest("Cannot create trails because there are no rides in the database.");
        }

        // Opprett mock trails basert på eksisterende rides
        var trails = TrailMock.CreateTrailsMock(existingRides);

        _context.Trails.AddRange(trails);
        await _context.SaveChangesAsync();

        return Created();
    }


    [HttpGet("testdist")] // Bare en test
    public IActionResult GetDistances( 
        [FromQuery] float? lat1, 
        [FromQuery] float? long1, 
        [FromQuery] float? lat2,
        [FromQuery] float? long2)
    {
        // Standardverdier hvis ingen query parameters er gitt
        float defaultLat1 = 60.7925f;
        float defaultLong1 = 10.695f;
        float defaultLat2 = 60.95558f;
        float defaultLong2 = 10.6115f;

        // Bruk verdiene fra query parameters. Hvis de ikke er oppgitt brukes standardverdiene som oppgitt ovenfor.
        float lat1Value = lat1 ?? defaultLat1;
        float long1Value = long1 ?? defaultLong1;
        float lat2Value = lat2 ?? defaultLat2;
        float long2Value = long2 ?? defaultLong2;

        // Beregn avstandene med DistanceCalc-klassen
        var distances = new
        {
            SimplePythagoras = DistanceCalc.SimplePytagoras(lat1Value, long1Value, lat2Value, long2Value),
            ImprovedPythagoras = DistanceCalc.ImprovedPytagoras(lat1Value, long1Value, lat2Value, long2Value),
            Haversine = DistanceCalc.Haversine(lat1Value, long1Value, lat2Value, long2Value)
        };

        return Ok(distances);  // Returnerer som JSON
    }


    [HttpPost("createcontent")]
    public async Task<IActionResult> CreateDbContent()
    {
        //await ClearDatabase();
        await CreateUsers();
        await CreateHorses();
        await CreateUserRelations();
        await CreateMessages();
        await CreateStables();
        //await CreateStableUSers();
        await CreateStableMessages();
        await CreateRides();
        //await CreateRideDetails();
        //await CreateRideReviews();
        await CreateTrails();
        //await CreateTrailReviews();
        //await CreateFilters;

        return Created();
    }
}
