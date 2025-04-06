using System.Text.Json;
using HoplaBackend.DTOs;
using HoplaBackend.Helpers;
using HoplaBackend.Models; // eller hvor Trail, TrailFilterDefinition, TrailFilterValue ligger

namespace HoplaBackend.Services
{
    public class TrailListItemBuilder
    {
        public List<TrailListItemDto> Build(
            List<Trail> trails,
            List<Guid> favoriteTrailIds,
            List<TrailFilterDefinition> definitions,
            List<TrailFilterValue> values,
            Dictionary<Guid, double>? distances = null)
        {
            return trails.Select(trail =>
            {
                var trailFilterValues = values.Where(v => v.TrailId == trail.Id).ToList();

               var filters = definitions.Select(def =>
                {
                    var val = trailFilterValues.FirstOrDefault(v => v.TrailFilterDefinitionId == def.Id);
                    if (val == null || string.IsNullOrWhiteSpace(val.Value))
                        return null;

                    return (object)new
                    {
                        def.Id,
                        def.Name,
                        def.DisplayName,
                        Type = def.Type.ToString(),
                        Options = string.IsNullOrEmpty(def.OptionsJson)
                            ? new List<string>()
                            : JsonSerializer.Deserialize<List<string>>(def.OptionsJson!),
                        Value = val.Value,
                        DefaultValue = def.DefaultValue
                    };
                })
                .Where(r => r != null)
                .ToList();


                return new TrailListItemDto
                {
                    Id = trail.Id,
                    Name = trail.Name,
                    PictureUrl = PictureHelper.BuildPictureUrl(trail.PictureUrl, "TrailPicture"),
                    //PictureUrl = trail.PictureUrl + "?h=140&fit=crop",
                    AverageRating = trail.AverageRating ?? 0,
                    IsFavorite = favoriteTrailIds.Contains(trail.Id),
                    Distance = distances != null && distances.ContainsKey(trail.Id) ? distances[trail.Id] : (double?)null,
                    Filters = filters
                };
            }).ToList();
        }
    }
}
