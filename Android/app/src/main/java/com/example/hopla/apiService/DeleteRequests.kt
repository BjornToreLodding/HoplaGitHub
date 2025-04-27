package com.example.hopla.apiService

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import android.util.Log
import com.example.hopla.universalData.ReactionRequest
import com.example.hopla.universalData.StableActionRequest
import com.example.hopla.universalData.StableResponse
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.apiUrl

// Delete a relation between two users, e.g., block a user, unfriend etc.
suspend fun relationRequestDelete(token: String, request: UserRelationChangeRequest): String {
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

// Delete a horse from the user's list of horses
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

// Remove trail from favorites list
suspend fun removeFavoriteTrail(token: String, trailId: String): String {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = client.delete(apiUrl+"trails/favorite") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(mapOf("TrailId" to trailId))
    }

    val responseBody: String = response.bodyAsText()
    Log.d("favoriteTrail", "Response: $responseBody")
    client.close()
    return responseBody
}

//Leave a stable the user is a member of
suspend fun leaveStable(token: String, stableActionRequest: StableActionRequest): StableResponse {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = client.delete(apiUrl+"stables/leave") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(stableActionRequest)
    }

    val responseBody: String = response.bodyAsText()
    client.close()
    Log.d("stableJoinLeave", "Response Body: $responseBody")
    return Json.decodeFromString(StableResponse.serializer(), responseBody)
}

// Delete a reaction from a post (e.g unlike a post)
suspend fun deleteReaction(token: String, entityId: String): String {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val requestBody = ReactionRequest(entityId = entityId)
    val response: HttpResponse = client.delete("https://hopla.onrender.com/reactions") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(requestBody)
    }

    val responseBody: String = response.bodyAsText()
    Log.d("reactionResponse", "Response Status: ${response.status}")
    client.close()
    return responseBody
}
