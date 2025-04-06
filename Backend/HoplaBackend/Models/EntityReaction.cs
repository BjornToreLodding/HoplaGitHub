using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models
{
    public class EntityReaction
    {
        [Key]
        public Guid Id { get; set; }

        [ForeignKey("User")]
        public Guid UserId { get; set; }

        public Guid EntityId { get; set; }

        public string EntityName { get; set; } = "EntityFeed";

        public ReactionType Reaction { get; set; } = ReactionType.Like; // F.eks. "Like", "Dislike", "Love"

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // 🚀 Navigasjonsfelt for å hente brukerens Alias
        public virtual User User { get; set; }
    }
    public enum ReactionType
    {
        Like,   // 👍
        Love,   // ❤️
        Haha,   // 😂
        Wow,    // 😮
        Sad,    // 😢
        Angry   // 😡
        // lete opp HesteEmoji
    }
}
