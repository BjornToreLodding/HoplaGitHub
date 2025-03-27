using System.Net.Http;
using System.Net.Http.Headers;
using Serilog.Sinks.Http;

public class LogtailHttpClient : IHttpClient
{
    private readonly HttpClient _client;

    public LogtailHttpClient(string token)
    {
        _client = new HttpClient();
        _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);
        _client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
    }

    public void Dispose() => _client.Dispose();

    public async Task<HttpResponseMessage> PostAsync(string requestUri, HttpContent content)
    {
        return await _client.PostAsync(requestUri, content);
    }
}
