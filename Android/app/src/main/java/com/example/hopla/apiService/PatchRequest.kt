package com.example.hopla.apiService

import com.example.hopla.universalData.DeleteUserResponse
import com.example.hopla.universalData.apiUrl
import io.ktor.client.HttpClient
import android.util.Log
import com.example.hopla.universalData.DeleteUserRequest
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun deleteUser(token: String, password: String): DeleteUserResponse {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response = client.patch(apiUrl+"users/delete") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(DeleteUserRequest(Password = password))
    }

    val responseBody: String = response.bodyAsText()
    Log.d("deleteUser", "Response: $responseBody")
    client.close()
    return Json.decodeFromString(DeleteUserResponse.serializer(), responseBody)
}