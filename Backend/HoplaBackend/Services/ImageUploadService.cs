// File: Services/ImageUploadService.cs
using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using HoplaBackend.Data;
using HoplaBackend.Helpers;
using Microsoft.EntityFrameworkCore;

namespace HoplaBackend.Services;

public interface IImageUploadService
{
    Task<string> UploadImageAsync(IFormFile image);
    Task<bool> SaveToDatabaseAsync(string table, Guid entityId, string fileName);
}

public class ImageUploadService : IImageUploadService
{
    private readonly AppDbContext _context;
    private readonly string _sftpHost;
    private readonly int _sftpPort;
    private readonly string _sftpUsername;
    private readonly string _sftpPassword;
    private readonly string _remoteDirectory;

    public ImageUploadService(AppDbContext context)
    {
        _context = context;
        _sftpHost = Environment.GetEnvironmentVariable("SFTP_HOST") ?? "";
        _sftpPort = int.TryParse(Environment.GetEnvironmentVariable("SFTP_PORT"), out var port) ? port : 22;
        _sftpUsername = Environment.GetEnvironmentVariable("SFTP_USER") ?? "";
        _sftpPassword = Environment.GetEnvironmentVariable("SFTP_PASS") ?? "";
        _remoteDirectory = Environment.GetEnvironmentVariable("SFTP_REMOTE_PATH") ?? "/";
    }

    public async Task<string> UploadImageAsync(IFormFile image)
    {
        using var stream = new MemoryStream();
        await image.CopyToAsync(stream);
        byte[] resizedImage = ImageUploadHelper.ResizeImage(stream.ToArray(), 1000);

        string fileName = $"{Guid.NewGuid()}.jpg";
        string tempPath = Path.Combine(Path.GetTempPath(), fileName);

        await File.WriteAllBytesAsync(tempPath, resizedImage);

        ImageUploadHelper.UploadToSftp(_sftpHost, _sftpPort, _sftpUsername, _sftpPassword, tempPath, fileName, _remoteDirectory);

        return fileName;
    }

    public async Task<bool> SaveToDatabaseAsync(string table, Guid entityId, string fileName)
    {
        var updated = false;

        switch (table)
        {
            case "Users":
                var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == entityId);
                if (user != null) { user.PictureUrl = fileName; updated = true; }
                break;
            case "MyHikes":
                var hike = await _context.UserHikes.FirstOrDefaultAsync(h => h.Id == entityId);
                if (hike != null) { hike.PictureUrl = fileName; updated = true; }
                break;
            case "Trails":
                var trail = await _context.Trails.FirstOrDefaultAsync(t => t.Id == entityId);
                if (trail != null) { trail.PictureUrl = fileName; updated = true; }
                break;
            case "Horses":
                var horse = await _context.Horses.FirstOrDefaultAsync(h => h.Id == entityId);
                if (horse != null) { horse.PictureUrl = fileName; updated = true; }
                break;
            case "Stables":
                var stable = await _context.Stables.FirstOrDefaultAsync(s => s.Id == entityId);
                if (stable != null) { stable.PictureUrl = fileName; updated = true; }
                break;

        }

        if (updated) await _context.SaveChangesAsync();

        return updated;
    }
}
