using HoplaBackend.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Controllers
{
    [Route("misc")]
    [ApiController]
    public class MiscController : ControllerBase
    {
        private readonly AppDbContext _context;

        public MiscController(AppDbContext context)
        {
            _context = context;
        }

        // Endpoint for å oppdatere FriendsCount og HorseCount for alle brukere
        [HttpPost("updateuser")]
        public async Task<IActionResult> UpdateUserCounts()
        {
            var users = await _context.Users.ToListAsync();

            foreach (var user in users)
            {
                // Teller antall venner (kun relasjoner med status "FRIENDS")
                user.FriendsCount = await _context.UserRelations
                    .Where(ur => (ur.FromUserId == user.Id || ur.ToUserId == user.Id) && ur.Status == "FRIENDS")
                    .CountAsync();

                // Teller antall hester brukeren eier
                user.HorseCount = await _context.Horses
                    .Where(h => h.UserId == user.Id)
                    .CountAsync();
            }

            await _context.SaveChangesAsync();
            return Ok(new { message = "User friend and horse counts updated successfully" });
        }
    }
}
