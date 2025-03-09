package com.example.hopla

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun fetchMessages(messageName: String): List<Message> {
    val currentTime = System.currentTimeMillis()
    val oneDayInMillis = 24 * 60 * 60 * 1000
    val yesterdayTime = currentTime - oneDayInMillis

    return listOf(
        Message(
            id = "1",
            content = "Welcome to the community!",
            timestamp = yesterdayTime,
            username = "Bob"
        ),
        Message(
            id = "2",
            content = "Hello everyone!",
            timestamp = yesterdayTime,
            username = "Maria"
        )
    )
}

suspend fun fetchHorses(token: String): List<Horse> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"horses/userhorses/") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

suspend fun fetchHorseDetails(horseId: String, token: String): HorseDetail {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"horses/$horseId") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

suspend fun fetchFriends(token: String): List<Friend> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"userrelations/friends") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

suspend fun fetchFollowing(token: String): List<Following> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "userrelations/following") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

suspend fun fetchFriendProfile(userId: String, token: String): FriendProfile {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "users/profile?userId=$userId") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

@Serializable
data class UserHikesResponse(
    val userHikes: List<Hike>
)

suspend fun fetchUserHikes(token: String): List<Hike> {
    val pageNumb = 1
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"userhikes/user?pageNumber=$pageNumb") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("UserHikesScreen", "PageNumb: $pageNumb")
        Log.d("UserHikesScreen", "Response: $responseBody")
        val userHikesResponse: UserHikesResponse = response.body()
        userHikesResponse.userHikes
    }
}
