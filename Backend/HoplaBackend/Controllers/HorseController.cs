using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.Helpers;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace HoplaBackend.Controllers
{
 
    [Route("horses")]
    [ApiController]
    public class HorseController : ControllerBase
    {
        private readonly AppDbContext _context;

        public HorseController(AppDbContext context)
        {
            _context = context;
        }
    
    [Authorize]
    [HttpGet("userhorses")]
    public async Task<IActionResult> GetUserHorses()
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

        // Hent brukerens hester
        var horses = await _context.Horses
            .Where(h => h.User.Id == userId) // Sammenligner riktig Guid
            .Select(h => new 
            {
                h.Id,
                h.Name,
                horsePictureUrl = !string.IsNullOrEmpty(h.HorsePictureUrl) 
                    ? $"{h.HorsePictureUrl}?h=64&w=64&fit=crop"
                    : ""
            })
            .ToListAsync(); // Krever Microsoft.EntityFrameworkCore

        if (horses == null || !horses.Any())
        {
            return NotFound(new { message = "Ingen hester funnet" });
        }

        return Ok(horses);
    }
        [Authorize]
        [HttpGet("{id}")]
        public async Task<IActionResult> GetHorse(Guid id)
        {
            //var horse = await _context.Horses.FindAsync(id);
            //hestekommentar;
            var horse = await _context.Horses
                .Include(h => h.User) // Henter brukerdata også
                .FirstOrDefaultAsync(h => h.Id == id);
            if (horse == null)
            {
                return NotFound(); // Returnerer 404 hvis hesten ikke finnes
            }

            return Ok(new
            {
                //id = horse.Id,
                name = horse.Name,
                horsePictureUrl = !string.IsNullOrEmpty(horse.HorsePictureUrl) 
                    ? $"{horse.HorsePictureUrl}?h=200&w=200&fit=crop"
                    : "",
                breed = horse.Breed,
                dob = horse.Dob,
                age = horse.Dob.HasValue 
                    ? DateTime.UtcNow.Year - horse.Dob.Value.Year - 
                        (DateTime.UtcNow.DayOfYear < horse.Dob.Value.DayOfYear ? 1 : 0) 
                    : (int?)null, // Setter alder til null hvis fødselsdato mangler
                
            });
        }
    }
}
/*
        //brukes ikke lenger
        [HttpGet("int/{horseId}")] 
        public async Task<IActionResult> GetIntHorse(int horseId)
        {
            //var endpointName = ControllerContext.ActionDescriptor.ActionName;
            var controllerName = ControllerContext.ActionDescriptor.ControllerName;
            Guid newGuid = CustomConvert.IntToGuid(controllerName, horseId);
        
            return await GetHorse(newGuid);
        }
*/