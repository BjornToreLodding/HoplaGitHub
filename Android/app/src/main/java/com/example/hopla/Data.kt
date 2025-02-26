package com.example.hopla

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

// Class for the data of a horse
data class Horse(
    val name: String,
    val imageResource: Int,
    val breed: String,
    val age: Int
)

// Class for the data of a user (yourself)
data class User(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val createdTime: String
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