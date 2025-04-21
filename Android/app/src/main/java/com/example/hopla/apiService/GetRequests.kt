package com.example.hopla.apiService

import android.util.Log
import com.example.hopla.universalData.ErrorResponse2
import com.example.hopla.universalData.FeedResponse
import com.example.hopla.universalData.Following
import com.example.hopla.universalData.Friend
import com.example.hopla.universalData.FriendProfile
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.HikeCoordinate
import com.example.hopla.universalData.Horse
import com.example.hopla.universalData.HorseDetail
import com.example.hopla.universalData.MapTrail
import com.example.hopla.universalData.Message
import com.example.hopla.universalData.OtherUsers
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.StableDetails
import com.example.hopla.universalData.TrailFilter
import com.example.hopla.universalData.TrailResponse
import com.example.hopla.universalData.TrailUpdate
import com.example.hopla.universalData.TrailsResponse
import com.example.hopla.universalData.UserHikesResponse
import com.example.hopla.universalData.UserRelationRequest
import com.example.hopla.universalData.apiUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Fetch a list of horses from the database and return as a list
suspend fun fetchHorses(
    userId: String,
    token: String,
    httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
): List<Horse> {
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl + "horses/userhorses?userid=$userId") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }

        val responseBody: String = response.bodyAsText()
        Log.d("fetchHorses", "Response Code: ${response.status.value}")
        Log.d("fetchHorses", "Response Body: $responseBody")

        return when (response.status) {
            HttpStatusCode.NotFound -> emptyList()
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Unexpected response: ${response.status}")
        }
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

//----------------Get requests for user hikes-------------------
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

suspend fun fetchUserHikeCoordinates(userHikeId: String, token: String): List<HikeCoordinate>? {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    return httpClient.use { client ->
        val url = "https://hopla.onrender.com/userhikes/coordinates/$userHikeId"
        Log.d("fetchUserHikeCoordinates", "Request URL: $url")
        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $token")
        }

        val responseBody: String = response.bodyAsText()
        Log.d("fetchUserHikeCoordinates", "Response: $responseBody")

        if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            Log.e("fetchUserHikeCoordinates", "Error: ${response.status}")
            null
        }
    }
}

//----------------Get requests for user relations-------------------
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
suspend fun fetchTrails(token: String, pageNumber: Int, searchQuery: String, filtersQuery: String): TrailsResponse {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val url = URLBuilder(apiUrl).apply {
            path("trails", "all")
            parameters.append("search", searchQuery)
            parameters.append("pageNumber", pageNumber.toString())
            // Append the filtersQuery directly without URL encoding
            encodedParameters.append("filter", filtersQuery)
        }.buildString()

        Log.d("fetchTrails", "Request URL: $url")
        val response: HttpResponse = client.get(url) {
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
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000 // 120 seconds
            connectTimeoutMillis = 120_000 // 120 seconds
            socketTimeoutMillis = 120_000 // 120 seconds
        }
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

// Fetch updates about the specified trail
suspend fun fetchTrailUpdates(trailId: String, pageNumber: Int, token: String): List<TrailUpdate> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"trails/updates") {
            parameter("trailId", trailId)
            parameter("pageNumber", pageNumber)
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        response.body()
    }
}

// Fetch filters available for trails
suspend fun fetchTrailFilters(token: String): List<TrailFilter> {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = client.get(apiUrl + "trailfilters/all") {
        headers {
            append("Authorization", "Bearer $token")
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    val responseBody: String = response.bodyAsText()
    Log.d("fetchTrailFilters", "Response: $responseBody")
    client.close()
    return response.body()
}

// Fetch all coordinates for the specified trail
suspend fun fetchTrailCoordinates(trailId: String, token: String): TrailResponse? {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    val url = (apiUrl+"trails/prepare?trailId=$trailId")
    Log.d("fetchTrailDetails", "Request URL: $url")

    return try {
        httpClient.use { client ->
            val response: HttpResponse = client.get(url) {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }

            val responseBody: String = response.bodyAsText()
            Log.d("fetchTrailDetails", "Response Body: $responseBody")

            response.body()
        }
    } catch (e: Exception) {
        Log.e("fetchTrailDetails", "Error fetching trail details", e)
        null
    }
}

//-------------------------------Other users--------------------------------------------------
// All users
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

// Users that have a friend request sent to logged in user
suspend fun fetchUserRelationRequests(token: String): List<UserRelationRequest> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    return httpClient.use { client ->
        val response: HttpResponse = client.get(apiUrl+"userrelations/requests") {
            header("Authorization", "Bearer $token")
        }
        response.body()
    }
}

//-------------------GET requests for stables--------------
suspend fun fetchStables(token: String, search: String, userid: String, latitude: Double, longitude: Double, pageNumber: Int): List<Stable> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            Log.d("HttpTimeout", "Timeout settings: requestTimeout=60s, connectTimeout=60s, socketTimeout=60s")
            requestTimeoutMillis = 120_000 // 60 seconds
            connectTimeoutMillis = 120_000 // 60 seconds
            socketTimeoutMillis = 120_000 // 60 seconds
        }
    }
    return httpClient.use { client ->
        try {

            Log.d("fetchStables", "Requesting stables with parameters: search=$search, latitude=$latitude, longitude=$longitude, pageNumber=$pageNumber")
            val response: HttpResponse = client.get(apiUrl+"stables/all?search=$search&userid=$userid&latitude=$latitude&longitude=$longitude&pagenumber=$pageNumber") {
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

suspend fun fetchStableDetails(token: String, stableId: String): StableDetails? {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    Log.d("fetchStableDetails", "Request URL: https://hopla.onrender.com/stables/$stableId")
    Log.d("fetchStableDetails", "Token: $token")

    return try {
        httpClient.use { client ->
            val response: HttpResponse = client.get("https://hopla.onrender.com/stables/$stableId") {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }

            val responseBody: String = response.bodyAsText()
            Log.d("fetchStableDetails", "Response Code: ${response.status.value}")
            Log.d("fetchStableDetails", "Response Body: $responseBody")

            response.body()
        }
    } catch (e: Exception) {
        Log.e("fetchStableDetails", "Error fetching stable details", e)
        null
    }
}

suspend fun fetchStableMessages(token: String, stableId: String, pageNumber: Int): List<Message> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    val url = "https://hopla.onrender.com/stablemessages/$stableId?pagenumber=$pageNumber"
    Log.d("fetchStableMessages", "Request URL: $url")
    Log.d("fetchStableMessages", "Token: $token")

    return try {
        httpClient.use { client ->
            val response: HttpResponse = client.get(url) {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }

            val responseBody: String = response.bodyAsText()
            Log.d("fetchStableMessages", "Response Code: ${response.status.value}")
            Log.d("fetchStableMessages", "Response Body: $responseBody")

            response.body()
        }
    } catch (e: Exception) {
        Log.e("fetchStableMessages", "Error fetching stable messages", e)
        emptyList()
    }
}

//--------------------Get requests for home screen ------------------------
suspend fun fetchFeed(
    token: String,
    pageNumber: Int,
    onlyFriendsAndFollowing: Boolean = false,
    onlyLikedTrails: Boolean = false,
    latitude: Double? = null,
    longitude: Double? = null,
    sortByLikes: Boolean = false
): FeedResponse? {
    val httpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000 // 120 seconds
            connectTimeoutMillis = 120_000 // 120 seconds
            socketTimeoutMillis = 120_000 // 120 seconds
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    return httpClient.use { client ->
        val url = URLBuilder(apiUrl).apply {
            path("feed", "all")
            parameters.append("show", "userhikes,trails,trailreviews,horses")
            parameters.append("pageNumber", pageNumber.toString())
            if (onlyFriendsAndFollowing) {
                parameters.append("onlyFriendsAndFollowing", "true")
            }
            if (onlyLikedTrails) {
                parameters.append("onlyLikedTrails", "true")
            }
            if (latitude != null && longitude != null) {
                parameters.append("userlat", latitude.toString())
                parameters.append("userlong", longitude.toString())
                parameters.append("radius", "20")
            }
            if (sortByLikes) {
                parameters.append("sort", "likes")
            }
        }.buildString()

        Log.d("fetchFeed", "Request URL: $url")
        val response: HttpResponse = client.get(url) {
            headers {
                append("Authorization", "Bearer $token")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }

        val responseBody: String = response.bodyAsText()
        Log.d("fetchFeed", "Response Code: ${response.status.value}")
        Log.d("fetchFeed", "Response Body: $responseBody")

        if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            Log.e("fetchFeed", "Error: ${response.status}")
            null
        }
    }
}