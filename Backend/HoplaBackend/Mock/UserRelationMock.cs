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
                FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), Status = "FRIENDS", //, CreatedAt = 
            },
            
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "FRIENDS" }, //, CreatedAt = 
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "FRIENDS" }, //, CreatedAt = 
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780008"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780009"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780010"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780011"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780012"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780013"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780014"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780015"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780030"), Status = "FOLLOWING" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780031"), Status = "FOLLOWING" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780033"), Status = "FOLLOWING" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780033"), Status = "FOLLOWING" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780035"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780036"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780037"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780038"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780039"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780016"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780017"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780018"), Status = "FOLLOWING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780019"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780020"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780008"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780009"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780010"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780011"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780012"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "declined" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "declined" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780010"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780025"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780038"), Status = "FRIENDS" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780021"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780022"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780023"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780024"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780025"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780026"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780027"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780028"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780029"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "PENDING" },
            
        };
    }
}