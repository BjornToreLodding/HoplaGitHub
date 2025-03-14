using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.IO;
using System.Threading.Tasks;
using Renci.SshNet;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.Processing;
using SixLabors.ImageSharp.Formats.Jpeg;
using DotNetEnv; // For miljøvariabler

[Route("upload")]
[ApiController]
public class UploadController : ControllerBase
{
    private readonly string _sftpHost;
    private readonly int _sftpPort;
    private readonly string _sftpUsername;
    private readonly string _sftpPassword;
    private readonly string _remoteDirectory;
    private readonly string _knownHostKey;
    
    public UploadController()
    {
        Env.Load(); // Laster .env hvis den finnes
        _sftpHost = Environment.GetEnvironmentVariable("SFTP_HOST");
        _sftpPort = int.TryParse(Environment.GetEnvironmentVariable("SFTP_PORT"), out var port) ? port : 22;
        _sftpUsername = Environment.GetEnvironmentVariable("SFTP_USER");
        _sftpPassword = Environment.GetEnvironmentVariable("SFTP_PASS");
        _remoteDirectory = Environment.GetEnvironmentVariable("SFTP_REMOTE_PATH");
        _knownHostKey = Environment.GetEnvironmentVariable("SFTP_KNOWN_HOSTS");
    }
    
    [HttpPost]
    public async Task<IActionResult> UploadImage([FromForm] IFormFile image, [FromForm] string table)
    {
        if (image == null || image.Length == 0)
            return BadRequest(new { error = "No file uploaded." });
        
        try
        {
            Console.WriteLine("🚀 Starter SFTP-opplasting...");
            Console.WriteLine($"➡️ Kobler til {_sftpHost} på port {_sftpPort} med brukernavn {_sftpUsername}");

            using var stream = new MemoryStream();
            await image.CopyToAsync(stream);
            byte[] resizedImage = ResizeImage(stream.ToArray(), 1000);
            
            string fileName = $"{Guid.NewGuid()}.jpg";
            string tempPath = Path.Combine(Path.GetTempPath(), fileName);
            await System.IO.File.WriteAllBytesAsync(tempPath, resizedImage);
            
            UploadToSftp(tempPath, fileName);
            
            SaveToDatabase(table, fileName);
            return Ok(new { filePath = _remoteDirectory + fileName });
        }
        catch (Exception ex)
        {
            Console.WriteLine($"🔥 FEIL: {ex.Message}");
            Console.WriteLine(ex.StackTrace);
            return StatusCode(500, new { error = ex.Message });
        }
    }

    private byte[] ResizeImage(byte[] imageBytes, int maxWidth)
    {
        using var image = Image.Load(imageBytes);

        int newWidth = maxWidth;
        int newHeight = (int)(image.Height * ((float)newWidth / image.Width));

        if (newHeight > maxWidth)
        {
            newHeight = maxWidth;
            newWidth = (int)(image.Width * ((float)newHeight / image.Height));
        }

        image.Mutate(x => x.Resize(newWidth, newHeight));

        using var outputStream = new MemoryStream();
        image.Save(outputStream, new JpegEncoder());
        Console.WriteLine("✅ Bildet er redusert i størrelse og lagret som JPEG.");
        return outputStream.ToArray();
    }

    private void UploadToSftp(string localFile, string remoteFileName)
    {
        try
        {
            Console.WriteLine("🚀 Starter SFTP-tilkobling...");

            using (var sftpClient = new SftpClient(_sftpHost, _sftpPort, _sftpUsername, _sftpPassword))
            {
                /*
                // Sjekk om en kjent SSH-nøkkel er satt
                if (!string.IsNullOrEmpty(_knownHostKey))
                {
                    sftpClient.HostKeyReceived += (sender, e) =>
                    {
                        string receivedKey = Convert.ToBase64String(e.HostKey);
                        if (!_knownHostKey.Contains(receivedKey))
                        {
                            Console.WriteLine("❌ Feil SSH-nøkkel! Tilkobling avvist.");
                            e.CanTrust = false;
                            return;
                        }
                        Console.WriteLine("✅ SSH-nøkkel verifisert. Tilkobling godkjent.");
                        e.CanTrust = true;
                    };
                }
                else
                {
                    */
                    // Hvis ingen kjent nøkkel er satt, godta serverens nøkkel automatisk
                    Console.WriteLine("⚠️ Ingen SSH-nøkkel sjekkes (lokal utvikling).");
                    sftpClient.HostKeyReceived += (sender, e) =>
                    {
                        // Aksepterer serverens nøkkel automatisk
                        Console.WriteLine("✅ Serverens fingeravtrykk godkjent.");
                        e.CanTrust = true;
                    };
                //}

                sftpClient.Connect();
                Console.WriteLine("✅ Tilkoblet til SFTP-serveren!");

                // Sjekk om mappen finnes, og opprett den om nødvendig
                if (!sftpClient.Exists(_remoteDirectory))
                {
                    Console.WriteLine($"📁 Mappen '{_remoteDirectory}' finnes ikke, oppretter den...");
                    sftpClient.CreateDirectory(_remoteDirectory);
                    Console.WriteLine("✅ Mappe opprettet!");
                }
                else
                {
                    Console.WriteLine("📂 Mappe eksisterer allerede.");
                }

                // Filbane på serveren
                string remoteFilePath = _remoteDirectory + remoteFileName;
                Console.WriteLine($"📝 Filbane på server: {remoteFilePath}");

                // Sjekk at den lokale filen finnes
                if (!System.IO.File.Exists(localFile))
                {
                    Console.WriteLine($"❌ Lokal fil ikke funnet: {localFile}");
                    return;
                }

                // Laste opp filen
                using (var fileStream = new FileStream(localFile, FileMode.Open))
                {
                    Console.WriteLine("📤 Laster opp filen...");
                    sftpClient.UploadFile(fileStream, remoteFilePath);
                    Console.WriteLine("✅ Fil opplastet!");
                }

                // Frakobling etter opplasting
                sftpClient.Disconnect();
                Console.WriteLine("🚪 Frakoblet fra SFTP-serveren.");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"🔥 FEIL under SFTP-opplasting: {ex.Message}");
            Console.WriteLine(ex.StackTrace);
        }
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

/*using DotNetEnv;
using Renci.SshNet;
using Renci.SshNet.Sftp;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.Processing;
using SixLabors.ImageSharp.Formats.Jpeg;
using System.IO;
using System.Net;
using System.Threading.Tasks;


[Route("upload")]
[ApiController]
public class UploadController : ControllerBase
{
    
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
            using var originalImage = Image.FromStream(stream); //rød strek under FromStream
            var resizedImage = ResizeImage(originalImage, 1000); //

            // Lagre til temp-fil
            string fileName = $"{Guid.NewGuid()}.jpg";
            string tempPath = Path.Combine(Path.GetTempPath(), fileName);
            Console.WriteLine(fileName);
            Console.WriteLine(tempPath);
            
            resizedImage.Save(tempPath, ImageFormat.Jpeg); //Rød Strek under ImageFormat

            // Last opp til FTP
            //string ftpPath = _ftpServer + fileName;
            //UploadToFtp(tempPath, ftpPath);

            string sftpPath = $"{fileName}"; // Endre path etter behov
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

        var resized = new Bitmap(newWidth, newHeight); //Rød Strek under Bitmap
        using var graphics = Graphics.FromImage(resized); //Rød Strek under Graphics
        graphics.CompositingQuality = System.Drawing.Drawing2D.CompositingQuality.HighQuality;
        graphics.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBicubic;
        graphics.DrawImage(image, 0, 0, newWidth, newHeight);
        return resized;
    }

    private void UploadToSftp(string localFile, string remoteFileName)
    {
        // Hent miljøvariabler (fra Render eller lokalt)
        Env.Load(); // Laster .env hvis den finnes (kun lokalt)
        
        string sftpHost = Environment.GetEnvironmentVariable("SFTP_HOST");
        int sftpPort = int.TryParse(Environment.GetEnvironmentVariable("SFTP_PORT"), out var port) ? port : 22;
        string sftpUsername = Environment.GetEnvironmentVariable("SFTP_USER");
        string sftpPassword = Environment.GetEnvironmentVariable("SFTP_PASS");
        string remoteDirectory = Environment.GetEnvironmentVariable("SFTP_REMOTE_PATH");
        string knownHostKey = Environment.GetEnvironmentVariable("SFTP_KNOWN_HOSTS"); // Tom lokalt

        try
        {
            Console.WriteLine("🚀 Starter SFTP-opplasting...");
            Console.WriteLine($"➡️ Kobler til {sftpHost} på port {sftpPort} med brukernavn {sftpUsername}");

            using (var sftpClient = new SftpClient(sftpHost, sftpPort, sftpUsername, sftpPassword))
            {
                // Kun sjekk SSH-nøkkel hvis den er satt (Render.com)
                if (!string.IsNullOrEmpty(knownHostKey))
                {
                    sftpClient.HostKeyReceived += (sender, e) =>
                    {
                        string receivedKey = Convert.ToBase64String(e.HostKey);
                        
                        if (!knownHostKey.Contains(receivedKey))
                        {
                            Console.WriteLine("❌ Feil SSH-nøkkel! Tilkobling avvist.");
                            e.CanTrust = false;
                            return;
                        }

                        Console.WriteLine("✅ SSH-nøkkel verifisert. Tilkobling godkjent.");
                        e.CanTrust = true;
                    };
                }
                else
                {
                    Console.WriteLine("⚠️ Ingen SSH-nøkkel sjekkes (lokal utvikling).");
                }

                sftpClient.Connect();
                Console.WriteLine("✅ Tilkoblet til SFTP-serveren!");

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
                //if (!File.Exists(localFile)) // Rød strek under File erstattet med linje under pga konflikt med ASP.NET Core Controllerbase.
                if (!System.IO.File.Exists(localFile)) 
                {
                    Console.WriteLine($"❌ Lokal fil ikke funnet: {localFile}");
                    return;
                }

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
        
    }
}
*/