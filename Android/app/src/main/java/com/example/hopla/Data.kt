package com.example.hopla

const val baseURLID = "/12345678-0000-0000-"
const val horseURLID = "0002-"
const val userURLID = "0123456780003"
const val baseID = "3"

// Class for the data of a trip
data class Trip(
    val name: String,
    val date: String,
    val length: String,
    val time: String,
    val imageResource: Int
)

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
