// File: Helpers/ImageUploadHelper.cs
using System;
using System.IO;
using Renci.SshNet;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.Processing;
using SixLabors.ImageSharp.Formats.Jpeg;

namespace HoplaBackend.Helpers;

public static class ImageUploadHelper
{
    public static byte[] ResizeImage(byte[] imageBytes, int maxWidth)
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
        return outputStream.ToArray();
    }

    public static void UploadToSftp(string host, int port, string username, string password, string localFile, string remoteFileName, string remoteDirectory)
    {
        using var sftpClient = new SftpClient(host, port, username, password);

        sftpClient.HostKeyReceived += (sender, e) =>
        {
            Console.WriteLine("✅ Serverens fingeravtrykk godkjent.");
            e.CanTrust = true;
        };

        sftpClient.Connect();

        if (!sftpClient.Exists(remoteDirectory))
        {
            sftpClient.CreateDirectory(remoteDirectory);
        }

        string remoteFilePath = remoteDirectory + remoteFileName;

        using var fileStream = new FileStream(localFile, FileMode.Open);
        sftpClient.UploadFile(fileStream, remoteFilePath);

        sftpClient.Disconnect();
    }
}
