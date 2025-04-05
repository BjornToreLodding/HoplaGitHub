using HoplaBackend.Helpers;

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
