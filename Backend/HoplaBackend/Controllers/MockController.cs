//using MyApp.Mock;
using Microsoft.AspNetCore.Mvc;
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
        _context.FriendRequests.RemoveRange(_context.FriendRequests);
        _context.Messages.RemoveRange(_context.Messages);
        _context.Stables.RemoveRange(_context.Stables);
        _context.StableMessages.RemoveRange(_context.StableMessages);
        //_context.Friendrequests.RemoveRange(_context.Friendrequests);
        //_context.Rides.RemoveRange(_context.Rides); 
        //_context.RideDetails.RemoveRange(_context.RideDetails);
        //_context.RideReviews.RemoveRange(_context.RideReviews);
        //_context.Trails.RemoveRange(_context.Trails);
        //_context.TrailReviews(_context.TrailReviews);
        //_context.Filters(_context.Filters);

        await _context.SaveChangesAsync();
        return Ok("Database tømt!");
    }

    [HttpPost("createusers")]
    public async Task<IActionResult> CreateUsers()
    {
        if (_context.Users.Any()) { return NoContent(); }

        var users = UserMock.GetUsers();
        _context.Users.AddRange(users);
        await _context.SaveChangesAsync();

        return Created();
    }

    [HttpPost("createhorses")]
    public async Task<IActionResult> CreateHorses()
    {
        if (_context.Horses.Any()) { return NoContent(); }

        var existingUsers = _context.Users.ToList();
        var horses = HorseMock.GetHorses(existingUsers);
        _context.Horses.AddRange(horses);
        await _context.SaveChangesAsync();

        return Created();
    }
    [HttpPost("createfriendrequests")]
    public async Task<IActionResult> CreateFriendRequests()
    {
        if (_context.FriendRequests.Any()) { return NoContent(); }

        var existingUsers = _context.Users.ToList();
        var friendRequests = FriendRequestMock.GetFriendRequests(existingUsers);
        _context.FriendRequests.AddRange(friendRequests);
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
        var stables = StableMock.GetStables();
        _context.Stables.AddRange(stables);
        await _context.SaveChangesAsync();

        return Created();
    }

    [HttpPost("createstablemessages")]
    public async Task<IActionResult> CreateStableMessages()
    {
        if (_context.StableMessages.Any()) { return NoContent(); }
        var stableMessages = StableMessageMock.GetStableMessages();
        _context.StableMessages.AddRange(stableMessages);
        await _context.SaveChangesAsync();

        return Created();
    }

    [HttpGet("testdist")] //bare en test-
    //public float testdistance() 
    //{
    public IActionResult GetDistances()
    {
        var distances = testdistance();
        return Ok(new
        {
            PythagorasSimple = distances.PythagorasSimple,
            PythagorasImproved = distances.PythagorasImproved,
            Haver_sine = distances.Haver_sine
        });
    }
    public (float PythagorasSimple, float PythagorasImproved, float Haver_sine) testdistance()  
    {
        //Gjøvik
        //var lat1 = 60.7925;
        //var long1 = 10.695;
        
        //Lommedalen
        var lat1 = 59.95;
        var long1 = 10.466667;

        //Biri
        var lat2 = 60.95558;
        var long2 = 10.6115;
        
        // Jordens radius i km
        var R = 6371.0;

        // Enkel Pythagoras (ca. 18.6780 km) Regner enkelt ut
        double degreeDist = 40008 / 360.0;
        double sideX_simple = Math.Cos((lat1 + lat2) * Math.PI / 360) * Math.Abs(long1 - long2) * degreeDist;
        double sideY_simple = Math.Abs(lat2 - lat1) * degreeDist;
        float pythagorasSimple = (float)Math.Sqrt(Math.Pow(sideX_simple, 2) + Math.Pow(sideY_simple, 2));

        // Metode 2: Forbedret Pythagoras (ca. 18.6796 km) Pytagorasvariant som tar hensyn til at jorda har større omkrets rundt ekvator enn rundt polene
        double latMid = (lat1 + lat2) / 2.0 * Math.PI / 180.0;
        double latDist = 111.132; //avstanden mellom hver grad rundt polene
        double longDist = 111.320 * Math.Cos(latMid); //avstanden mellom hver grad rundt ekvator. 
        double sideX_improved = longDist * Math.Abs(long1 - long2);
        double sideY_improved = latDist * Math.Abs(lat1 - lat2);
        float pythagorasImproved = (float)Math.Sqrt(sideX_improved * sideX_improved + sideY_improved * sideY_improved);

        //Metode 3: Haversine-formelen (ca. 18.6883 km) Tar hensyn til at jorda er buet. 
        double dlat = (lat2 - lat1) * Math.PI / 180.0;
        double dlon = (long2 - long1) * Math.PI / 180.0;
        double a = Math.Sin(dlat / 2) * Math.Sin(dlat / 2) + Math.Cos(lat1 * Math.PI / 180) * Math.Cos(lat2 * Math.PI / 180) * Math.Sin(dlon / 2) * Math.Sin(dlon / 2);
        double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
        float haver_sine = (float)(R * c);

        return (pythagorasSimple, pythagorasImproved, haver_sine);
    }
/*
        //Gjøvik lat1 60.7925, long1 10.695 
        //Biri Lat2 = 60.95558, Long2 = 10.6115
        var lat1 = 60.7925;
        var long1 = 10.695;
        var lat2 = 60.95558;
        var long2 = 10.6115;

        //Poor mans distance, enkleste pytagoras. Lite resurskrevende. Egner seg til korte distanser under 5km
        var degreeDist = 40000/360; //avstanden mellom lat rundt ekvator
        //A^2 + B^2 = C^2
        //Avstanden mellom lat vil variere ut i fra hvilken long man befinner seg på. LatNordpolen = 0. LatEkvator = 90
        var sideA = Math.Cos((long1+long2)/2) * Math.Abs(lat1-lat2) * degreeDist;
        var sideB = Math.Abs(long2-long1)*degreeDist;
        
        //Denne tar litt hensyn til at jorda er epitisk og har større omkrets rundt ekvator pga sentrifugalkraften.

        //Haversine
        //var earthRadius = 6371.0; // i km

        // Grader til radianer
        double latMid = (lat1 + lat2) / 2.0 * Math.PI / 180.0;  

        // Avstand per breddegrad (varierer med cosinus)
        double latDist = 111.132 * Math.Cos(latMid);
        double longDist = 111.320;  

        // Beregn avstand mellom punktene
        double sideA2 = latDist * Math.Abs(lat1 - lat2);
        double sideB2 = longDist * Math.Abs(long1 - long2);
        return (float)Math.Sqrt(Math.Pow(sideA2,2) + Math.Pow(sideB2,2));
    }
*/
    [HttpPost("createcontent")]
    public async Task<IActionResult> CreateDbContent()
    {
        //await ClearDatabase();
        await CreateUsers();
        await CreateHorses();
        await CreateFriendRequests();
        await CreateMessages();
        await CreateStables();
        //await CreateStableUSers();
        await CreateStableMessages();
        //await CreateRides();
        //await CreateRideDetails();
        //await CreateRideReviews();
        //await CreateTrails();
        //await CreateTrailReviews();
        //await CreateFilters;

        return Created();
    }
}
