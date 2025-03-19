using System;
using RestSharp; // RestSharp v112.1.0
using RestSharp.Authenticators;
using System.Threading;
using System.Threading.Tasks;
namespace MailGunExamples
{
  class SendSimpleMessage
  {
    public static async Task<RestResponse> Send()
    {
        var options = new RestClientOptions("https://api.mailgun.net/v3")
        {
            Authenticator = new HttpBasicAuthenticator("api", Environment.GetEnvironmentVariable("API_KEY") ?? "API_KEY")
        };

        var client = new RestClient(options);
        var request = new RestRequest("/sandboxa636ea1ffabc48db8d8ea16a9cc2c578.mailgun.org/messages", Method.Post);
        request.AlwaysMultipartFormData = true;
        request.AddParameter("from", "Mailgun Sandbox <postmaster@sandboxa636ea1ffabc48db8d8ea16a9cc2c578.mailgun.org>");
        request.AddParameter("to", "Bjorn Tore Lodding <bjorn_tore_lodding@hotmail.com>");
        request.AddParameter("subject", "Hello Bjorn Tore Lodding");
        request.AddParameter("text", "Congratulations Bjorn Tore Lodding, you just sent an email with Mailgun! You are truly awesome!");
        return await client.ExecuteAsync(request);
    }
  }
}