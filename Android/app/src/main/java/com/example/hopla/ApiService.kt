package com.example.hopla

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*

fun fetchMessages(messageName: String): List<Message> {
    // Replace with actual database fetching logic
    return listOf(
        Message(
            id = "1",
            content = "Welcome to the community!",
            timestamp = System.currentTimeMillis()
        ),
        Message(
            id = "2",
            content = "Hello everyone!",
            timestamp = System.currentTimeMillis()
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
