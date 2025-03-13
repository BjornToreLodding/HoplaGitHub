using MailKit.Net.Smtp;
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
        emailMessage.From.Add(new MailboxAddress("Hopla NoReply", "noreply@hopla.no"));
        
        emailMessage.To.Add(new MailboxAddress("", to));
        emailMessage.Subject = subject;

        var bodyBuilder = new BodyBuilder { HtmlBody = htmlMessage };
        emailMessage.Body = bodyBuilder.ToMessageBody();

        using (var client = new SmtpClient())
        {
            //
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
