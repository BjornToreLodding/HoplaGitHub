using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System.Diagnostics;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;
using Microsoft.Extensions.Configuration;

var builder = WebApplication.CreateBuilder(args);

// Les databaseforbindelsen fra appsettings.json
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
);
//"DefaultConnection": "Host=localhost;Port=5432;Database=hopla3;Username=postgres;Password=Hopla"
    
// Legg til nødvendige tjenester (ingen database kreves her)
builder.Services.AddControllers();

var app = builder.Build();

// Aktiver HTTPS-omdirigering
app.UseHttpsRedirection();

// Aktiver routing
app.MapControllers();

app.Run();
