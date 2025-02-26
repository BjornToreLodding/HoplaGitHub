package com.example.hopla

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*

// For user data (profile)
interface ApiService {
    @GET("users/int/$baseID")
    suspend fun getUser(): User
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://hopla.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

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
    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return try {
        val response: HttpResponse = client.get("https://hopla.onrender.com/horses/userhorses/") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    } finally {
        client.close()
    }
}