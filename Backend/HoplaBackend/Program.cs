using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System.Diagnostics;
using Microsoft.EntityFrameworkCore;
using MyApp.Data;

var builder = WebApplication.CreateBuilder(args);

// Les databaseforbindelsen fra appsettings.json
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
);

// Legg til nødvendige tjenester (ingen database kreves her)
builder.Services.AddControllers();

var app = builder.Build();

// Aktiver HTTPS-omdirigering
app.UseHttpsRedirection();

// Aktiver routing
app.MapControllers();

app.Run();
