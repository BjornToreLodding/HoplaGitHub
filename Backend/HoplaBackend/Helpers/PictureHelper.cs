namespace HoplaBackend.Helpers;

/// <summary>
/// Helper class for building picture URLs based on system settings.
/// Automatically applies width, height, fit, and fallback images.
/// </summary>
public static class PictureHelper
{
    /// <summary>
    /// Builds a full picture URL based on the context settings and the given picture path.
    /// Returns a fallback image if no picture is provided.
    /// </summary>
    /// <param name="pictureUrl">The original picture URL or filename.</param>
    /// <param name="context">The context name to load size and fallback settings.</param>
    /// <returns>Full image URL with resizing and fallback applied.</returns>
    public static string BuildPictureUrl(string? pictureUrl, string context)
    {
        var (width, height, fit, fallback) = SystemSettingsCache.GetPictureSettings(context);
        var baseUrl = SystemSettingsCache.GetPictureBaseUrl();

        // If no picture is provided, use the fallback
        if (string.IsNullOrEmpty(pictureUrl))
        {
            Console.WriteLine("No picture provided for context: " + context);
            pictureUrl = fallback;
        }

        // If pictureUrl is a full HTTP link, use it directly
        if (pictureUrl.StartsWith("http", StringComparison.OrdinalIgnoreCase))
        {
            return $"{pictureUrl}?w={width}&h={height}&fit={fit}";
        }

        // Otherwise, build the full URL using the baseUrl
        return $"{baseUrl}{pictureUrl}?w={width}&h={height}&fit={fit}";
    }
}

/*
namespace HoplaBackend.Helpers
{
    public static class PictureHelper
    {
        private const string BaseUrl = "https://hopla.imgix.net/";

        public static string BuildPictureUrl(string? pictureUrl, string context)
        {
            var (width, height, fit) = SystemSettingsCache.GetPictureSettings(context);
            if (string.IsNullOrEmpty(pictureUrl))
            {
                Console.WriteLine("Skal ikke gå ann å komme hit" + context + " - ");
                return $"{BaseUrl}{pictureUrl}?w={width}&h={height}&fit={fit}";
            }
            else if (pictureUrl.StartsWith("http", StringComparison.OrdinalIgnoreCase))
                return $"{pictureUrl}?w={width}&h={height}&fit={fit}";

            //var (width, height, fit) = SystemSettingsCache.GetPictureSettings(context);
            else 
            {
                return $"{BaseUrl}{pictureUrl}?w={width}&h={height}&fit={fit}";
            }
        }

    }
}
*/