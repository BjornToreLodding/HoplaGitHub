using Microsoft.AspNetCore.StaticAssets;

public static class DistanceCalc
{
        //Avstanden mellom longitude vil variere ut i fra hvilken Latitude man befinner seg på. LatNordpolen = 0. LatEkvator = 90. Mulig jeg har blandet sammen hva som er hva av Lat og Long.

        //Testet med: Gjøvik lat1: 60.7925, long1: 10.695 og Biri lat2: 60.95558, long2: 10.6115
        //Enkel Pythagoras      (ca. 18.6780 km)
        //Forbedret Pythagoras  (ca. 18.6796 km) +1,6meter
        //Haversine-formelen    (ca. 18.6883 km) +10,3/8,7meter
        //Avstanden fra Biri til Gjøvik gir forskjell på ca 10 meter ved bruk av simpel pytagoras og Haversine.

        //Testet med Lommedalen lat1: 59.95, long1: 10.466667 og Biri lat2: 60.95558, long2: 10.6115
        //Enkel Pytagoras       112.03499km
        //Forbedret Pytagoras   112.03460km + 4meter
        //Haversine             112.09701km + 62meter
        //Avstanden fra Lommedalen til Biri gir forskjell på ca 62meter ved bruk av simple/improved og HaverSine.
        //Forskjellen mellom simple pytagoras og Improved er minimal

    public static float SimplePytagoras(float lat1, float long1, float lat2, float long2) 
    {

        // Enkel Pythagoras (Biri-Gjøvik 18.6780 km) Regner enkelt ut
        double degreeDist = 40008 / 360.0;    // lengde mellom hver lengdegrad, Jordens.gjsn_omkrets / lengdegrader.Antall
        double sideX_simple = Math.Cos((lat1 + lat2) * Math.PI / 360) * Math.Abs(long1 - long2) * degreeDist;
        double sideY_simple = Math.Abs(lat2 - lat1) * degreeDist;
        return (float)Math.Sqrt(Math.Pow(sideX_simple, 2) + Math.Pow(sideY_simple, 2));
    }

    public static float ImprovedPytagoras(float lat1, float long1, float lat2, float long2)
    {
        // Metode 2: Forbedret Pythagoras (Biri-Gjøvik 18.6796 km) Pytagorasvariant som tar hensyn til at jorda har større omkrets rundt ekvator enn rundt polene
        //
        double latMid = (lat1 + lat2) / 2.0 * Math.PI / 180.0;
        double latDist = 111.132; //avstanden mellom hver grad rundt polene
        double longDist = 111.320 * Math.Cos(latMid); //avstanden mellom hver grad rundt ekvator. 
        double sideX_improved = longDist * Math.Abs(long1 - long2);
        double sideY_improved = latDist * Math.Abs(lat1 - lat2);
        return (float)Math.Sqrt(sideX_improved * sideX_improved + sideY_improved * sideY_improved);
    }

    public static float Haversine(float lat1, float long1, float lat2, float long2)
    {

        //Metode 3: Haversine-formelen (Biri-Gjøvik 18.6883 km) Tar hensyn til at jorda er buet. 
        //Mer resurskrevende metode, men mer nøyaktig på store avstander
        var radiusEarth = 6371.0;
        double dlat = (lat2 - lat1) * Math.PI / 180.0;
        double dlon = (long2 - long1) * Math.PI / 180.0;
        double a = Math.Sin(dlat / 2) * Math.Sin(dlat / 2) + Math.Cos(lat1 * Math.PI / 180) * Math.Cos(lat2 * Math.PI / 180) * Math.Sin(dlon / 2) * Math.Sin(dlon / 2);
        double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
        return (float)(radiusEarth * c);

    }
}