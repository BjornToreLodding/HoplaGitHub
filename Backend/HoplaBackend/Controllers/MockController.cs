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

    [HttpGet("testdist")]
    public float testdistance() 
    {
        //Gjøvik lat1 60.7925, long1 10.695 
        //Biri Lat2 = 60.95558, Long2 = 10.6115
        var lat1 = 60.7925;
        var long1 = 10.695;
        var lat2 = 60.95558;
        var long2 = 10.6115;
        var degreeDist = 40000/360; //avstanden mellom lat rundt ekvator
        //A^2 + B^2 = C^2
        //Avstanden mellom lat vil variere ut i fra hvilken long man befinner seg på. LatNordpolen = 0. LatEkvator = 90
        var sideA = Math.Cos((long1+long2)/2) * Math.Abs(lat1-lat2) * degreeDist;
        var sideB = Math.Abs(long2-long1)*degreeDist;
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
