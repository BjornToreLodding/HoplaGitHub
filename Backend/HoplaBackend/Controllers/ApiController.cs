using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;
using HoplaBackend.Data;
using Serilog;
using HoplaBackend.Helpers;


[ApiController]
[Route("div")]
public class ApiController : ControllerBase
{
    private readonly AppDbContext _context;

    private static readonly Stopwatch _stopwatch = Stopwatch.StartNew();
    private static int _requestCount = 0;
    private static int _errorCount = 0;

    // ✅ Dependency Injection krever nå at AppDbContext ikke er null
    public ApiController(AppDbContext context)
    {
        _context = context ?? throw new ArgumentNullException(nameof(context));
    }

    // 📌 GET: /div/helloworld
    [HttpGet("helloworld")]
    public IActionResult HelloWorld()
    {
        try
        {
            _requestCount++;
            Log.Information("📢 HelloWorld-endpoint kalt.");
            return Ok(new { Message = "Hello, World!" });
        }
        catch (Exception ex)
        {
            _errorCount++;
            Log.Error("❌ Feil i HelloWorld-endpoint: {ErrorMessage}", ex.Message);
            return StatusCode(500, new { Error = "En uventet feil oppstod." });
        }
    }

    // 📌 GET: /div/status
    [HttpGet("status")]
    public IActionResult Status()
    {
        try
        {
            _requestCount++;
            Log.Information("📢 Status-endpoint kalt.");

            return Ok(new
            {
                Uptime = $"{_stopwatch.Elapsed.TotalSeconds:F2} sekunder",
                RequestCount = _requestCount,
                ErrorCount = _errorCount
            });
        }
        catch (Exception ex)
        {
            _errorCount++;
            Log.Error("❌ Feil i Status-endpoint: {ErrorMessage}", ex.Message);
            return StatusCode(500, new { Error = "En uventet feil oppstod." });
        }
    }

    // 📌 GET: /div/database - Sjekker databaseforbindelse
    [HttpGet("database")]
    public async Task<IActionResult> CheckDatabaseConnection()
    {
        Log.Information("📢 Database-status-endpoint kalt.");
        
        try
        {
            bool canConnect = await _context.Database.CanConnectAsync();

            if (canConnect)
            {
                Log.Information("✅ Databaseforbindelsen er OK.");
                return Ok(new { status = "OK", message = "Databaseforbindelsen er oppe og fungerer." });
            }
            else
            {
                Log.Warning("⚠️ Klarte ikke å koble til databasen.");
                return StatusCode(500, new { status = "Feil", message = "Klarte ikke å koble til databasen." });
            }
        }
        catch (Exception ex)
        {
            Log.Error("❌ Feil i databaseforbindelsessjekk: {ErrorMessage}", ex.Message);
            return StatusCode(500, new { status = "Feil", message = "Databaseforbindelsessjekk feilet.", error = ex.Message });
        }
    }
    [HttpGet("logging")]
    public IActionResult SetLoggingState([FromQuery] string? global, [FromQuery] string? controller, [FromQuery] string? endpoint)
    {
        if (!string.IsNullOrEmpty(global))
        {
            if (global.ToLower() == "on")
            {
                PutLog.EnableLogging();
                Log.Information("🟢 Global logging er AKTIVERT!");
                return Ok(new { status = "OK", message = "Global logging er nå aktivert." });
            }
            else if (global.ToLower() == "off")
            {
                PutLog.DisableLogging();
                Log.Information("🔴 Global logging er DEAKTIVERT!");
                return Ok(new { status = "OK", message = "Global logging er nå deaktivert." });
            }
        }

        if (!string.IsNullOrEmpty(controller))
        {
            bool isEnabled = !controller.StartsWith("-");
            PutLog.SetLoggingForController(controller.TrimStart('-'), isEnabled);
            Log.Information($"🔍 Logging for controller '{controller.TrimStart('-')}' er {(isEnabled ? "AKTIVERT" : "DEAKTIVERT")}!");
            return Ok(new { status = "OK", message = $"Logging for controller '{controller.TrimStart('-')}' er {(isEnabled ? "aktivert" : "deaktivert")}" });
        }

        if (!string.IsNullOrEmpty(endpoint))
        {
            bool isEnabled = !endpoint.StartsWith("-");
            PutLog.SetLoggingForEndpoint(endpoint.TrimStart('-'), isEnabled);
            Log.Information($"🔍 Logging for endpoint '{endpoint.TrimStart('-')}' er {(isEnabled ? "AKTIVERT" : "DEAKTIVERT")}!");
            return Ok(new { status = "OK", message = $"Logging for endpoint '{endpoint.TrimStart('-')}' er {(isEnabled ? "aktivert" : "deaktivert")}" });
        }

        return BadRequest(new { status = "Feil", message = "Ugyldige parametere. Bruk '?global=on|off', '?controller=UserController' eller '?endpoint=GetAllUsers'." });
    }


}
