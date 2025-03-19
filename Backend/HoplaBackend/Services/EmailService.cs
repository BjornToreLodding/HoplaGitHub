using System;
using System.Net;
using System.Net.Mail;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;

public class EmailService
{
    private readonly IConfiguration _configuration;
    
    public EmailService(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public async Task SendEmailAsync(string to, string subject, string htmlMessage)
    {
        Console.WriteLine($"SMTP Server: {_configuration["EmailSettings:SmtpServer"]}");
        Console.WriteLine($"SMTP Username: {_configuration["EmailSettings:SmtpUsername"]}");
        Console.WriteLine($"SMTP Password: {(string.IsNullOrEmpty(_configuration["EmailSettings:SmtpPassword"]) ? "IKKE SATT!" : "SATT ✔")}");
        var smtpServer = _configuration["EmailSettings:SmtpServer"];
        var smtpUsername = _configuration["EmailSettings:SmtpUsername"];
        var smtpPassword = _configuration["EmailSettings:SmtpPassword"];

        if (string.IsNullOrEmpty(smtpServer) || string.IsNullOrEmpty(smtpUsername) || string.IsNullOrEmpty(smtpPassword))
        {
            throw new Exception("❌ Miljøvariabler for SMTP mangler!");
        }

        using (var smtp = new SmtpClient(smtpServer, 587))
        {
            smtp.Credentials = new NetworkCredential(smtpUsername, smtpPassword);
            smtp.EnableSsl = true;
            
            TimeZoneInfo norwayTimeZone = TimeZoneInfo.FindSystemTimeZoneById("Central European Standard Time");
            DateTime norwegianTime = TimeZoneInfo.ConvertTimeFromUtc(DateTime.UtcNow, norwayTimeZone);
            string timestamp = norwegianTime.ToString("HH:mm:ss dd-MM-yyyy");

            // 📨 Legg til tidsstempel med linjeskift
            string footer = $"<br><br><hr><p>Denne e-posten ble sendt: {timestamp} (Norsk tid).  Denne eposten kan ikke besvares.</p>";

            var mailMessage = new MailMessage
            {
                From = new MailAddress("postmaster@hopla.no", "Hopla NoReply"),
                Subject = subject,
                Body = htmlMessage + footer,
                IsBodyHtml = true
            };
            mailMessage.To.Add(to);

            await smtp.SendMailAsync(mailMessage);
        }
    }
}

//Gammel epost-tjeneste, men den er ment for å sende store vedlegg, så den fyller opp minnet svært fort.
/*using MailKit.Net.Smtp;
using MimeKit;
using System.Threading.Tasks;

public class EmailService
{
    private readonly IConfiguration _configuration;
    public EmailService(IConfiguration configuration)
    {
        _configuration = configuration;
    }
    public async Task SendEmailAsync(string to, string subject, string htmlMessage)
    {
        var emailMessage = new MimeMessage();
        
        //noreply@hopla.no er bestilt!
        emailMessage.From.Add(new MailboxAddress("Hopla NoReply", "postmaster@hopla.no"));
        
        emailMessage.To.Add(new MailboxAddress("", to));
        emailMessage.Subject = subject;

        var bodyBuilder = new BodyBuilder { HtmlBody = htmlMessage };
        emailMessage.Body = bodyBuilder.ToMessageBody();
        
        using (var client = new SmtpClient())
        {
         
            Console.WriteLine($"Connecting to {_configuration["EmailSettings:SmtpServer"]}");
            await client.ConnectAsync("smtp.eu.mailgun.org", 587, MailKit.Security.SecureSocketOptions.StartTls);
            Console.WriteLine("Authenticating...");
            await client.AuthenticateAsync("postmaster@hopla.no", "61fd6fe64521e2430ac55b63db0e24c7-3d4b3a2a-9290d169");
            Console.WriteLine("Sending email...");
            await client.SendAsync(emailMessage);
            await client.DisconnectAsync(true);
               
            //await client.ConnectAsync("smtp.domeneshop.no", 587, MailKit.Security.SecureSocketOptions.StartTls);
            Console.WriteLine($"Connecting to {_configuration["EmailSettings:SmtpServer"]}");
            await client.ConnectAsync("email-smtp.eu-west-1.amazonaws.com", 587, MailKit.Security.SecureSocketOptions.StartTls);
            Console.WriteLine("Authenticating...");
            await client.AuthenticateAsync("AKIAWIJIUWJKZ223VLF3", "UYBpoD5Ef8vpUl5qpgv1AjqYvchzKWJlp5FVClsf");
            Console.WriteLine("Sending email...");
            await client.SendAsync(emailMessage);
            await client.DisconnectAsync(true);
            
        }
        

    }
}
*/