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
            },
            
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "accepted" }, //, CreatedAt = 
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "accepted" }, //, CreatedAt = 
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780008"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780009"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780010"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780011"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780012"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780013"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780014"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780015"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780030"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780031"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780033"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780033"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780035"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780036"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780037"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780038"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780039"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780016"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780017"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780018"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780019"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780020"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780008"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780009"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780010"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780011"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780012"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "declined" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "declined" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780010"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780025"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780038"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780021"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780022"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780023"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780024"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780025"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780026"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780027"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780028"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780029"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            
        };
    }
}