using System.ComponentModel.DataAnnotations;

using MyApp.Models;

public static class FriendRequestMock 
{
    public static List<FriendRequest> CreateFriendRequestsMock(List<User> existingUsers){
        return [
            new FriendRequest {
                FromUserId = 1, ToUserId = 2, Status = "accepted" //, CreatedAt = 
            },
            new FriendRequest { FromUserId = 1, ToUserId = 3, Status = "accepted" }, //, CreatedAt = 
            new FriendRequest { FromUserId = 1, ToUserId = 4, Status = "accepted" }, //, CreatedAt = 
            new FriendRequest { FromUserId = 1, ToUserId = 5, Status = "accepted" },
            new FriendRequest { FromUserId = 1, ToUserId = 6, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 7, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 8, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 9, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 10, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 11, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 12, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 13, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 14, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 15, Status = "pending" },
            new FriendRequest { FromUserId = 1, ToUserId = 30, Status = "deleted" },  
            new FriendRequest { FromUserId = 1, ToUserId = 31, Status = "deleted" },  
            new FriendRequest { FromUserId = 1, ToUserId = 32, Status = "deleted" },
            new FriendRequest { FromUserId = 1, ToUserId = 33, Status = "deleted" },  
            new FriendRequest { FromUserId = 1, ToUserId = 34, Status = "deleted" },  
            new FriendRequest { FromUserId = 1, ToUserId = 35, Status = "deleted" },
            new FriendRequest { FromUserId = 1, ToUserId = 36, Status = "deleted" },
            new FriendRequest { FromUserId = 1, ToUserId = 37, Status = "deleted" },
            new FriendRequest { FromUserId = 1, ToUserId = 38, Status = "deleted" },
            new FriendRequest { FromUserId = 1, ToUserId = 39, Status = "deleted" },
            new FriendRequest { FromUserId = 2, ToUserId = 3, Status = "accepted" },
            new FriendRequest { FromUserId = 2, ToUserId = 4, Status = "pending" },
            new FriendRequest { FromUserId = 2, ToUserId = 5, Status = "pending" },
            new FriendRequest { FromUserId = 2, ToUserId = 6, Status = "pending" },
            new FriendRequest { FromUserId = 3, ToUserId = 4, Status = "accepted" },
            new FriendRequest { FromUserId = 3, ToUserId = 5, Status = "accepted" },
            new FriendRequest { FromUserId = 3, ToUserId = 6, Status = "accepted" },
            new FriendRequest { FromUserId = 4, ToUserId = 5, Status = "accepted" },
            new FriendRequest { FromUserId = 4, ToUserId = 6, Status = "accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 6, Status = "accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 16, Status = "accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 17, Status = "accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 18, Status = "accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 19, Status = "accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 20, Status = "accepted" },
            new FriendRequest { FromUserId = 7, ToUserId = 8, Status = "pending" },
            new FriendRequest { FromUserId = 7, ToUserId = 9, Status = "pending" },
            new FriendRequest { FromUserId = 7, ToUserId = 10, Status = "pending" },
            new FriendRequest { FromUserId = 7, ToUserId = 11, Status = "pending" },
            new FriendRequest { FromUserId = 7, ToUserId = 12, Status = "pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 2, Status = "pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 3, Status = "declined" },
            new FriendRequest { FromUserId = 32, ToUserId = 4, Status = "declined" },
            new FriendRequest { FromUserId = 32, ToUserId = 5, Status = "pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 6, Status = "pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 1, Status = "accepted" },
            new FriendRequest { FromUserId = 32, ToUserId = 10, Status = "pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 25, Status = "pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 38, Status = "accepted" },
            new FriendRequest { FromUserId = 21, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 22, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 23, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 24, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 25, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 26, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 27, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 28, ToUserId = 1, Status = "pending" },
            new FriendRequest { FromUserId = 29, ToUserId = 1, Status = "pending" },

        ];
    }
}