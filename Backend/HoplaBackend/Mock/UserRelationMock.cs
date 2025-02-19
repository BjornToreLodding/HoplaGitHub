using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

using HoplaBackend.Models;

public static class UserRelationMock 
{
    public static List<UserRelation> CreateUserRelationMock(List<User> existingUsers){
        return new List<UserRelation> 
        {
            new UserRelation {
                FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), Status = "accepted", //, CreatedAt = 
            }
            /*
            new UserRelation { FromUserId = 1, ToUserId = 3, Status = "accepted" }, //, CreatedAt = 
            new UserRelation { FromUserId = 1, ToUserId = 4, Status = "accepted" }, //, CreatedAt = 
            new UserRelation { FromUserId = 1, ToUserId = 5, Status = "accepted" },
            new UserRelation { FromUserId = 1, ToUserId = 6, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 7, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 8, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 9, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 10, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 11, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 12, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 13, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 14, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 15, Status = "pending" },
            new UserRelation { FromUserId = 1, ToUserId = 30, Status = "deleted" },  
            new UserRelation { FromUserId = 1, ToUserId = 31, Status = "deleted" },  
            new UserRelation { FromUserId = 1, ToUserId = 32, Status = "deleted" },
            new UserRelation { FromUserId = 1, ToUserId = 33, Status = "deleted" },  
            new UserRelation { FromUserId = 1, ToUserId = 34, Status = "deleted" },  
            new UserRelation { FromUserId = 1, ToUserId = 35, Status = "deleted" },
            new UserRelation { FromUserId = 1, ToUserId = 36, Status = "deleted" },
            new UserRelation { FromUserId = 1, ToUserId = 37, Status = "deleted" },
            new UserRelation { FromUserId = 1, ToUserId = 38, Status = "deleted" },
            new UserRelation { FromUserId = 1, ToUserId = 39, Status = "deleted" },
            new UserRelation { FromUserId = 2, ToUserId = 3, Status = "accepted" },
            new UserRelation { FromUserId = 2, ToUserId = 4, Status = "pending" },
            new UserRelation { FromUserId = 2, ToUserId = 5, Status = "pending" },
            new UserRelation { FromUserId = 2, ToUserId = 6, Status = "pending" },
            new UserRelation { FromUserId = 3, ToUserId = 4, Status = "accepted" },
            new UserRelation { FromUserId = 3, ToUserId = 5, Status = "accepted" },
            new UserRelation { FromUserId = 3, ToUserId = 6, Status = "accepted" },
            new UserRelation { FromUserId = 4, ToUserId = 5, Status = "accepted" },
            new UserRelation { FromUserId = 4, ToUserId = 6, Status = "accepted" },
            new UserRelation { FromUserId = 5, ToUserId = 6, Status = "accepted" },
            new UserRelation { FromUserId = 5, ToUserId = 16, Status = "accepted" },
            new UserRelation { FromUserId = 5, ToUserId = 17, Status = "accepted" },
            new UserRelation { FromUserId = 5, ToUserId = 18, Status = "accepted" },
            new UserRelation { FromUserId = 5, ToUserId = 19, Status = "accepted" },
            new UserRelation { FromUserId = 5, ToUserId = 20, Status = "accepted" },
            new UserRelation { FromUserId = 7, ToUserId = 8, Status = "pending" },
            new UserRelation { FromUserId = 7, ToUserId = 9, Status = "pending" },
            new UserRelation { FromUserId = 7, ToUserId = 10, Status = "pending" },
            new UserRelation { FromUserId = 7, ToUserId = 11, Status = "pending" },
            new UserRelation { FromUserId = 7, ToUserId = 12, Status = "pending" },
            new UserRelation { FromUserId = 32, ToUserId = 2, Status = "pending" },
            new UserRelation { FromUserId = 32, ToUserId = 3, Status = "declined" },
            new UserRelation { FromUserId = 32, ToUserId = 4, Status = "declined" },
            new UserRelation { FromUserId = 32, ToUserId = 5, Status = "pending" },
            new UserRelation { FromUserId = 32, ToUserId = 6, Status = "pending" },
            new UserRelation { FromUserId = 32, ToUserId = 1, Status = "accepted" },
            new UserRelation { FromUserId = 32, ToUserId = 10, Status = "pending" },
            new UserRelation { FromUserId = 32, ToUserId = 25, Status = "pending" },
            new UserRelation { FromUserId = 32, ToUserId = 38, Status = "accepted" },
            new UserRelation { FromUserId = 21, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 22, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 23, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 24, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 25, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 26, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 27, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 28, ToUserId = 1, Status = "pending" },
            new UserRelation { FromUserId = 29, ToUserId = 1, Status = "pending" },
            */
        };
    }
}