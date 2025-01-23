using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System.Diagnostics;

var builder = WebApplication.CreateBuilder(args);

// Legg til nødvendige tjenester (ingen database kreves her)
builder.Services.AddControllers();

var app = builder.Build();

// Aktiver HTTPS-omdirigering
app.UseHttpsRedirection();

// Aktiver routing
app.MapControllers();

app.Run();
