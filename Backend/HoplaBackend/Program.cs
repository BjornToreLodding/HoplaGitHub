using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using Microsoft.Extensions.Configuration;
using System;

var builder = WebApplication.CreateBuilder(args);

// Prøver å hente database-url fra miljøvariabelen DATABASE_URL
string? databaseUrl = Environment.GetEnvironmentVariable("DATABASE_URL");

string connectionString;

if (!string.IsNullOrEmpty(databaseUrl))
{
    // Konverterer Render PostgreSQL URL til et gyldig format for Npgsql
    var uri = new Uri(databaseUrl);
    var userInfo = uri.UserInfo.Split(':');

    connectionString = $"Host={uri.Host};Port={uri.Port};Database={uri.AbsolutePath.TrimStart('/')};" +
                       $"Username={userInfo[0]};Password={userInfo[1]};SSL Mode=Require;Trust Server Certificate=True;";
}
else
{
    // Fallback til appsettings.json hvis DATABASE_URL ikke er satt
    connectionString = builder.Configuration.GetConnectionString("DefaultConnection")
        ?? throw new Exception("Database connection string is missing!");
}
Console.ForegroundColor = ConsoleColor.Blue;
Console.WriteLine(connectionString);
Console.ResetColor();
// Konfigurer Entity Framework med riktig connection-string
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionString));

builder.Services.AddControllers();
var app = builder.Build();
app.UseHttpsRedirection();
app.MapControllers();
app.Run();

/*
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System.Diagnostics;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using Microsoft.Extensions.Configuration;
using System;

var builder = WebApplication.CreateBuilder(args);

// Prøver først å hente connection string fra miljøvariabler
string connectionString = Environment.GetEnvironmentVariable("DATABASE_URL") 
    ?? builder.Configuration.GetConnectionString("DefaultConnection")
    ?? throw new Exception("Database connection string is missing!");

// Konfigurer Entity Framework med riktig connection-string
builder.Services.AddDbContext<AppDbContext>(options => //Rød strek under MyDbContext
    options.UseNpgsql(connectionString));

// Les databaseforbindelsen fra appsettings.json
//builder.Services.AddDbContext<AppDbContext>(options =>
//    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
//);

//"DefaultConnection": "Host=localhost;Port=5432;Database=hopla3;Username=postgres;Password=Hopla"
    
// Legg til nødvendige tjenester (ingen database kreves her)
builder.Services.AddControllers();

var app = builder.Build();

// Aktiver HTTPS-omdirigering
app.UseHttpsRedirection();

// Aktiver routing
app.MapControllers();

app.Run();
*/