package com.example.hopla

import kotlinx.serialization.Serializable

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
    val profilePictureURL: String
)

// Class for the data of a community
data class Community(
    val name: String,
    val imageResource: Int,
    val description: String
)

// Class for the data of a post
data class Message(
    val id: String,
    val content: String,
    val timestamp: Long,
    val imageUrl: String? = null
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