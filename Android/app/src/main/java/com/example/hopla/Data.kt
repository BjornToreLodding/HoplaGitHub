package com.example.hopla

import com.google.android.gms.maps.model.LatLng
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
    FRIEND,
    FOLLOWING,
    NONE,
    PENDING
}

// Class for the data of a person a user is following/friends
data class Person(
    val name: String,
    val imageResource: Int,
    val status: PersonStatus
)

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
    val email: String,
    val alias: String,
    val pictureUrl: String
)

// Class for the data of a community
data class Community(
    val name: String,
    val imageResource: Int,
    val description: String,
    val communityMemberStatus: CommunityMemberStatus,
    val communityStatus: CommunityStatus
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

// Class for the data for a friend (for the list of friends)
@Serializable
data class Friend(
    val friendId: String,
    val friendName: String,
    val friendAlias: String,
    val friendPictureURL: String
)

// Class for the data for a person the user follows (for the list of following)
@Serializable
data class Following(
    val followingUserId: String,
    val followingUserName: String,
    val followingUserAlias: String,
    val followingUserPicture: String
)

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

val presetFilters = listOf("gravel", "sand", "asphalt", "dirt")

// Define the Filters class
data class Filters(
    val filterStrings: Set<String> = emptySet(),
    val difficulty: Difficulty? = null
)

// Data class to hold the information for each ContentBox
data class ContentBoxInfo(
    val title: String,
    val imageResource: Set<Int>,
    val isHeartClicked: Boolean,
    val starRating: Int,
    val filters: Filters = Filters(),
    val description: String
)

data class TestLocation(
    val mainCoordinate: LatLng,
    val name: String,
    val tripCoordinates: List<LatLng>
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
    val userHikes: List<Hike> = emptyList()
)

@Serializable
data class Hike(
    val id: String,
    val trailName: String,
    val length: Double,
    val duration: Double,
    val pictureUrl: String
)

fun formatDate(dateString: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return date?.let { outputFormat.format(it) }
}