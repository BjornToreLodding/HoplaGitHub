package com.example.hopla.universalData

import android.graphics.Bitmap
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.serialization.SerialName

// Base URL for the API
const val apiUrl = "https://hopla.onrender.com/"

//------------------- Data classes for user information ----------------
// Different status relationship between users can be
enum class PersonStatus {
    FRIENDS,
    FOLLOWING,
    NONE,
    PENDING
}

// Class for the data of a user
@Serializable
data class User(
    val token: String,
    val userId: String,
    val name: String? = null,
    val alias: String? = null,
    val pictureUrl: String,
    val telephone: String? = null,
    val description: String? = null,
    val dob: DateOfBirth? = null,
    val redirect: String
)

//----------------- Data classes for horse information ----------------
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

// Class for the request to add a horse
@Serializable
data class HorseRequest(
    @SerialName("Name") val name: String,
    @SerialName("Breed") val breed: String,
    @SerialName("Year") val year: String,
    @SerialName("Month") val month: String,
    @SerialName("Day") val day: String,
    @SerialName("Image") @Contextual val image: Bitmap
)

//----------------- Universal data classes ----------------
// Class for the date of birth of a user/horse etc.
@Serializable
data class DateOfBirth(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int,
    val dayOfYear: Int,
    val dayNumber: Int
)

// Function to format a date string from the server to a more readable format
fun formatDate(dateString: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return date?.let { outputFormat.format(it) }
}

//----------------- Data classes for other users ----------------
// Class for the data of a user (for the list of other users)
@Serializable
data class OtherUsers(
    val id: String,
    val name: String?,
    val pictureUrl: String?,
    val alias: String?
)

// Interface for the data of a user (for the list of other users)
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

// Class for the data for a follower (for the list of followers)
@Serializable
data class FriendProfile(
    val id: String,
    val name: String,
    val pictureUrl: String,
    val alias: String,
    val description: String? = null,
    val dob: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val friendsCount: Int? = null,
    val horseCount: Int? = null,
    val relationStatus: String? = null,
    val userHikes: List<Hike> = emptyList(),
    val page: Int? = null,
    val size: Int? = null
)

//---------------- Community data classes -------------
// Class for the data of a post
@Serializable
data class Message(
    val content: String,
    val timestamp: String,
    val senderId: String,
    val senderAlias: String
)

//---------------- Login data classes -------------
// Class for content that are sent to database when trying to login
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// Class for content that are sent to database when trying to register
@Serializable
data class ResetPasswordRequest(@SerialName("Email") val email: String)

//------------------ Data classes for errors
// Class for content of error messages received from the server
@Serializable
data class ErrorResponse(
    val message: String
)

// Class for content of error messages received from the server
@Serializable
data class ErrorResponse2(
    val type: String,
    val title: String,
    val traceId: String,
    val errors: Map<String, List<String>>
)

//---------------- Trail data classes -------------
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

// Data class to hold the information for each ContentBox
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

// Data class to hold all the filters for the trails
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

// Response class for the trails
@Serializable
data class TrailsResponse(
    val trails: List<Trail> = emptyList(),
    val pageNumber: Int = 0,
    val pageSize: Int = 0
)

//-------------- Data classes for hikes-------------------
// Data class to hold the information for each hike
@Serializable
data class Hike(
    val id: String,
    val trailName: String,
    val length: Double,
    val duration: Double,
    val pictureUrl: String? = null,
    val page: Int? = null,
    val size: Int? = null,
    val trailButton: Boolean = false,
    val comment: String? = null,
    val horseName: String? = null,
    val title: String? = null,
    val trailId: String? = null
)

// Response class for the hikes
@Serializable
data class UserHikesResponse(
    val userHikes: List<Hike>
)

//---------------- Data classes for map -------------
// Data class to hold the information for the map
@Serializable
data class MapTrail(
    val id: String,
    val name: String,
    val latMean: Double,
    val longMean: Double,
    val trailAllCoordinates: JsonElement? = null
)

//-------- Data classes for user reports -------------
// Class for the data of a user report request
@Serializable
data class UserReportRequest(
    @SerialName("EntityId") val entityId: String,
    @SerialName("EntityName") val entityName: String,
    @SerialName("Category") val category: String? = "Annet",
    @SerialName("Message") val message: String
)

// Class for the data of a user report response
@Serializable
data class UserReportResponse(
    val message: String
)

//-------- Data classes for deleting a user -------------
// Class for the data of a user delete request
@Serializable
data class DeleteUserRequest(@SerialName("Password") val password: String)

// Class for the data of a user delete response
@Serializable
data class DeleteUserResponse(val message: String)

//-------------Data classes for updating user information----------------
// Class for the response when updating the password
@Serializable
data class ChangePasswordResponse(val message: String)

//-----------Data classes for stables----------------
// Class for the data of a stable (for the list of stables)
@Serializable
data class Stable(
    val stableId: String,
    val stableName: String,
    val distance: Double,
    val member: Boolean,
    val pictureUrl: String
)

// Class for the data of a stable (for the details of a stable)
@Serializable
data class StableDetails(
    val id: String,
    val name: String,
    val description: String,
    val pictureUrl: String,
    val isMember: Boolean
)

// The data class for stable request
@Serializable
data class StableRequest(
    @SerialName("Name") val name: String,
    @SerialName("Description") val description: String,
    @SerialName("Image") @Contextual val image: Bitmap,
    @SerialName("Latitude") val latitude: Double,
    @SerialName("Longitude") val longitude: Double,
    @SerialName("PrivateGroup") val privateGroup: Boolean
)

// The data class for stable request
@Serializable
data class StableMessageRequest(
    @SerialName("StableId") val stableId: String,
    @SerialName("Content") val content: String
)

// The data class for stable message response
@Serializable
data class StableMessageResponse(
    val message: String
)

// The data class for stable request action
@Serializable
data class StableActionRequest(
    @SerialName("StableId") val stableId: String
)

// The data class for stable request action response
@Serializable
data class StableResponse(
    val message: String
)

// The data class for stable request action response
data class FetchStableRequest(
    val token: String,
    val search: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val pageNumber: Int
)

//------------------Data classes for trails -----------------
// Class for the data of a trail (for the list of trails)
@Serializable
data class TrailRatingResponse(val message: String)

// Class for the data of a trail (for the details of a trail) the request
@Serializable
data class TrailRatingRequest(
    @SerialName("TrailId") val trailId: String,
    @SerialName("Rating") val rating: Int
)

// The class for trail filters
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

// Data class for getting the trail coordinates
@Serializable
data class TrailCoordinate(
    val lat: Double,
    val lng: Double
)

// Trail response class for the details of a trail (coordinates)
@Serializable
data class TrailResponse(
    val id: String,
    val distance: Double,
    @SerialName("coordinates") val allCoords: List<TrailCoordinate>
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
    @SerialName("TargetUserId") val targetUserId: String,
    @SerialName("Status") val status: String? = null
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
    val duration: Double,
    val userProfilePicture: String,
    val likes: Int,
    val isLikedByUser: Boolean
)

@Serializable
data class FeedResponse(
    val totalCount: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val hasNextPage: Boolean,
    val items: List<FeedItem>
)

// data class for the reaction to a post
@Serializable
data class ReactionRequest(@SerialName("EntityId") val entityId: String)

//--------------------Data classes for new hike screen---------------
// Data class for the coordinates of a new hike
@Serializable
data class Coordinate(
    val timestamp: Long,
    val lat: Double,
    val long: Double
)

// Data class for the new hike request
@Serializable
data class NewHike (
    @SerialName("StartedAt") val startetAt: String,
    @SerialName("Distance") val distance: String,
    @SerialName("Duration") val duration: String,
    @SerialName("Coordinates") val coordinates: List<Coordinate>,
    @SerialName("Title") val title: String? = null,
    @SerialName("Description") val description: String? = null,
    @SerialName("HorseId") val horseId: String? = null,
    @SerialName("TrailId") val trailId: String? = null
)

//----------------------Data classes for user hikes---------------------
// Data class for the user hike coordinates
@Serializable
data class HikeCoordinate(
    val lat: Double,
    val lng: Double
)

// Filter data class for the user hike
@Serializable
data class FilterData(
    @SerialName("FilterDefinitionId") val filterDefinitionId: String,
    @SerialName("Value") val value: String
)

// Class to create a trail request 
@Serializable
data class CreateTrailRequest(
    @SerialName("Name") val name: String,
    val description: String,
    @SerialName("UserHikeId") val userHikeId: String,
    @SerialName("Filters") val filters: List<FilterData>
)
