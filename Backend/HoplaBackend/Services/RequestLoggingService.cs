using Microsoft.AspNetCore.Mvc.Filters;
using Serilog;
using System.Security.Claims;
using System.Text.Json;

namespace HoplaBackend.Services;

public class RequestLoggingService : IActionFilter
{
    private readonly Serilog.ILogger _logger;

    public RequestLoggingService()
    {
        _logger = Log.Logger;

    }

    public void OnActionExecuting(ActionExecutingContext context)
    {
        var endpoint = context.ActionDescriptor.DisplayName ?? "(ukjent endpoint)";
        var user = context.HttpContext.User;

        string? userInfo = user.Identity?.IsAuthenticated == true
            ? user.FindFirst(ClaimTypes.Email)?.Value
              ?? user.FindFirst(ClaimTypes.Name)?.Value
              ?? user.Identity?.Name
            : "Ukjent / ikke innlogget";

        _logger.Information("➡️ Endpoint: {Endpoint} | Bruker: {User}", endpoint, userInfo);

        foreach (var arg in context.ActionArguments)
        {
            if (arg.Value is IFormFile || arg.Value is IFormFileCollection)
            {
                _logger.Information("📎 {Key}: (fil - ikke logget)", arg.Key);
                continue;
            }

            try
            {
                var serialized = JsonSerializer.Serialize(arg.Value, new JsonSerializerOptions
                {
                    WriteIndented = false,
                    MaxDepth = 3
                });

                _logger.Information("   🔸 {Key}: {Value}", arg.Key, serialized);
            }
            catch
            {
                _logger.Information("   🔸 {Key}: (ikke loggbar)");
            }
        }
    }

    public void OnActionExecuted(ActionExecutedContext context)
    {
        // Valgfritt: legg inn statuskode eller responstid
    }
}
