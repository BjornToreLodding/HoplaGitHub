//using HoplaBackend.Mock;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Linq;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Helpers;
using MediatR;
using HoplaBackend.Events;
using System.Diagnostics;

namespace HoplaBackend.Models;

[ApiController]
[Route("mock")]
public class MockController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly IMediator _mediator;

    public MockController(AppDbContext context, IMediator mediator)
    {
        _context = context;
        _mediator = mediator;
    }

    [HttpPost("clearsystemsettings")]
    public async Task<IActionResult> ClearSystemsettings()
    {
        _context.SystemSettings.RemoveRange(_context.SystemSettings); //rød strek under _context.SystemSettings
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"SystemSettings\" RESTART IDENTITY CASCADE");
        return Ok("Database cleared and IDs reset.");    
    }

    [HttpPost("createsystemsettings")]
    public async Task<IActionResult> CreateSystemSettings()
    {
        if (_context.SystemSettings.Any()) { return NoContent(); }
        var systemSettings = SystemSettingMock.SetDefaultSettingsMock();
        _context.SystemSettings.AddRange(systemSettings);
        await _context.SaveChangesAsync();
        return Created("", new { message = "SystemSettings created "});
    }

    [HttpPost("clearusers")]
    public async Task<IActionResult> ClearUsers()
    {
        _context.Users.RemoveRange(_context.Users); //Ingen rød strek her
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Users\" RESTART IDENTITY CASCADE");
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

    [HttpPost("clearhorses")]
    public async Task<IActionResult> ClearHorses()
    {
        var horses = _context.Horses.ToList();
        if (!horses.Any()) { return NoContent(); }

        _context.Horses.RemoveRange(horses);
        await _context.SaveChangesAsync();

        // 🚀 Returner kun ID-ene på slettede hester for å unngå JSON-feil
        return Ok(new { message = "Hester slettet!", horseIds = horses.Select(h => h.Id) });

        //_context.Horses.RemoveRange(_context.Horses);
        //await _context.SaveChangesAsync();
        //await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Horses\" RESTART IDENTITY CASCADE");
        //return Ok("Horses table cleared and IDs reset.");    
    }
    [HttpPost("createhorses")]
    public async Task<IActionResult> CreateHorses()
    {
        if (_context.Horses.Any()) { return NoContent(); }

        var existingUsers = _context.Users.ToList(); 
        var horses = HorseMock.CreateHorsesMock(existingUsers);
        _context.Horses.AddRange(horses);
        await _context.SaveChangesAsync();

        /*
            // Publiser event for hver ny hest i feeden
        foreach (var horse in horses)
        {
            await _mediator.Publish(new EntityCreatedEvent(horse.Id, "Horse", horse.UserId)); //Rød strek under IMediator.Publish
        }
        */
        return Created("", new { message = "Hester opprettet!", horses });
        //return Created(nameof(GetHorseById), new { message = "Hester opprettet!", horses });
    
        //return Created(); // versjon 12+
    }

    [HttpPost("clearuserrelations")]
    public async Task<IActionResult> ClearUserRelations()
    {
        _context.UserRelations.RemoveRange(_context.UserRelations);
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"UserRelations\" RESTART IDENTITY CASCADE");
        return Ok("UserRelations table cleared and IDs reset.");    
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

    [HttpPost("clearmessages")]
    public async Task<IActionResult> ClearMessages()
    {
        _context.Messages.RemoveRange(_context.Messages);
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Messages\" RESTART IDENTITY CASCADE");
        return Ok("Messages table cleared and IDs reset.");    
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

    [HttpPost("clearstables")]
    public async Task<IActionResult> ClearStables()
    {
        _context.Stables.RemoveRange(_context.Stables);
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Stables\" RESTART IDENTITY CASCADE");
        return Ok("Stables table content cleared and IDs reset.");    
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
    [HttpPost("clearstableusers")]
    public async Task<IActionResult> ClearStableUsers()
    {
        _context.StableUsers.RemoveRange(_context.StableUsers);
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"StableUsers\" RESTART IDENTITY CASCADE");
        return Ok("StableUsers table content cleared and IDs reset.");    
    }
    [HttpPost("createstableusers")]
    public async Task<IActionResult> CreateStableUsers()
    {
        if (_context.StableUsers.Any()) { return NoContent(); }
        var stableUsers = StableUserMock.CreateStableUsersMock();
        _context.StableUsers.AddRange(stableUsers);
        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!", stableUsers });

    }

    [HttpPost("clearstablemessages")]
    public async Task<IActionResult> ClearStableMessages()
    {
        _context.StableMessages.RemoveRange(_context.StableMessages);
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"StableMessages\" RESTART IDENTITY CASCADE");
        return Ok("StableMessages table content cleared and IDs reset.");    
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
    /*
   [HttpPost("clearrides")]
    public async Task<IActionResult> ClearRides()
    {
        _context.Rides.RemoveRange(_context.Rides);
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Rides\" RESTART IDENTITY CASCADE");
        return Ok("Rides table content cleared and IDs reset.");    
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
    */
    [HttpPost("cleartrails")]
    public async Task<IActionResult> ClearTrails()
    {
        _context.Trails.RemoveRange(_context.Trails); 
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Trails\" RESTART IDENTITY CASCADE");
        return Ok("Database cleared and IDs reset.");    
    }
    
    [HttpPost("createtrails")]
    public async Task<IActionResult> CreateTrails()
    {
        if (_context.Trails.Any()) 
        { 
            return NoContent(); 
        }

        // Opprett mock trails basert på eksisterende rides
        var trails = TrailMock.CreateTrailsMock();
        _context.Trails.AddRange(trails);
        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!", trails });

    }
    [HttpPost("cleartrailfiltervalues")]
    public async Task<IActionResult> ClearTrailfilters()
    {
        _context.TrailFilterValues.RemoveRange(_context.TrailFilterValues); 
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"TrailFilterValues\" RESTART IDENTITY CASCADE");
        return Ok("Database cleared and IDs reset.");    
    }
    
    [HttpPost("createtrailfiltervalues")]
    public async Task<IActionResult> CreateTrailFilterValues()
    {
        //await TrailFilterSeeder.SyncDefinitionsAsync(context);

        if (_context.TrailFilterValues.Any()) 
        { 
            return NoContent(); 
        }

        // Opprett mock trails basert på eksisterende rides
        await TrailFilterMock.CreateTrailFilterValuesMock(_context);

        await _context.SaveChangesAsync();

        //return Created();
        return Created("", new { message = "opprettet!"});

    }

    [HttpPost("clearuserhikes")]
    public async Task<IActionResult> ClearUserHikes()
    {
        _context.UserHikes.RemoveRange(_context.UserHikes); 
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"UserHikes\" RESTART IDENTITY CASCADE");
        return Ok("Database cleared and IDs reset.");    
    }

    [HttpPost("createuserhikes")]
    public async Task<IActionResult> CreateUserHikes() 
    {
        if (_context.UserHikes.Any()) { return NoContent(); } 

        var userHikes = UserHikeMock.CreateUserHikeMock();
        _context.UserHikes.AddRange(userHikes);
        await _context.SaveChangesAsync();

        return Created("", new { message = "Success!" }); // Hvis URL ikke trengs
    
    }

    [HttpPost("cleartrailfavorites")]
    public async Task<IActionResult> ClearTrailFavorites()
    {
        _context.TrailFavorites.RemoveRange(_context.TrailFavorites); 
        await _context.SaveChangesAsync();
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"TrailFavorites\" RESTART IDENTITY CASCADE");
        return Ok("Database cleared and IDs reset.");    
    }

    [HttpPost("createtrailfavorites")]
    public async Task<IActionResult> CreateTrailFavorites() 
    {
        if (_context.TrailFavorites.Any()) { return NoContent(); } 

        var trailFavorites = TrailFavoriteMock.CreateTrailFavoriteMock();
        _context.TrailFavorites.AddRange(trailFavorites);
        await _context.SaveChangesAsync();

        return Created("", new { message = "Success!" }); // Hvis URL ikke trengs
    
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


    [HttpPost("createdatabase")]
    public async Task<IActionResult> CreateDataBase() //kanskje misvisende navn. Lager innholdet
    {
    var stopwatch = new Stopwatch();

    stopwatch.Restart();
    Console.WriteLine("Creating Users");
    await CreateUsers();
    Console.WriteLine($"Users created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating Horses");
    await CreateHorses();
    Console.WriteLine($"Horses created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating UserRelations");
    await CreateUserRelations();
    Console.WriteLine($"UserRelations created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating Messages");
    await CreateMessages();
    Console.WriteLine($"Messages created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating Stables");
    await CreateStables();
    Console.WriteLine($"Stables created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating StableUsers");
    await CreateStableUsers();
    Console.WriteLine($"StableUsers created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating StableMessages");
    await CreateStableMessages();
    Console.WriteLine($"StableMessages created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating Trails");
    await CreateTrails();
    Console.WriteLine($"Trails created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

        //await CreateRides();
        //await CreateRideDetails();
        //await CreateRideReviews();
        //await CreateRideDetailsDatas();
    stopwatch.Restart();
    Console.WriteLine("Creating UserHikes");
    await CreateUserHikes();
    Console.WriteLine($"UserHikes created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

        //await CreateTrailDetails();
        //await CreateTrailReviews();
        //await CreateTrailFilters;
    stopwatch.Restart();
    Console.WriteLine("Creating SystemSettings");
    await CreateSystemSettings();
    Console.WriteLine($"SystemSettings created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

    stopwatch.Restart();
    Console.WriteLine("Creating TrailFavorites");
    await CreateTrailFavorites();
    Console.WriteLine($"TrailFavorites created in {stopwatch.Elapsed.TotalSeconds:F2} seconds\n");

        return Created("", new { message = "opprettet!"});

    }
    [HttpPost("cleardatabase")]
    public async Task<IActionResult> ClearDatabase()
    {
        
        _context.SystemSettings.RemoveRange(_context.SystemSettings); //ingen rød strek her
        _context.TrailFavorites.RemoveRange(_context.TrailFavorites);
        //_context.Users.RemoveRange(_context.Users); //ingen rød strek her
        _context.Horses.RemoveRange(_context.Horses);//ingen rød strek her
        _context.UserRelations.RemoveRange(_context.UserRelations); //ingen rød strek her
        _context.Messages.RemoveRange(_context.Messages);
        _context.Stables.RemoveRange(_context.Stables);
        _context.StableMessages.RemoveRange(_context.StableMessages);
        _context.StableUsers.RemoveRange(_context.StableUsers);
        //_context.Rides.RemoveRange(_context.Rides); 
        //_context.RideDetails.RemoveRange(_context.RideDetails);
        //_context.RideReviews.RemoveRange(_context.RideReviews);
        _context.Trails.RemoveRange(_context.Trails);
        //_context.TrailReviews.RemoveRange(_context.TrailReviews);
        //_context.TrailFilters(_context.TrailFilters);
        _context.UserHikes.RemoveRange(_context.UserHikes);
        _context.EntityFeeds.RemoveRange(_context.EntityFeeds);


        await _context.SaveChangesAsync();

        //En merkelig feil, gjør at sekvensene for Id på Stables og Rides ikke starter på 1 etter cleardatabase og createcontent. 
        // Da blir det feil når Mock skal opprettes for StableMessages og Trails, da Id refererer til noe som ikke eksisterer
        //Det er ikke nødvendig og kjøre dette på Users og Horses, men de er med da det virker mest logisk.
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"TrailFavorites\" RESTART IDENTITY CASCADE");
        //await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Users\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Horses\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"UserRelations\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Messages\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Stables\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"StableMessages\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"StableUsers\" RESTART IDENTITY CASCADE");
        //await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Rides\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"Trails\" RESTART IDENTITY CASCADE");
        await _context.Database.ExecuteSqlRawAsync("TRUNCATE TABLE \"SystemSettings\" RESTART IDENTITY CASCADE");

        // Nullstill sekvensene manuelt (i tilfelle PostgreSQL ikke gjør det automatisk, noe som desverre skjer)
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Users_Id_seq\" RESTART WITH 1"); //Trenger ikke denne lenger da den er bygget om fra int til Guid
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Horses_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"UserRelations_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Messages_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Stables_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"StableMessages_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"StableUsers_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Users_Id_seq\" RESTART WITH 1"); //Denne skulle sikkert hete noe annet?
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Rides_Id_seq\" RESTART WITH 1");
        //await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"Trails_Id_seq\" RESTART WITH 1");
        await _context.Database.ExecuteSqlRawAsync("ALTER SEQUENCE \"SystemSettings_Id_seq\" RESTART WITH 1");

        return Ok("Database cleared, except Users, and IDs reset.");    
        }

    // Fyller opp tabellen systemSettings med systemSettingsMock.cs

}
