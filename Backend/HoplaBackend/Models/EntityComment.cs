using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models
{
    public class EntityComment
    {
        [Key]
        public Guid Id { get; set; }

        [ForeignKey("User")]
        public Guid UserId { get; set; }

        public Guid EntityId { get; set; }  // Kan være en hest, et bilde, eller en annen entitet

        public string EntityName { get; set; }  // "Horse", "Image", osv.

        public string Comment { get; set; }
        public int LikesCount { get ; set ; } = 0 ;
        public int CommentsCount { get ; set ; } = 0;

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // 🚀 Støtte for å svare på en kommentar
        public Guid? ParentCommentId { get; set; }  // NULL hvis det er en hovedkommentar

        [ForeignKey("ParentCommentId")]
        public virtual EntityComment? ParentComment { get; set; }  // Referanse til overordnet kommentar

        public virtual User User { get; set; }
    }
}
