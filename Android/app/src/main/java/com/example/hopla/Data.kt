package com.example.hopla

import kotlinx.serialization.Serializable

const val baseID = "3"
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

@Serializable
data class HorseDetail(
    val name: String,
    val horsePictureUrl: String,
    val dob: String,
    val age: Int,
    val breed: String
)

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

data class Message(
    val id: String,
    val content: String,
    val timestamp: Long
)

@Serializable
data class Friend(
    val friendId: String,
    val friendName: String,
    val friendAlias: String,
    val friendPictureURL: String
)

@Serializable
data class Following(
    val followingUserId: String,
    val followingUserName: String,
    val followingUserAlias: String,
    val followingUserPicture: String
)