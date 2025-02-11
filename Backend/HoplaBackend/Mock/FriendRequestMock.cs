using System.ComponentModel.DataAnnotations;

using MyApp.Models;

public static class FriendRequestMock 
{
    public static List<FriendRequest> GetFriendRequests(List<User> existingUsers){
        return [
            new FriendRequest {
                FromUserId = 1, ToUserId = 2, Status = "Accepted" //, CreatedAt = 
            },
            new FriendRequest { FromUserId = 1, ToUserId = 3, Status = "Accepted" }, //, CreatedAt = 
            new FriendRequest { FromUserId = 1, ToUserId = 4, Status = "Accepted" }, //, CreatedAt = 
            new FriendRequest { FromUserId = 1, ToUserId = 5, Status = "Accepted" },
            new FriendRequest { FromUserId = 1, ToUserId = 6, Status = "Accepted" },
            new FriendRequest { FromUserId = 2, ToUserId = 3, Status = "Accepted" },
            new FriendRequest { FromUserId = 2, ToUserId = 4, Status = "Pending" },
            new FriendRequest { FromUserId = 2, ToUserId = 5, Status = "Accepted" },
            new FriendRequest { FromUserId = 2, ToUserId = 6, Status = "Accepted" },
            new FriendRequest { FromUserId = 3, ToUserId = 4, Status = "Accepted" },
            new FriendRequest { FromUserId = 3, ToUserId = 5, Status = "Accepted" },
            new FriendRequest { FromUserId = 3, ToUserId = 6, Status = "Accepted" },
            new FriendRequest { FromUserId = 4, ToUserId = 5, Status = "Accepted" },
            new FriendRequest { FromUserId = 4, ToUserId = 6, Status = "Accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 6, Status = "Accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 16, Status = "Accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 17, Status = "Accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 18, Status = "Accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 19, Status = "Accepted" },
            new FriendRequest { FromUserId = 5, ToUserId = 20, Status = "Accepted" },
            new FriendRequest { FromUserId = 7, ToUserId = 8, Status = "Accepted" },
            new FriendRequest { FromUserId = 7, ToUserId = 9, Status = "Accepted" },
            new FriendRequest { FromUserId = 7, ToUserId = 10, Status = "Accepted" },
            new FriendRequest { FromUserId = 7, ToUserId = 11, Status = "Accepted" },
            new FriendRequest { FromUserId = 7, ToUserId = 12, Status = "Accepted" },
            new FriendRequest { FromUserId = 32, ToUserId = 2, Status = "Pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 3, Status = "Declined" },
            new FriendRequest { FromUserId = 32, ToUserId = 4, Status = "Declined" },
            new FriendRequest { FromUserId = 32, ToUserId = 5, Status = "Pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 6, Status = "Pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 1, Status = "Accepted" },
            new FriendRequest { FromUserId = 32, ToUserId = 10, Status = "Pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 25, Status = "Pending" },
            new FriendRequest { FromUserId = 32, ToUserId = 38, Status = "Accepted" },
        ];
    }
}