using System.Linq.Expressions;
using HoplaBackend.Models;

namespace HoplaBackend.Helpers;

public static class FilterExtensions
{
    public static IQueryable<Trail> ApplyDynamicFilter(this IQueryable<Trail> query, string fieldName, object value)
    {
        var parameter = Expression.Parameter(typeof(Trail), "t");
        var property = Expression.Property(parameter, "TrailFilters"); // Henter TrailFilters-objektet
        var field = Expression.Property(property, fieldName); // Henter spesifikt felt

        // Konverter verdien til riktig type
        var fieldType = field.Type;
        var convertedValue = Convert.ChangeType(value, fieldType);
        var constant = Expression.Constant(convertedValue, fieldType);

        // Lag en sammenligning: t.TrailFilters.{fieldName} == {value}
        var comparison = Expression.Equal(field, constant);
        var lambda = Expression.Lambda<Func<Trail, bool>>(comparison, parameter);

        return query.Where(lambda);
    }
}
