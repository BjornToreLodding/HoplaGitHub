package com.example.hopla.apiService

import android.util.Log
import com.example.hopla.universalData.ErrorResponse2
import com.example.hopla.universalData.Following
import com.example.hopla.universalData.Friend
import com.example.hopla.universalData.FriendProfile
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.Horse
import com.example.hopla.universalData.HorseDetail
import com.example.hopla.universalData.MapTrail
import com.example.hopla.universalData.Message
import com.example.hopla.universalData.OtherUsers
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.TrailsResponse
import com.example.hopla.universalData.apiUrl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
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

suspend fun fetchHorses(userId: String, token: String): List<Horse> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "horses/userhorses?userid=$userId") {
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
        val response: HttpResponse = client.get(apiUrl +"horses/$horseId") {
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
        val response: HttpResponse = client.get(apiUrl +"userrelations/friends") {
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

suspend fun fetchUserHikes(token: String, pageNumber: Int): List<Hike> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl +"userhikes/user?pageNumber=$pageNumber") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("UserHikesScreen", "PageNumb: $pageNumber")
        Log.d("UserHikesScreen", "Response: $responseBody")
        val userHikesResponse: UserHikesResponse = response.body()
        userHikesResponse.userHikes
    }
}

suspend fun fetchUserFriends(userId: String, token: String): List<Friend> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get("https://hopla.onrender.com/userrelations/friends?userid=$userId") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}
//---------------------------------Trails---------------------------------
// All trails
suspend fun fetchTrails(token: String, pageNumber: Int, searchQuery: String): TrailsResponse {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"trails/all?search=$searchQuery&pageNumber=$pageNumber") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("fetchTrails", "Response: $responseBody")
        response.body()
    }
}
// Trails by position
suspend fun fetchTrailsByLocation(token: String, latitude: Double, longitude: Double, pageNumber: Int): TrailsResponse {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "trails/list?latitude=$latitude&longitude=$longitude&pageNumber=$pageNumber") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("fetchTrailsDistance", "Response: $responseBody")
        response.body()
    }
}

// Favorite trails
suspend fun fetchFavoriteTrails(token: String): TrailsResponse {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "trails/favorites") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("fetchFavoriteTrails", "Response: $responseBody")
        response.body()
    }
}

// Friends and followers trails
suspend fun fetchTrailsRelations(token: String, pageNumber: Int): TrailsResponse {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "trails/relations?friends=true&following=true&pagenumber=$pageNumber") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("fetchTrailsRelations", "Response: $responseBody")

        if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Client request (${response.request.url}) failed: ${response.status}.")
        }
    }
}

// Trail on the map
suspend fun fetchTrailsOnMap(token: String, latitude: Double, longitude: Double, zoomLevel: Int): List<MapTrail> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "trails/map?latitude=$latitude&longitude=$longitude&zoomlevel=$zoomLevel") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        if (response.status == HttpStatusCode.OK) {
            val responseBody: String = response.bodyAsText()
            Log.d("fetchTrailsOnMap", "Response: $responseBody")
            response.body()
        } else {
            val errorResponse: ErrorResponse2 = response.body()
            Log.e("fetchTrailsOnMap", "Error: ${errorResponse.title}, Details: ${errorResponse.errors}")
            emptyList()
        }
    }
}

//-----------------------------------------------------------------------------------------------
suspend fun fetchAllUsers(token: String): List<OtherUsers> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get("https://hopla.onrender.com/users/all") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

//-------------------GET requests for stables--------------
suspend fun fetchStables(token: String, search: String, latitude: Double, longitude: Double, pageNumber: Int): List<Stable> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        try {

            Log.d("fetchStables", "Requesting stables with parameters: search=$search, latitude=$latitude, longitude=$longitude, pageNumber=$pageNumber")
            val response: HttpResponse = client.get("https://hopla.onrender.com/stables/all?search=$search&latitude=$latitude&longitude=$longitude&pagenumber=$pageNumber") {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }
            val responseBody: String = response.bodyAsText()
            Log.d("fetchStables", "Response: $responseBody")
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                Log.e("fetchStables", "Request failed with status: ${response.status}")
                Log.e("fetchStables", "Error response: $responseBody")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("fetchStables", "Exception occurred: ${e.message}", e)
            emptyList()
        }
    }
}