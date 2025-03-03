#import <Foundation/Foundation.h>

#if __has_attribute(swift_private)
#define AC_SWIFT_PRIVATE __attribute__((swift_private))
#else
#define AC_SWIFT_PRIVATE
#endif

/// The "Gjøvik" asset catalog image resource.
static NSString * const ACImageNameGjvik AC_SWIFT_PRIVATE = @"Gjøvik";

/// The "Group" asset catalog image resource.
static NSString * const ACImageNameGroup AC_SWIFT_PRIVATE = @"Group";

/// The "Group2" asset catalog image resource.
static NSString * const ACImageNameGroup2 AC_SWIFT_PRIVATE = @"Group2";

/// The "Group3" asset catalog image resource.
static NSString * const ACImageNameGroup3 AC_SWIFT_PRIVATE = @"Group3";

/// The "HorseImage" asset catalog image resource.
static NSString * const ACImageNameHorseImage AC_SWIFT_PRIVATE = @"HorseImage";

/// The "HorseImage2" asset catalog image resource.
static NSString * const ACImageNameHorseImage2 AC_SWIFT_PRIVATE = @"HorseImage2";

/// The "HorseImage3" asset catalog image resource.
static NSString * const ACImageNameHorseImage3 AC_SWIFT_PRIVATE = @"HorseImage3";

/// The "LogoUtenBakgrunn" asset catalog image resource.
static NSString * const ACImageNameLogoUtenBakgrunn AC_SWIFT_PRIVATE = @"LogoUtenBakgrunn";

/// The "Preikestolen" asset catalog image resource.
static NSString * const ACImageNamePreikestolen AC_SWIFT_PRIVATE = @"Preikestolen";

/// The "Profile" asset catalog image resource.
static NSString * const ACImageNameProfile AC_SWIFT_PRIVATE = @"Profile";

#undef AC_SWIFT_PRIVATE
