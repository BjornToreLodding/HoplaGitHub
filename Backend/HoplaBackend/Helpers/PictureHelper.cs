using HoplaBackend.Helpers;

namespace HoplaBackend.Helpers
{
    public static class PictureHelper
    {
        private const string BaseUrl = "https://hopla.imgix.net/";

        public static string BuildPictureUrl(string? pictureUrl, string context)
        {
            if (string.IsNullOrEmpty(pictureUrl))
                return "";

            if (pictureUrl.StartsWith("http", StringComparison.OrdinalIgnoreCase))
                return pictureUrl;

            var (width, height, fit) = SystemSettingsCache.GetPictureSettings(context);

            return $"{BaseUrl}{pictureUrl}?w={width}&h={height}&fit={fit}";
        }

    }
}
