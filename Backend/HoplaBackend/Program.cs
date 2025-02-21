using System;
//using Serilog;
using Serilog.Context;

using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using Microsoft.Extensions.Configuration;
using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using HoplaBackend.Services;
using HoplaBackend.Helpers;
/*
using Serilog;
using System;
using System.Net.Http;
*/

var builder = WebApplication.CreateBuilder(args);

/*

var logtailUrl = "https://s1209901.eu-nbg-2.betterstackdata.com:443/SqQyvVrV6jWshrdibjNdoKkM";

Log.Logger = new LoggerConfiguration()
    .MinimumLevel.Debug()
    //.Enrich.FromLogContext() // Viktig for å legge til Controller-informasjon
    .WriteTo.Console()
    .WriteTo.Sink(new PutLog.LogTail(logtailUrl))  // Bruker vår Logtail-sink
    .CreateLogger();

//builder.Host.UseSerilog(); // ✅ Koble Serilog til ASP.NET Core
// Test logging med farger
Log.Information("🚀 Forsøker å starte programmet ");
//Log.Warning("⚠️ Dette er en advarsel");
//Log.Error("❌ Dette er en feilmelding");

//Log.CloseAndFlush();

//builder.Host.UseSerilog();

*/

// Prøver å hente database-url fra miljøvariabelen DATABASE_URL
string? databaseUrl = Environment.GetEnvironmentVariable("DATABASE_URL");

// Logger hva som skjer (se i Render logs)
Console.WriteLine("DATABASE_URL fra miljøvariabel: " + (databaseUrl ?? "IKKE FUNNET"));

string connectionString;

if (!string.IsNullOrEmpty(databaseUrl))
{
    // Konverterer Render PostgreSQL URL til et gyldig format for Npgsql
    var uri = new Uri(databaseUrl);
    var userInfo = uri.UserInfo.Split(':');

    connectionString = $"Host={uri.Host};Port={uri.Port};Database={uri.AbsolutePath.TrimStart('/')};" +
                       $"Username={userInfo[0]};Password={userInfo[1]};SSL Mode=Require;Trust Server Certificate=True;";

    Console.WriteLine("Bruker DATABASE_URL fra miljøvariabel. Programmet kjører på Render.com");
    //Log.Information("🚀 Forsøker å starte programmet på Render.com");
}
else
{
    // Fallback til lokal database fra appsettings.json
    connectionString = builder.Configuration.GetConnectionString("DefaultConnection")
        ?? throw new Exception("Database connection string is missing!");

    Console.WriteLine("Miljøvariabel IKKE funnet. Kjører programmet lokalt? DefaultConnection fra appsettings.json.");
    //Log.Information("🚀 Forsøker å starte programmet lokalt ");
}

// ✅ Sikrer at Serilog fungerer for ALLE controllere
/*
builder.Logging.ClearProviders();
builder.Logging.AddSerilog();
*/

// Legg til JWT-autentisering
//var secretKey = builder.Configuration["Jwt:Key"] ?? "SuperHemmeligNøkkelSomErLang123!"; // Hent fra config eller sett default
var secretKey = builder.Configuration["Jwt:Key"];


if (string.IsNullOrEmpty(secretKey) || secretKey.Length < 32)
{
    throw new Exception("!!!!    !!!!   Jwt:Key er for kort! Må være minst 32 tegn lang.");
}
Console.WriteLine($"🔑 JWT Secret Key: {builder.Configuration["Jwt:Key"]}");

Console.WriteLine(secretKey);
/*
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.RequireHttpsMetadata = false; // Sett til true i produksjon
    options.SaveToken = true;
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey)),
        ValidateIssuer = false,
        ValidateAudience = false,
        ClockSkew = TimeSpan.Zero // Ingen forsinkelse på token-utløp
    };
});
*/

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.RequireHttpsMetadata = false;
        options.SaveToken = true;
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey)),
            ValidateIssuer = false,
            ValidateAudience = false,
            ClockSkew = TimeSpan.Zero
        };
    });
// Konfigurer Entity Framework med riktig connection-string
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddControllers();//options =>
//{
//    options.Filters.Add<PutLog.LogEnricherFilter>(); // Bruk full namespace
//});
builder.Services.AddScoped<IUserService, UserService>();  // 🔹 Registrer UserService
builder.Services.AddScoped<Authentication>();
builder.Services.AddScoped<SystemSettingService>();
/*
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowFrontend",
        policy => policy.WithOrigins("http://localhost:3000", "https://adminhopla.render.com") // Erstatt med riktig frontend-URL
                        .AllowAnyMethod()
                        .AllowAnyHeader());
});
*/
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        policy => policy.AllowAnyOrigin()
                        .AllowAnyMethod()
                        .AllowAnyHeader());
});


var app = builder.Build();
app.UseHttpsRedirection();

app.UseRouting();
//app.UseCors("AllowFrontend"); // Aktiver CORS-policyen
app.UseCors("AllowAll");

app.Use(async (context, next) =>
{
    if (context.Request.Method == "OPTIONS")
    {
        context.Response.StatusCode = 200;
        return;
    }
    await next();
});

app.UseAuthentication(); // Aktiver JWT-autentisering
app.UseAuthorization();  // Aktiver autorisasjon

app.MapControllers();

app.Run();

//Log.Information("🚀 Programmet startet ");
//Log.CloseAndFlush();
