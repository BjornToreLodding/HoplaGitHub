using HoplaBackend.DTOs;
using HoplaBackend.Models;

namespace HoplaBackend.Helpers;

public static class TrailCoordinatesTrim
{
    public static List<TrailCoordinateDto> ReduceTo50Coordinates(List<TrailCoordinateDto> coordinates)
    {
        if (coordinates.Count <= 50)
            return coordinates;

        int step = coordinates.Count / 50;
        return coordinates.Where((c, index) => index % step == 0).Take(50).ToList();
    }

    public static List<RideCoordinateDto> AdjustForTupleLimit(List<RideCoordinateDto> coordinates)
    {
        while (coordinates.Count > 3333)
        {
            for (int i = 1; i < coordinates.Count - 1; i++)
            {
                if (coordinates.Count <= 3333)
                    break;

                coordinates[i - 1].TimeSinceLast += coordinates[i].TimeSinceLast;
                coordinates.RemoveAt(i);
            }
        }
        return coordinates;
    }
}

