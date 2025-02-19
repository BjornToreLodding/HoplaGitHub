//using HoplaBackend.Mock;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Linq;
using HoplaBackend;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;

namespace HoplaBackend.Models;

[ApiController]
[Route("mock")]
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
        _context.StableUsers.RemoveRange(_context.StableUsers);
        _context.Rides.RemoveRange(_context.Rides); 
        //_context.RideDetails.RemoveRange(_context.RideDetails);
        //_context.RideReviews.RemoveRange(_context.RideReviews);
        _context.Trails.RemoveRange(_context.Trails);
        //_context.TrailReviews.RemoveRange(_context.TrailReviews);
        //_context.TrailFilters(_context.TrailFilters);



        await _context.SaveChangesAsync();

        //En merkelig feil, gjør at sekvensene for Id på Stables og Rides ikke starter på 1 etter cleardatabase og createcontent. 
        // Da blir det feil når Mock skal opprettes for StableMessages og Trails, da Id refererer til noe som ikke eksisterer
        //Det er ikke nødvendig og kjøre dette på Users og Horses, men de er med da det virker mest logisk.
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Users\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Horses\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"UserRelations\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Messages\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Stables\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"StableMessages\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"StableUsers\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Rides\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Trails\" RESTART IDENTITY CASCADE");

        // Nullstill sekvensene manuelt (i tilfelle PostgreSQL ikke gjør det automatisk, noe som desverre skjer)
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Users_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Horses_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"UserRelations_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Messages_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Stables_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"StableMessages_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"StableUsers_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Users_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Rides_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Trails_Id_seq\" RESTART WITH 1");

        return Ok("Database cleared and IDs reset.");    
        }

    [HttpPost("createusers")]
    public async Task<IActionResult> CreateUsers() //rød strek under Task<IActionResult>
    {
        if (_context.Users.Any()) { return NoContent(); } 

        var users = UserMock.CreateUsersMock();
        _context.Users.AddRange(users);
        await _context.SaveChangesAsync();

        return Created("", new { message = "Success!" }); // Hvis URL ikke trengs
    
    }

    [HttpPost("createhorses")]
    public async Task<IActionResult> CreateHorses()
    {
        if (_context.Horses.Any()) { return NoContent(); } //rød strek under .Any

        var existingUsers = _context.Users.ToList(); //rød strek under .ToList
        var horses = HorseMock.CreateHorsesMock(existingUsers);
        _context.Horses.AddRange(horses);
        await _context.SaveChangesAsync();
    
        return Created("", new { message = "Hester opprettet!", horses });
        //return Created(nameof(GetHorseById), new { message = "Hester opprettet!", horses });
    
        //return Created(); // versjon 12+
    }
    [HttpPost("createuserrelations")]
    public async Task<IActionResult> CreateUserRelations()
    {
        if (_context.UserRelations.Any()) { return NoContent(); }

        var existingUsers = _context.Users.ToList();
        var friendRequests = UserRelationMock.CreateUserRelationMock(existingUsers);
        _context.UserRelations.AddRange(friendRequests);
        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!", friendRequests });

    }
    [HttpPost("createmessages")]
    public async Task<IActionResult> CreateMessages()
    {
        if (_context.Messages.Any()) { return NoContent(); }
        var messages = MessageMock.CreateMessagesMock();  
        _context.Messages.AddRange(messages);
        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!", messages });

    }


    [HttpPost("createstables")]
    public async Task<IActionResult> CreateStables()
    {
        if (_context.Stables.Any()) { return NoContent(); }
        var stables = StableMock.GetStablesMock();
        _context.Stables.AddRange(stables);
        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!", stables });

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

        //return Created();
        return Created("", new { message = "opprettet!", stableMessages });

    }

    [HttpPost("createrides")]
    public async Task<IActionResult> CreateRides()
    {
        if (_context.Rides.Any()) { return NoContent(); }
        var rides = RideMock.CreateRidesMock();
        _context.Rides.AddRange(rides);
        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!", rides });

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

        //return Created();
        return Created("", new { message = "opprettet!", trails });

    }


    [HttpGet("testdist")] // Bare en test
    public IActionResult GetDistances( 
        [FromQuery] double? lat1, 
        [FromQuery] double? long1, 
        [FromQuery] double? lat2,
        [FromQuery] double? long2)
    {
        // Standardverdier hvis ingen query parameters er gitt
        double defaultLat1 = 60.7925f;
        double defaultLong1 = 10.695f;
        double defaultLat2 = 60.95558f;
        double defaultLong2 = 10.6115f;

        // Bruk verdiene fra query parameters. Hvis de ikke er oppgitt brukes standardverdiene som oppgitt ovenfor.
        double lat1Value = lat1 ?? defaultLat1;
        double long1Value = long1 ?? defaultLong1;
        double lat2Value = lat2 ?? defaultLat2;
        double long2Value = long2 ?? defaultLong2;

        // Beregn avstandene med DistanceCalc-klassen
        var distances = new
        {
            SimplePythagoras = DistanceCalc.SimplePytagoras(lat1Value, long1Value, lat2Value, long2Value),
            ImprovedPythagoras = DistanceCalc.ImprovedPytagoras(lat1Value, long1Value, lat2Value, long2Value),
            Haversine = DistanceCalc.Haversine(lat1Value, long1Value, lat2Value, long2Value)
        };

        //return Ok(distances);  // Returnerer som JSON
        return Created("", new { message = "opprettet!", distances });

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
        //await CreateRideDetailsDatas();
        await CreateTrails();
        //await CreateTrailDetails();
        //await CreateTrailReviews();
        //await CreateTrailFilters;

        //return Created();
        return Created("", new { message = "opprettet!"});

    }
}
