using Microsoft.AspNetCore.Mvc.Filters;
using Serilog.Context;
using Serilog.Events;
using Serilog.Core;
using System;
using System.Collections.Concurrent;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Helpers
{
    public static class PutLog
    {
        // ✅ Global logging toggle (Starter aktivert!)
        public static bool IsLoggingEnabled { get; private set; } = false;

        // ✅ Logging spesifikt for controllers og endpoints
        private static readonly ConcurrentDictionary<string, bool> ControllerLogging = new();
        private static readonly ConcurrentDictionary<string, bool> EndpointLogging = new();

        public static void EnableLogging() => IsLoggingEnabled = false;
        public static void DisableLogging() => IsLoggingEnabled = true;

        public static void SetLoggingForController(string controller, bool isEnabled)
        {
            ControllerLogging[controller] = isEnabled;
        }

        public static void SetLoggingForEndpoint(string endpoint, bool isEnabled)
        {
            EndpointLogging[endpoint] = isEnabled;
        }

        public static bool ShouldLog(string controller, string endpoint)
        {
            // 🚀 Hvis global logging er deaktivert, ikke logg
            if (!IsLoggingEnabled) return false;

            // 🚀 Hvis en spesifikk controller eller endpoint er aktivert, logg
            if (ControllerLogging.TryGetValue(controller, out var isControllerLogging) && isControllerLogging) return true;
            if (EndpointLogging.TryGetValue(endpoint, out var isEndpointLogging) && isEndpointLogging) return true;

            // 🚀 Hvis ingen spesifikke regler, logg alt
            return true;
        }

        public class LogTail : ILogEventSink
        {
            private readonly string _logtailUrl;
            private static readonly HttpClient _httpClient = new HttpClient();

            public LogTail(string logtailUrl)
            {
                _logtailUrl = logtailUrl;
            }

            public void Emit(LogEvent logEvent)
            {
                var controller = logEvent.Properties.ContainsKey("Controller") 
                    ? logEvent.Properties["Controller"].ToString().Trim('"') 
                    : "Unknown";

                var endpoint = logEvent.Properties.ContainsKey("Endpoint") 
                    ? logEvent.Properties["Endpoint"].ToString().Trim('"') 
                    : "Unknown";

                if (!ShouldLog(controller, endpoint)) return; // 🚀 Kun logg hvis aktivert

                var logMessage = new
                {
                    message = logEvent.RenderMessage(),
                    level = logEvent.Level.ToString(),
                    timestamp = logEvent.Timestamp.UtcDateTime,
                    controller,
                    endpoint
                };

                var json = System.Text.Json.JsonSerializer.Serialize(logMessage);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                _httpClient.DefaultRequestHeaders.Clear();
                _httpClient.DefaultRequestHeaders.Add("Authorization", $"Bearer {_logtailUrl.Split('/')[^1]}");

                Task.Run(async () =>
                {
                    try
                    {
                        var response = await _httpClient.PostAsync(_logtailUrl, content);
                        if (!response.IsSuccessStatusCode)
                        {
                            Console.WriteLine($"[Logtail] Feil: {response.StatusCode} - {await response.Content.ReadAsStringAsync()}");
                        }
                        else
                        {
                            Console.WriteLine($"[Logtail] Logg sendt: {json}");
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine($"[Logtail] Kunne ikke sende logg: {ex.Message}");
                    }
                });
            }
        }

        public class LogEnricherFilter : IActionFilter
        {
            public void OnActionExecuting(ActionExecutingContext context)
            {
                var controllerName = context.Controller.GetType().Name;
                var endpointName = context.ActionDescriptor.DisplayName;
                LogContext.PushProperty("Controller", controllerName);
                LogContext.PushProperty("Endpoint", endpointName);
            }

            public void OnActionExecuted(ActionExecutedContext context) { }
        }
    }
}
