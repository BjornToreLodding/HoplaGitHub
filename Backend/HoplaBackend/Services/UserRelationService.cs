using HoplaBackend.Data;

namespace HoplaBackend.Services;
public class UserRelationService
{
    private readonly AppDbContext _context;

    public UserRelationService(AppDbContext context)
    {
        _context = context;
    }

    public string RelationStatus(Guid userId, Guid targetUserId)
    {
        //Se først etter om dem er venner. 
        //Lage rutiner for hva som skal overstyre hva. Hvis de følger hverandre og er venner, skal det kun vises at de er venner.
        var relation = _context.UserRelations
            .FirstOrDefault(r => 
                (r.FromUserId == userId && r.ToUserId == targetUserId) || 
                (r.FromUserId == targetUserId && r.ToUserId == userId));

        if (relation == null)
            return "NONE"; // Ingen relasjon

        return relation.Status; // Returnerer f.eks. "FRIENDS", "PENDING", "BLOCKED"
    }
}