using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace HoplaBackend.Models;

//[Table("users")] // Forteller EF Core at denne modellen er koblet til `users`-tabellen i PostgreSQL
public class UserSetting
{
    
    [Key, ForeignKey("User")]
    public Guid Id { get; set; } 
    //public guid Id { get; set; } //Bruker kanskje denne senere
    
    public bool DarkMode { get; set; } = false;
    public string Language { get; set; } = "NOR";

    // privacy settings
    public bool HideFeedFriendNewFriends { get; set; } = false;
    public bool HideFeedHorse { get; set; } = false;
    public bool HideFeedFriendHikes{ get; set; } = false;
    public bool HideReactionFriendNewFriends{ get; set; } = false;
    public bool HideCommentFriendNewFriends{ get; set; } = false;

}