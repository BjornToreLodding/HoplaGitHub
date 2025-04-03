using System;
using System.Collections.Generic;
using System.Linq;

namespace HoplaBackend.Helpers // Bytt ut med riktig namespace
{
    public class FilterQuery
    {
        public Guid FilterId { get; set; }
        public List<string> Values { get; set; } = new();
    }

    public static class TrailFilterHelper
    {
        public static List<FilterQuery> ParseFilterQuery(string? filterQuery)
        {
            var result = new List<FilterQuery>();
            if (string.IsNullOrWhiteSpace(filterQuery))
                return result;

            var filters = filterQuery.Split(';', StringSplitOptions.RemoveEmptyEntries);

            foreach (var filter in filters)
            {
                var parts = filter.Split(':', StringSplitOptions.RemoveEmptyEntries);
                if (parts.Length != 2)
                    continue;

                if (!Guid.TryParse(parts[0], out Guid filterId))
                    continue;

                var values = parts[1]
                    .Split(',', StringSplitOptions.RemoveEmptyEntries)
                    .Select(v => v.Trim())
                    .ToList();

                if (values.Count > 0)
                {
                    result.Add(new FilterQuery
                    {
                        FilterId = filterId,
                        Values = values
                    });
                }
            }

            return result;
        }
    }
}
