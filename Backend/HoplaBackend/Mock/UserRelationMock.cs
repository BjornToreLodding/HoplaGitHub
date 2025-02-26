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
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800010"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800011"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800012"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800013"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800014"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800015"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800030"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800031"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800033"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800033"), Status = "deleted" },  
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800035"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800036"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800037"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800038"), Status = "deleted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800039"), Status = "deleted" },
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
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800016"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800017"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800018"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800019"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800020"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780008"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780009"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800010"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800011"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-123456780007"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800012"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780002"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780003"), Status = "declined" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780004"), Status = "declined" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780005"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780006"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800010"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800025"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800032"), ToUserId = Guid.Parse("12345678-0000-0000-0001-1234567800038"), Status = "accepted" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800021"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800022"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800023"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800024"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800025"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800026"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800027"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800028"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            new UserRelation { FromUserId = Guid.Parse("12345678-0000-0000-0001-1234567800029"), ToUserId = Guid.Parse("12345678-0000-0000-0001-123456780001"), Status = "pending" },
            
        };
    }
}