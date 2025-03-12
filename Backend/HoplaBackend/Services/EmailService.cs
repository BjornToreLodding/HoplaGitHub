using MailKit.Net.Smtp;
using MimeKit;
using System.Threading.Tasks;

public class EmailService
{
    public async Task SendEmailAsync(string to, string subject, string htmlMessage)
    {
        var emailMessage = new MimeMessage();
        
        //noreply@hopla.no er bestilt!
        emailMessage.From.Add(new MailboxAddress("Hopla NoReply", "noreply@hopla.no"));
        
        emailMessage.To.Add(new MailboxAddress("", to));
        emailMessage.Subject = subject;

        var bodyBuilder = new BodyBuilder { HtmlBody = htmlMessage };
        emailMessage.Body = bodyBuilder.ToMessageBody();

        using (var client = new SmtpClient())
        {
            await client.ConnectAsync("smtp.domeneshop.no", 587, MailKit.Security.SecureSocketOptions.StartTls);
            await client.AuthenticateAsync("noreply@hopla.no", "Calibra2006!");
            await client.SendAsync(emailMessage);
            await client.DisconnectAsync(true);
        }
    }
}
