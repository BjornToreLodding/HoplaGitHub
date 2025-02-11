public static class DistanceCalc
{
    public static float CalcDistance(float lat1, float long1, float lat2, float long2) 
    {
        //Gjøvik lat1 60.7925, long1 10.695 
        //Biri Lat2 = 60.95558, Long2 = 10.6115
        var degreeDist = 40000/360; //avstanden mellom lat rundt ekvator
        //A^2 + B^2 = C^2
        //Avstanden mellom lat vil variere ut i fra hvilken long man befinner seg på. LatNordpolen = 0. LatEkvator = 90
        var sideA = Math.Cos((long1+long2)/2) * Math.Abs(lat1-lat2) * degreeDist;
        var sideB = Math.Abs(long2-long1)*degreeDist;
        return (float)Math.Sqrt(Math.Pow(sideA,2) + Math.Pow(sideB,2));
    }

}