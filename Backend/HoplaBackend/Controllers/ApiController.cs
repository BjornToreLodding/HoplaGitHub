using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;

[ApiController]
[Route("api")]
public class ApiController : ControllerBase
{
    private static readonly Stopwatch _stopwatch = Stopwatch.StartNew();
    private static int _requestCount = 0;
    private static int _errorCount = 0;

    // GET: /api/helloworld
    [HttpGet("helloworld")]
    public IActionResult HelloWorld()
    {
        try
        {
            _requestCount++;
            return Ok(new { Message = "Hello, World!" });
        }
        catch
        {
            _errorCount++;
            return StatusCode(500, new { Error = "An unexpected error occurred." });
        }
    }

    // GET: /api/status
    [HttpGet("status")]
    public IActionResult Status()
    {
        try
        {
            _requestCount++;
            return Ok(new
            {
                Uptime = $"{_stopwatch.Elapsed.TotalSeconds:F2} seconds",
                RequestCount = _requestCount,
                ErrorCount = _errorCount
            });
        }
        catch
        {
            _errorCount++;
            return StatusCode(500, new { Error = "An unexpected error occurred." });
        }
    }
}
/*
   [Route("api2/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly AppDbContext _context;

        public UsersController(AppDbContext context)
        {
            _context = context;
        }

        // 🔹 TEST-ENDPOINT: Hent alle brukere fra `users`-tabellen
        [HttpGet("test-db")]
        public IActionResult TestDatabase()
        {
            try
            {
                var users = _context.Users.ToList();
                if (users.Any())
                {
                    return Ok(users); // ✅ Returnerer brukerne som JSON
                }
                return NotFound("Ingen brukere funnet.");
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Feil ved tilkobling til databasen: {ex.Message}");
            }
        }
    }

*/
