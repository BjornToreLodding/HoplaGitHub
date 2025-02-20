using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.Helpers;
using Helpers;

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

        [HttpGet("int/{horseId}")] 
        public async Task<IActionResult> GetIntHorse(int horseId)
        {
            //var endpointName = ControllerContext.ActionDescriptor.ActionName;
            var controllerName = ControllerContext.ActionDescriptor.ControllerName;
            Guid newGuid = CustomConvert.IntToGuid(controllerName, horseId);
        
            return await GetHorse(newGuid);
        }
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
                id = horse.Id,
                name = horse.Name,
                userId = horse.UserId,
                userName = horse.User.Name // Henter brukerens navn
            });
        }
    }
}