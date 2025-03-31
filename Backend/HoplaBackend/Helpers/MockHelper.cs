using System;
using System.Collections.Generic;
using System.Linq;
using HoplaBackend.Migrations;
using HoplaBackend.Models; // Husk riktig namespace

namespace HoplaBackend.Helpers;
public static class MockHelper
{
    public static List<(double Lat, double Lng)> GenerateCircularTrail(double latCenter, double longCenter, double distanceKm, int points = 500)
    {
        var coordinates = new List<(double Lat, double Lng)>();

        double radiusKm = distanceKm / (2 * Math.PI);
        double degPerKmLat = 1.0 / 111.32;
        double degPerKmLong = 1.0 / (111.32 * Math.Cos(latCenter * Math.PI / 180));

        for (int i = 0; i < points; i++)
        {
            double angle = 2 * Math.PI * i / points;
            double dx = Math.Cos(angle) * radiusKm;
            double dy = Math.Sin(angle) * radiusKm;

            double lat = latCenter + dy * degPerKmLat;
            double lng = longCenter + dx * degPerKmLong;

            coordinates.Add((lat, lng));
        }

        return coordinates;
    }
}


/*
public static class MockHelper
{
    public static List<TrailCoordinates50> GenerateCircularTrail(double latCenter, double longCenter, double distanceKm, int points = 500)
    {
        var coordinates = new List<TrailCoordinates50>();

        double radiusKm = distanceKm / (2 * Math.PI);
        double degPerKmLat = 1.0 / 111.32;
        double degPerKmLong = 1.0 / (111.32 * Math.Cos(latCenter * Math.PI / 180));

        for (int i = 0; i < points; i++)
        {
            double angle = 2 * Math.PI * i / points;
            double dx = Math.Cos(angle) * radiusKm;
            double dy = Math.Sin(angle) * radiusKm;

            double lat = latCenter + dy * degPerKmLat;
            double lon = longCenter + dx * degPerKmLong;

            coordinates.Add(new TrailCoordinates50 { Lat = lat, Long = lon });
        }

        return coordinates;
    }
}
*/