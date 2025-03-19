package com.example.hopla.universalData

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

// Base URL for the API
const val apiUrl = "https://hopla.onrender.com/"

// Class for the data of a trip
data class Trip(
    val name: String,
    val date: String,
    val length: String,
    val time: String,
    val imageResource: Int
)

// Different status relationship between users can be
enum class PersonStatus {
    FRIENDS,
    FOLLOWING,
    NONE,
    PENDING
}

// Class for details about a specific horse
@Serializable
data class HorseDetail(
    val name: String,
    val horsePictureUrl: String,
    val dob: String,
    val age: Int,
    val breed: String
)

// Class for the data of a horse (for the list of horses)
@Serializable
data class Horse(
    val id: String,
    val name: String,
    val horsePictureUrl: String
)

// Class for the data of a user (yourself)
@Serializable
data class User(
    val token: String,
    val userId: String,
    val name: String,
    val alias: String,
    val pictureUrl: String,
    val telephone: Int? = null,
    val description: String,
    val dob: String,
    val redirect: String
)


@Serializable
data class OtherUsers(
    val id: String,
    val name: String,
    val pictureUrl: String,
    val alias: String
)

// Class for the data of a community
data class Community(
    val name: String,
    val imageResource: Int,
    val description: String,
    val communityMemberStatus: CommunityMemberStatus,
    val communityStatus: CommunityStatus,
    val latitude: Double? = null,
    val longitude: Double? = null
)

enum class CommunityMemberStatus{
    NONE,
    REQUEST,
    MEMBER,
    ADMIN
}

enum class CommunityStatus{
    PUBLIC,
    PRIVATE
}

// Class for the data of a post
data class Message(
    val id: String,
    val content: String,
    val timestamp: Long,
    val imageUrl: String? = null,
    val username: String
)

interface UserItem {
    val id: String
    val name: String
    val alias: String
    val pictureUrl: String
}

// Class for the data for a friend (for the list of friends)
@Serializable
data class Friend(
    val friendId: String,
    val friendName: String,
    val friendAlias: String,
    val friendPictureURL: String
) : UserItem {
    override val id: String get() = friendId
    override val name: String get() = friendName
    override val alias: String get() = friendAlias
    override val pictureUrl: String get() = friendPictureURL
}

// Class for the data for a following user (for the list of following users)
@Serializable
data class Following(
    val followingUserId: String,
    val followingUserName: String,
    val followingUserAlias: String,
    val followingUserPicture: String
) : UserItem {
    override val id: String get() = followingUserId
    override val name: String get() = followingUserName
    override val alias: String get() = followingUserAlias
    override val pictureUrl: String get() = followingUserPicture
}

// Class for content that are sent to database when trying to login
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// Class for content of error messages received from the server
@Serializable
data class ErrorResponse(
    val message: String
)

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

// Define the Filters class
data class Filters(
    val filterStrings: Set<String> = emptySet(),
    val difficulty: Difficulty? = null
)

// Data class to hold the information for each ContentBox
data class ContentBoxInfo(
    val id: String,
    val title: String,
    val imageResource: List<Any>,
    val isFavorite: Boolean,
    val starRating: Int,
    val filters: Filters = Filters(),
    val description: String
)

@Serializable
data class FriendProfile(
    val id: String,
    val name: String,
    val pictureUrl: String,
    val alias: String,
    val description: String? = null,
    val dob: String? = null,
    val created_at: String? = null,
    val friendsCount: Int? = null,
    val horseCount: Int? = null,
    val relationStatus: String? = null,
    val userHikes: List<Hike> = emptyList(),
    val page: Int? = null,
    val size: Int? = null
)

@Serializable
data class Hike(
    val id: String,
    val trailName: String,
    val length: Double,
    val duration: Double,
    val pictureUrl: String,
    val page: Int? = null,
    val size: Int? = null

)

fun formatDate(dateString: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return date?.let { outputFormat.format(it) }
}

@Serializable
data class Trail(
    val id: String,
    val name: String,
    val pictureUrl: String?,
    val averageRating: Int,
    val difficulty: String? = "EASY",
    val isFavorite: Boolean? = false,
    val filters: List<String>? = listOf("gravel", "sand", "asphalt", "dirt"),
)

@Serializable
data class TrailsResponse(
    val trails: List<Trail>,
    val pageNumber: Int,
    val pageSize: Int
)

@Serializable
data class MapTrail(
    val id: String,
    val name: String,
    val latMean: Double,
    val longMean: Double,
    val trailAllCoordinates: List<String>?
)

@Serializable
data class ErrorResponse2(
    val type: String,
    val title: String,
    val traceId: String,
    val errors: Map<String, List<String>>
)

//-------- Data classes for user reports -------------
@Serializable
data class UserReportRequest(
    val EntityId: String,
    val EntityName: String,
    val Category: String? = "Annet",
    val Message: String
)

@Serializable
data class UserReportResponse(
    val message: String
)

//-------- Data classes for deleting a user -------------
@Serializable
data class DeleteUserRequest(val Password: String)

@Serializable
data class DeleteUserResponse(val message: String)