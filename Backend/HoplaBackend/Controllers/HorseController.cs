using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HoplaBackend.Data;
using HoplaBackend.Models;
using HoplaBackend.Helpers;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using HoplaBackend.DTOs;
using HoplaBackend.Services;

namespace HoplaBackend.Controllers;


/// <summary>
/// API controller for managing horse-related operations.
/// </summary>
[Route("horses")]
[ApiController]
public class HorseController : ControllerBase
{
    private readonly AppDbContext _context;
    private readonly ImageUploadService _imageUploadService;

    /// <summary>
    /// Initializes a new instance of the <see cref="HorseController"/> class.
    /// </summary>
    /// <param name="context">The database context.</param>
    /// <param name="imageUploadService">Service for uploading images.</param>
    public HorseController(AppDbContext context, ImageUploadService imageUploadService)
    {
        _context = context;
        _imageUploadService = imageUploadService;
    }

    /// <summary>
    /// Gets all horses for a specific user, excluding deleted horses.
    /// </summary>
    /// <param name="userId">Optional user ID. If not provided, it will be read from the token.</param>
    /// <returns>A list of the user's horses or 404 if none are found.</returns>
    [Authorize]
    [HttpGet("userhorses")]
    public async Task<IActionResult> GetUserHorses([FromQuery] Guid? userId)
    {
        // If no userId is provided, get the user ID from the token
        if (!userId.HasValue)
        {
            if (!Guid.TryParse(User.FindFirstValue(ClaimTypes.NameIdentifier), out var parsedUserId))
            {
                return Unauthorized(new { message = "Invalid token or user ID" });
            }
            userId = parsedUserId;
        }

        // Fetch the user's horses, excluding deleted horses
        var horses = await _context.Horses
            .Where(h => h.UserId == userId && !h.IsDeleted) // Only active horses
            .Select(h => new 
            {
                h.Id,
                h.Name,
                horsePictureUrl = PictureHelper.BuildPictureUrl(h.PictureUrl, "HorsePictureList"),
            })
            .ToListAsync(); // Requires Microsoft.EntityFrameworkCore

        // Return 404 if no horses are found
        if (horses == null || !horses.Any())
        {
            return NotFound(new { message = "No horses found" });
        }

        // Return the list of horses
        return Ok(horses);
    }

    /// <summary>
    /// Gets a horse by its ID, including user data, and returns detailed information.
    /// Only non-deleted horses are returned.
    /// </summary>
    /// <param name="id">The ID of the horse to retrieve.</param>
    /// <returns>Returns 200 OK with horse details if found, or 404 Not Found if the horse does not exist or is deleted.</returns>
    [Authorize]
    [HttpGet("{id}")]
    public async Task<IActionResult> GetHorse(Guid id)
    {
        // Fetch the horse by ID, including user data
        var horse = await _context.Horses
            .Include(h => h.User)
            .FirstOrDefaultAsync(h => h.Id == id && !h.IsDeleted); // Only non-deleted horses

        // Return 404 if the horse is not found or is deleted
        if (horse == null)
        {
            return NotFound();
        }

        // Return horse details
        return Ok(new
        {
            name = horse.Name,
            horsePictureUrl = PictureHelper.BuildPictureUrl(horse.PictureUrl, "HorsePictureSelect"),
            breed = horse.Breed,
            dob = horse.Dob,
            age = horse.Dob.HasValue
                ? DateTime.UtcNow.Year - horse.Dob.Value.Year - (DateTime.UtcNow.DayOfYear < horse.Dob.Value.DayOfYear ? 1 : 0)
                : (int?)null // Set age to null if date of birth is missing
        });
    }

    /// <summary>
    /// Creates a new horse entry associated with the logged-in user.
    /// </summary>
    /// <param name="request">The form data containing horse details and optional picture upload.</param>
    /// <returns>Returns 200 OK if the horse is successfully created, or 400/401 if invalid data is provided.</returns>
    [Authorize]
    [HttpPost("create")]
    public async Task<IActionResult> CreateHorse([FromForm] RegisterHorseForm request)
    {
        // Get the user ID from the token
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
            return Unauthorized(new { message = "Invalid token or user ID" });

        string? pictureUrl = null;

        // Upload the picture if provided
        if (request.Image != null)
        {
            var fileName = await _imageUploadService.UploadImageAsync(request.Image);
            pictureUrl = fileName;
        }

        DateOnly? dob = null;

        // Try to build the date of birth if all parts are provided
        if (request.Year.HasValue && request.Month.HasValue && request.Day.HasValue)
        {
            try
            {
                dob = new DateOnly(request.Year.Value, request.Month.Value, request.Day.Value);
            }
            catch
            {
                return BadRequest(new { error = "Invalid date of birth." });
            }
        }

        // Create the horse entity
        var horse = new Horse 
        {
            Name = request.Name,
            UserId = parsedUserId,
            Breed = request.Breed,
            PictureUrl = pictureUrl,
            Dob = dob
        };

        // Save the horse to the database
        _context.Horses.Add(horse);
        await _context.SaveChangesAsync();

        return Ok("Horse Created");
    }

    /// <summary>
    /// Soft deletes a horse owned by the logged-in user.
    /// </summary>
    /// <param name="horseId">The ID of the horse to delete.</param>
    /// <returns>Returns 200 OK if the horse is successfully marked as deleted, or 404/401 if not found or unauthorized.</returns>
    [Authorize]
    [HttpDelete("delete/{horseId}")]
    public async Task<IActionResult> DeleteHorse(Guid horseId)
    {
        // Get the user ID from the token
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

        if (string.IsNullOrEmpty(userIdString) || !Guid.TryParse(userIdString, out Guid parsedUserId))
            return Unauthorized(new { message = "Invalid token or user ID" });

        // Fetch the horse belonging to the user
        var horse = await _context.Horses.FirstOrDefaultAsync(h =>
            h.Id == horseId && h.UserId == parsedUserId);

        if (horse == null)
            return NotFound(new { message = "Horse not found or you do not have permission to delete it" });

        // Perform soft delete
        horse.IsDeleted = true;
        await _context.SaveChangesAsync();

        return Ok(new { message = "Horse deleted" });
    }

}
