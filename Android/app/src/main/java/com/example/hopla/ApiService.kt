package com.example.hopla

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*

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
