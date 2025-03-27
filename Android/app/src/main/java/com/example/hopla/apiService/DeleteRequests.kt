package com.example.hopla.apiService

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import android.util.Log
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.apiUrl

suspend fun sendUserRelationRequestDelete(token: String, request: UserRelationChangeRequest): String {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = client.delete(apiUrl + "userrelations") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    val responseBody: String = response.bodyAsText()
    Log.d("deleteRelation", "Response: $responseBody")
    client.close()
    return responseBody
}

suspend fun deleteHorse(token: String, horseId: String): String {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val url = "http://hopla.onrender.com/horses/delete/$horseId"
    var response: HttpResponse = client.delete(url) {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
    }

    // Handle redirect manually
    if (response.status == HttpStatusCode.TemporaryRedirect) {
        val redirectUrl = response.headers[HttpHeaders.Location]
        if (redirectUrl != null) {
            response = client.delete(redirectUrl) {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
            }
        }
    }

    val responseBody: String = response.bodyAsText()
    Log.d("deleteHorse", "Status: ${response.status.value}, Response: $responseBody")
    client.close()
    return responseBody
}