using HoplaBackend.Models;

namespace HoplaBackend.Helpers;
public static class EntityTypeMapper
{
    public static EntityType? MapStringToEntityType(string type)
    {
        return type.ToLower() switch
        {
            "trails" => EntityType.Trail,
            "userhikes" => EntityType.UserHike,
            "trailreviews" => EntityType.TrailReview,
            "trailratings" => EntityType.TrailRating,
            "horses" => EntityType.Horse,
            "stables" => EntityType.Stable,
            "stablemessages" => EntityType.StableMessage,
            _ => null
        };
    }
}
