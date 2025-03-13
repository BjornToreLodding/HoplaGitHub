using Renci.SshNet;
using Renci.SshNet.Sftp;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Net;
using System.Threading.Tasks;


[Route("upload")]
[ApiController]
public class UploadController : ControllerBase
{
    private readonly string _ftpServer = "sftp.domeneshop.no";
    private readonly string _ftpUsername = "hopla";
    private readonly string _ftpPassword = "Reell-300-hr-lam-Gjess";
    
    [HttpPost]
    public async Task<IActionResult> UploadImage([FromForm] IFormFile image, [FromForm] string table)
    {
        Console.WriteLine("Upload endpoint");
        if (image == null || image.Length == 0)
        {
            Console.WriteLine("if - no file uploaded");
            return BadRequest(new { error = "No file uploaded." });
        }
        // Sjekk om tabellen er gyldig
        string[] allowedTables = { "Users", "MyHikes", "Trails", "Horses" };
        if (!Array.Exists(allowedTables, t => t.Equals(table, StringComparison.OrdinalIgnoreCase)))
        {
            Console.WriteLine("if - invalid table name");
            return BadRequest(new { error = "Invalid table name." });
        }

        try
        {
            // Reduser bildestørrelse
            Console.WriteLine("try");
            using var stream = new MemoryStream();
            await image.CopyToAsync(stream);
            using var originalImage = Image.FromStream(stream);
            var resizedImage = ResizeImage(originalImage, 1000); //

            // Lagre til temp-fil
            string fileName = $"{Guid.NewGuid()}.jpg";
            string tempPath = Path.Combine(Path.GetTempPath(), fileName);
            Console.WriteLine(fileName);
            Console.WriteLine(tempPath);
            
            resizedImage.Save(tempPath, ImageFormat.Jpeg);

            // Last opp til FTP
            //string ftpPath = _ftpServer + fileName;
            //UploadToFtp(tempPath, ftpPath);

            string sftpPath = $"/{fileName}"; // Endre path etter behov
            Console.WriteLine(sftpPath);
            UploadToSftp(tempPath, sftpPath);

            Console.WriteLine("Upload forsøkt. Nå skal det saves til databasen.");
            // Lagre i database (pseudo-kode, må tilpasses din DbContext)
            SaveToDatabase(table, sftpPath);
            
            return Ok(new { filePath = sftpPath });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { error = ex.Message });
        }
    }

    private Image ResizeImage(Image image, int maxWidth)
    {
        Console.WriteLine("ResizeImage");
        int newWidth = maxWidth;
        int newHeight = (int)(image.Height * ((float)newWidth / image.Width));
        if (newHeight > maxWidth)
        {
            newHeight = maxWidth;
            newWidth = (int)(image.Width * ((float)newHeight / image.Height));
        }

        var resized = new Bitmap(newWidth, newHeight);
        using var graphics = Graphics.FromImage(resized);
        graphics.CompositingQuality = System.Drawing.Drawing2D.CompositingQuality.HighQuality;
        graphics.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBicubic;
        graphics.DrawImage(image, 0, 0, newWidth, newHeight);
        return resized;
    }

    private void UploadToSftp(string localFile, string remoteFileName)
    {
        string sftpHost = "sftp.domeneshop.no";
        int sftpPort = 22; // Standard SFTP-port
        string sftpUsername = "hopla";
        string sftpPassword = "Reell-300-hr-lam-Gjess";
        string remoteDirectory = "/upload";

        try
        {
            Console.WriteLine("🚀 Starter SFTP-opplasting...");
            Console.WriteLine($"➡️  Kobler til {sftpHost} på port {sftpPort} med brukernavn {sftpUsername}");

            using (var sftpClient = new SftpClient(sftpHost, sftpPort, sftpUsername, sftpPassword))
            {
                sftpClient.Connect();
                Console.WriteLine("✅ Tilkoblet til SFTP-serveren!");

                // Sjekk om mappen eksisterer
                if (!sftpClient.Exists(remoteDirectory))
                {
                    Console.WriteLine($"📁 Mappen '{remoteDirectory}' finnes ikke, oppretter den...");
                    sftpClient.CreateDirectory(remoteDirectory);
                    Console.WriteLine("✅ Mappe opprettet!");
                }
                else
                {
                    Console.WriteLine("📂 Mappe eksisterer allerede.");
                }

                string remoteFilePath = remoteDirectory + remoteFileName;
                Console.WriteLine($"📝 Filbane på server: {remoteFilePath}");

                // Sjekk at filen eksisterer lokalt før opplasting
                /*if (!File.Exists(localFile)) //rød strek under File
                {
                    Console.WriteLine($"❌ Lokal fil ikke funnet: {localFile}");
                    return;
                }
                */
                using (var fileStream = new FileStream(localFile, FileMode.Open))
                {
                    Console.WriteLine("📤 Laster opp filen...");
                    sftpClient.UploadFile(fileStream, remoteFilePath);
                    Console.WriteLine("✅ Fil opplastet!");
                }

                sftpClient.Disconnect();
                Console.WriteLine("🚪 Frakoblet fra SFTP-serveren.");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"🔥 FEIL: {ex.Message}");
            Console.WriteLine(ex.StackTrace);
        }
    }

/*
    private void UploadToSftp(string localFile, string remoteFileName)
    {
        string sftpHost = "sftp.domeneshop.no";
        int sftpPort = 22; // Standard SFTP-port
        string sftpUsername = "hopla";
        string sftpPassword = "Reell-300-hr-lam-Gjess";
        string remoteDirectory = "/ftptest/uploads/";

        using (var sftpClient = new SftpClient(sftpHost, sftpPort, sftpUsername, sftpPassword))
        {
            sftpClient.Connect();
        if (!sftpClient.Exists(remoteDirectory))
        {
            sftpClient.CreateDirectory(remoteDirectory);
        }
        Console.WriteLine(sftpHost);
        Console.WriteLine(remoteDirectory);
        Console.WriteLine(sftpHost);
        
        string remoteFilePath = remoteDirectory + remoteFileName;
        Console.WriteLine(remoteFilePath);

        using (var fileStream = new FileStream(localFile, FileMode.Open))
        {
            sftpClient.UploadFile(fileStream, remoteFilePath);
        }            sftpClient.Disconnect();
        }
    }
*/
    private void UploadToFtp(string localFile, string ftpPath)
    {
        using WebClient client = new WebClient { Credentials = new NetworkCredential(_ftpUsername, _ftpPassword) };
        client.UploadFile(ftpPath, WebRequestMethods.Ftp.UploadFile, localFile);
    }

    private void SaveToDatabase(string table, string filePath)
    {
        Console.WriteLine("");
        Console.WriteLine(table);
        Console.WriteLine(filePath);
        
        /*
        using var db = new YourDbContext();
        
        switch (table)
        {
            case "Users": db.Users.Add(new User { ProfileImage = filePath }); break;
            case "MyHikes": db.MyHikes.Add(new MyHike { ImageUrl = filePath }); break;
            case "Trails": db.Trails.Add(new Trail { ImageUrl = filePath }); break;
            case "Horses": db.Horses.Add(new Horse { ImageUrl = filePath }); break;
        }
        
        db.SaveChanges();
        */
    }
}
