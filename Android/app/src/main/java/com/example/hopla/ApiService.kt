package com.example.hopla

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

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