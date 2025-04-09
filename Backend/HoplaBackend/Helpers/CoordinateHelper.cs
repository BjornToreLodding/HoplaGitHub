using System.Globalization;

public static class CoordinateHelper
{
    public static (double LatMin, double LatMax, double LongMin, double LongMax, double LatMean, double LongMean) 
    CalculateCoordinateStats(List<(double Lat, double Lng)> coordinates)
    {
        var latitudes = coordinates.Select(c => c.Lat);
        var longitudes = coordinates.Select(c => c.Lng);

        double latMin = latitudes.Min();
        double latMax = latitudes.Max();
        double longMin = longitudes.Min();
        double longMax = longitudes.Max();
        double latMean = latitudes.Average();
        double longMean = longitudes.Average();

        return (latMin, latMax, longMin, longMax, latMean, longMean);
    }

    public static List<(double Lat, double Lng)> ParseLatLngOnly(string coordinatesCsv)
    {
        var coordinates = new List<(double Lat, double Lng)>();

        var parts = coordinatesCsv.Split(';', StringSplitOptions.RemoveEmptyEntries);

        foreach (var part in parts)
        {
            var split = part.Split(',');

            if (split.Length >= 3)
            {
                bool latOk = double.TryParse(split[1], NumberStyles.Float, CultureInfo.InvariantCulture, out double lat);
                bool lngOk = double.TryParse(split[2], NumberStyles.Float, CultureInfo.InvariantCulture, out double lng);

                if (latOk && lngOk)
                {
                    coordinates.Add((lat, lng));
                }
                else
                {
                    Console.WriteLine($"Ugyldig tallformat i koordinat: {part}");
                    // Her kunne du også valgt å logge eller samle opp feilede punkter hvis ønskelig
                }
            }
            else
            {
                Console.WriteLine($"Ugyldig format på koordinat: {part}");
            }
        }

        return coordinates;
    }
    //Gammel metode som er erstattet med tryggere variant.
    /*   public static List<(double Lat, double Lng)> ParseLatLngOnly(string coordinatesCsv)
        {
            return coordinatesCsv
                .Split(';', StringSplitOptions.RemoveEmptyEntries)
                .Select(part =>
                {
                    var split = part.Split(',');
                    return (
                        Lat: double.Parse(split[1], CultureInfo.InvariantCulture),
                        Lng: double.Parse(split[2], CultureInfo.InvariantCulture)
                    );
                }).ToList();
        }
    */
    public static List<(double Lat, double Lng)> DownsampleCoordinates(List<(double Lat, double Lng)> coordinates, int maxCount)
    {
        if (coordinates.Count <= maxCount)
            return coordinates;

        double step = (double)coordinates.Count / maxCount;
        var result = new List<(double Lat, double Lng)>();

        for (int i = 0; i < maxCount; i++)
        {
            int index = (int)Math.Round(i * step);
            if (index >= coordinates.Count)
                index = coordinates.Count - 1;

            result.Add(coordinates[index]);
        }

        return result;
    }

    public static string ToCsv(List<(double Lat, double Lng)> coordinates)
    {
        return string.Join(";", coordinates.Select(c => $"{c.Lat},{c.Lng}"));
    }
}
