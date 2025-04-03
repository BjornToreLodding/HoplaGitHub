using HoplaBackend.Helpers;
using HoplaBackend.Models;

namespace HoplaBackend.Services;
public interface ITrailFilterService
{
    IQueryable<Trail> ApplyDynamicFilters(IQueryable<Trail> query, List<FilterQuery> filters);
}

public class TrailFilterService : ITrailFilterService
{
    public IQueryable<Trail> ApplyDynamicFilters(IQueryable<Trail> query, List<FilterQuery> filters)
    {
        foreach (var filter in filters)
        {
            var filterId = filter.FilterId;
            var values = filter.Values;

            query = query.Where(t =>
                t.TrailFilterValues.Any(v =>
                    v.TrailFilterDefinitionId == filterId &&
                    values.Contains(v.Value)
                )
            );
        }

        return query;
    }
}
