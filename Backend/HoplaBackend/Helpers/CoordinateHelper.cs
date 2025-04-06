using System.Globalization;

public static class CoordinateHelper
{
    public static List<(double Lat, double Lng)> ParseLatLngOnly(string coordinatesCsv)
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
