namespace HoplaBackend.Helpers;

// Hjelpefunksjon for å bruke LIMIt og OFFSET på en effektiv måte med C#
// Trenger kildehenvisning på denne og må lete opp igjen hvor jeg fant den.
public static class QueryExtensions
{
    public static IQueryable<T> ApplyPagination<T>(this IQueryable<T> query, int? pageNumber, int? pageSize)
    {
        if (pageNumber.HasValue && pageSize.HasValue)
        {
            return query.Skip((pageNumber.Value - 1) * pageSize.Value).Take(pageSize.Value);
        }
        return query;
    }
}

