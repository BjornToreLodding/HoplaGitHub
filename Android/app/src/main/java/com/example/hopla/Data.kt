package com.example.hopla

// Class for the data of a trip
data class Trip(
    val name: String,
    val date: String,
    val length: String,
    val time: String,
    val imageResource: Int
)

// Class for the data of a friend
data class Friend(
    val name: String,
    val imageResource: Int
)

// Class for the data of a horse
data class Horse(
    val name: String,
    val imageResource: Int,
    val breed: String,
    val age: Int
)