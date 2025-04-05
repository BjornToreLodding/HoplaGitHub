package com.example.hopla.universalData

import android.graphics.Bitmap
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.text.SimpleDateFormat
import java.util.Locale

// Base URL for the API
const val apiUrl = "https://hopla.onrender.com/"


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
    val dob: DateOfBirth,
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
data class DateOfBirth(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int,
    val dayOfYear: Int,
    val dayNumber: Int
)

@Serializable
data class User(
    val token: String,
    val userId: String,
    val name: String? = null,
    val alias: String? = null,
    val pictureUrl: String,
    val telephone: Int? = null,
    val description: String? = null,
    val dob: DateOfBirth? = null,
    val redirect: String
)

@Serializable
data class OtherUsers(
    val id: String,
    val name: String?,
    val pictureUrl: String?,
    val alias: String?
)

// Class for the data of a post
@Serializable
data class Message(
    val content: String,
    val timestamp: String,
    val senderId: String,
    val senderAlias: String
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
    val description: String,
    val filters: List<String>
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
    val description: String? = null,
    val pictureUrl: String,
    val averageRating: Int,
    val isFavorite: Boolean,
    val filters: List<Filter>? = null,
)

@Serializable
data class Filter(
    val id: String,
    val name: String,
    val displayName: String,
    val type: String,
    val options: List<String>,
    val value: String,
    val defaultValue: String
)

@Serializable
data class TrailsResponse(
    val trails: List<Trail> = emptyList(),
    val pageNumber: Int = 0,
    val pageSize: Int = 0
)

@Serializable
data class MapTrail(
    val id: String,
    val name: String,
    val latMean: Double,
    val longMean: Double,
    val trailAllCoordinates: String? = null
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

//-------------Data classes for updating user information----------------
@Serializable
data class ChangePasswordResponse(val message: String)


//-----------Data classes for stables----------------
@Serializable
data class Stable(
    val stableId: String,
    val stableName: String,
    val distance: Double,
    val member: Boolean,
    val pictureUrl: String
)

@Serializable
data class StableDetails(
    val id: String,
    val name: String,
    val description: String,
    val pictureUrl: String,
    val isMember: Boolean
)

@Serializable
data class StableRequest(
    val Name: String,
    val Description: String,
    @Contextual val Image: Bitmap,
    val Latitude: Double,
    val Longitude: Double,
    val PrivateGroup: Boolean
)

@Serializable
data class StableMessageRequest(
    val StableId: String,
    val Content: String
)

@Serializable
data class StableMessageResponse(
    val message: String
)

@Serializable
data class StableActionRequest(
    val StableId: String
)

@Serializable
data class StableActionResponse(
    val message: String
)

//---------------- Data classes for login page ----------------
@Serializable
data class ResetPasswordRequest(val Email: String)


//-------------------- Data classes for horses -----------
@Serializable
data class HorseRequest(
    val Name: String,
    val Breed: String,
    val Year: String,
    val Month: String,
    val Day: String,
    @Contextual val Image: Bitmap
)

//------------------Data classes for trails -----------------
@Serializable
data class TrailRatingResponse(val message: String)

@Serializable
data class TrailRatingRequest(val TrailId: String, val Rating: Int)

@Serializable
data class UserHikesResponse(
    val userHikes: List<Hike>
)

@Serializable
data class TrailFilter(
    val id: String,
    val name: String,
    val displayName: String,
    val type: String,
    val options: List<String>,
    val defaultValue: JsonElement
)

// Data class for updates about a specified trail
@Serializable
data class TrailUpdate(
    val id: String,
    val comment: String,
    val pictureUrl: String,
    val condition: Int,
    val createdAt: String,
    val alias: String
)

@Serializable
data class TrailCoordinate(
    val lat: Double,
    val long: Double
)

@Serializable
data class TrailResponse(
    val id: String,
    val distance: Double,
    val allCoords: List<TrailCoordinate>
)

//---------------------Data classes for relations-----------
// A request from another user of becoming friends
@Serializable
data class UserRelationRequest(
    val id: String,
    val fromUserId: String,
    val fromUserAlias: String,
    val fromUserName: String
)

// Classes for responding to relation changes
@Serializable
data class UserRelationChangeRequest(
    val TargetUserId: String,
    val Status: String? = null
)

@Serializable
data class UserRelationResponse(
    val message: String,
    val status: String ? = null
)

//---------------------Data classes for home screen-----------------
@Serializable
data class FeedItem(
    val entityId: String,
    val entityName: String,
    val title: String,
    val description: String,
    val pictureUrl: String,
    val actionType: String,
    val createdAt: String,
    val userId: String,
    val userAlias: String,
    val duration: Double
)

@Serializable
data class FeedResponse(
    val totalCount: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val hasNextPage: Boolean,
    val items: List<FeedItem>
)