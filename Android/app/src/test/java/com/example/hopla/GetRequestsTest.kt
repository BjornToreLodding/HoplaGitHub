package com.example.hopla

import com.example.hopla.apiService.fetchHorses
import com.example.hopla.universalData.DateOfBirth
import com.example.hopla.universalData.FeedItem
import com.example.hopla.universalData.FeedResponse
import com.example.hopla.universalData.FetchStableRequest
import com.example.hopla.universalData.Following
import com.example.hopla.universalData.Friend
import com.example.hopla.universalData.FriendProfile
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.HikeCoordinate
import com.example.hopla.universalData.HorseDetail
import com.example.hopla.universalData.MapTrail
import com.example.hopla.universalData.Message
import com.example.hopla.universalData.OtherUsers
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.StableDetails
import com.example.hopla.universalData.Trail
import com.example.hopla.universalData.TrailFilter
import com.example.hopla.universalData.TrailUpdate
import com.example.hopla.universalData.TrailsResponse
import com.example.hopla.universalData.UserHikesResponse
import com.example.hopla.universalData.UserRelationRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.headersOf
import io.ktor.http.path
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

// Test for fetchHorses function, getting a list of horses
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class FetchHorsesTest {

    @Test
    fun `test fetchHorses with valid response`() = runBlocking {
        // Mock JSON response
        val jsonResponse = """
        [
            {"id": "1", "name": "Horse A", "horsePictureUrl": "urlA"},
            {"id": "2", "name": "Horse B", "horsePictureUrl": "urlB"}
        ]
    """.trimIndent()

        // Mock HTTP client
        val mockEngine = MockEngine { request ->
            // Check if the Authorization header contains the correct token
            if (request.headers["Authorization"] == "Bearer token") {
                respond(
                    content = jsonResponse,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            } else {
                respond(
                    content = "Unauthorized",
                    status = HttpStatusCode.Unauthorized
                )
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Call the function
        val result = fetchHorses("userId", "token", client)

        // Assertions
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Horse A", result[0].name)
        assertEquals("urlA", result[0].horsePictureUrl)
    }

    @Test
    fun `test fetchHorses with 404 response`() = runBlocking {
        // Mock HTTP client
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Call the function
        val result = fetchHorses("userId", "token", client)

        // Assertions
        assertTrue(result.isEmpty())
    }

    @Test
    fun `test fetchHorses with unexpected error`() = runBlocking {
        // Mock HTTP client
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        try {
            // Call the function (expected to throw an exception)
            fetchHorses("userId", "token")
            fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            // Verify the exception message
            assertTrue(e.message!!.contains("Unexpected response"))
        }
    }
}

// Test for fetching horse details
class FetchHorseDetailsTest {

    private val baseUrl = "https://hopla.onrender.com/"

    @Test
    fun `fetchHorseDetailsTestable returns deserialized HorseDetail on success`(): Unit = runTest {
        // Arrange
        val horseId = "horse123"
        val token = "testToken"
        val expectedDetail = HorseDetail(
            name = "Horse",
            horsePictureUrl = "url@url.com",
            dob = LocalDate.of(2010, 5, 12).toDateOfBirth(),
            age = 7,
            breed = "Arabian",
        )
        val jsonText = Json.encodeToString(HorseDetail.serializer(), expectedDetail)

        val mockEngine = MockEngine { requestData ->
            // Verify method and URL
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("${baseUrl}horses/$horseId", requestData.url.toString())
            // Verify header
            assertEquals("Bearer $token", requestData.headers["Authorization"])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
            }
        }

        // Act
        val actual = fetchHorseDetailsTestable(client, horseId, token, baseUrl)

        // Assert
        assertEquals(expectedDetail, actual)
    }

    // Testable variant accepting HttpClient + baseUrl
    private suspend fun fetchHorseDetailsTestable(
        client: HttpClient,
        horseId: String,
        token: String,
        baseUrl: String
    ): HorseDetail {
        val response: HttpResponse = client.get(baseUrl + "horses/$horseId") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        return response.body()
    }
    private fun LocalDate.toDateOfBirth(): DateOfBirth {
        return DateOfBirth(
            year = this.year,
            month = this.monthValue,
            day = this.dayOfMonth,
            dayOfWeek = this.dayOfWeek.value,
            dayOfYear = this.dayOfYear,
            dayNumber = this.toEpochDay().toInt()
        )
    }
}

// Test for fetching user hikes
class FetchUserHikesTest {

    private val baseUrl = "https://hopla.onrender.com/"

    @Test
    fun `fetchUserHikesTestable returns list of hikes on success`() = runTest {
        // Arrange
        val token = "mockToken"
        val pageNumber = 2
        val hikes = listOf(
            Hike("1", "Morning Trail", 5.0, 2.0),
            Hike("2", "Sunset Ride", 3.5, 1.5)
        )
        val responseObj = UserHikesResponse(userHikes = hikes)
        val jsonText = Json.encodeToString(UserHikesResponse.serializer(), responseObj)

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, and header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals(
                "https://hopla.onrender.com/userhikes/user?pageNumber=$pageNumber",
                requestData.url.toString()
            )
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchUserHikesTestable(client, baseUrl, token, pageNumber)

        // Assert
        assertEquals(2, actual.size)
        assertEquals(hikes, actual)
    }
    // Test for fetchUserHikes with 404 response
    @Test
    fun `fetchUserHikesTestable returns empty list on 404`() = runTest {
        // Arrange
        val token = "mockToken"
        val pageNumber = 5

        val mockEngine = MockEngine {
            respond(
                content = """{"userHikes":[]}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchUserHikesTestable(client, baseUrl, token, pageNumber)

        // Assert
        assertEquals(emptyList<Hike>(), actual)
    }
    // Testable variant accepting HttpClient + baseUrl
    private suspend fun fetchUserHikesTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        pageNumber: Int
    ): List<Hike> {
        val response: HttpResponse = client.get(baseUrl+"userhikes/user?pageNumber=$pageNumber") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        // Deserialize JSON-response to UserHikesResponse
        val userHikesResponse: UserHikesResponse = response.body()
        return userHikesResponse.userHikes
    }
}

// Test for fetching user hike coordinates (longitude/latitude)
class FetchUserHikeCoordinatesTest {

    private val baseUrl = "https://hopla.onrender.com/"

    // Testable variant that injects client and baseUrl
    private suspend fun fetchUserHikeCoordinatesTestable(
        client: HttpClient,
        baseUrl: String,
        userHikeId: String,
        token: String
    ): List<HikeCoordinate>? {
        val url = baseUrl+"userhikes/coordinates/$userHikeId"
        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $token")
        }
        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            null
        }
    }

    @Test
    fun `returns list of coordinates on 200 OK`() = runTest {
        // Arrange
        val userHikeId = "hike42"
        val token = "mockToken"
        val coords = listOf(
            HikeCoordinate(59.91, 10.75),
            HikeCoordinate(59.92, 10.76)
        )
        val json = Json.encodeToString(ListSerializer(HikeCoordinate.serializer()), coords)

        val mockEngine = MockEngine { requestData ->
            // Check that URL and header are correct
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals(baseUrl+"userhikes/coordinates/$userHikeId", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = json,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val result = fetchUserHikeCoordinatesTestable(client, baseUrl, userHikeId, token)

        // Assert
        assertEquals(coords, result)
    }

    @Test
    fun `returns null on non-OK status`() = runTest {
        // Arrange
        val userHikeId = "hike99"
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """{"error":"Not found"}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val result = fetchUserHikeCoordinatesTestable(client, baseUrl, userHikeId, token)

        // Assert
        assertNull(result)
    }
}

// Test for fetching user friends (friends of another user)
class FetchUserFriendsTest {

    private val baseUrl = "https://hopla.onrender.com/"

    // Testable variant that injects client and baseUrl
    private suspend fun fetchUserFriendsTestable(
        client: HttpClient,
        baseUrl: String,
        userId: String,
        token: String
    ): List<Friend> {
        val response: HttpResponse = client.get(baseUrl+"userrelations/friends?userid=$userId") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }
        return response.body()
    }
    // Test for fetchUserFriends with 200 OK response
    @Test
    fun `returns list of friends on 200 OK`() = runTest {
        // Arrange
        val userId = "user42"
        val token = "mockToken"
        val friends = listOf(
            Friend("f1", "Alice", "Alice", "url@url.no"),
            Friend("f2", "Bob", "Bob", "url@url.no")
        )
        val jsonText = Json.encodeToString(ListSerializer(Friend.serializer()), friends)

        val mockEngine = MockEngine { requestData ->
            // Sjekk at URL og header er korrekte
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals(baseUrl+"userrelations/friends?userid=$userId", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchUserFriendsTestable(client, baseUrl, userId, token)

        // Assert
        assertEquals(friends, actual)
    }
    // Test for fetchUserFriends with 404 response
    @Test
    fun `returns empty list on non-OK status`() = runTest {
        // Arrange
        val userId = "user99"
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val result = fetchUserFriendsTestable(client, baseUrl, userId, token)

        // Assert
        assertEquals(emptyList<Friend>(), result)
    }
}

// Test for fetching trails
class FetchTrailsTest {

    private val baseUrl = "https://hopla.onrender.com/"
    // Test for fetchTrails with 200 OK response
    @Test
    fun `fetchTrailsTestable returns TrailsResponse on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val pageNumber = 1
        val searchQuery = "mountain"
        val filtersQuery = "difficulty:easy"
        val trails = listOf(
            Trail("t1", "Mountain View", "description1", "url1", 1, true),
            Trail("t2", "River Side", "description2", "url2", 2, false)
        )
        val responseObj = TrailsResponse(trails = trails, pageNumber = pageNumber)
        val jsonText = Json.encodeToString(TrailsResponse.serializer(), responseObj)

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, and header
            val expectedUrl = "${baseUrl}trails/all?search=$searchQuery&pageNumber=$pageNumber&filter=${filtersQuery}"
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals(expectedUrl, requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsTestable(client, baseUrl, token, pageNumber, searchQuery, filtersQuery)

        // Assert
        assertEquals(responseObj, actual)
    }
    // Test for fetchTrails with 500 response
    @Test
    fun `fetchTrailsTestable returns deserialized error object on 500`() = runTest {
        // Arrange
        val token = "mockToken"
        val pageNumber = 2
        val searchQuery = "forest"
        val filtersQuery = "length:short"

        // Pretend the API returns a TrailsResponse shape even on error.
        val errorResponse = TrailsResponse(trails = emptyList(), pageNumber = pageNumber)
        val jsonText = Json.encodeToString(TrailsResponse.serializer(), errorResponse)

        val mockEngine = MockEngine {
            respond(
                content = jsonText,
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsTestable(client, baseUrl, token, pageNumber, searchQuery, filtersQuery)

        // Assert: returns whatever body() gives, regardless of status code
        assertEquals(errorResponse, actual)
    }

    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchTrailsTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        pageNumber: Int,
        searchQuery: String,
        filtersQuery: String
    ): TrailsResponse {
        val url = URLBuilder(baseUrl).apply {
            path("trails", "all")
            parameters.append("search", searchQuery)
            parameters.append("pageNumber", pageNumber.toString())
            encodedParameters.append("filter", filtersQuery)
        }.buildString()

        val response: HttpResponse = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        return response.body()
    }
}

// Test for fetching trails by location
class FetchTrailsByLocationTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Test for fetchTrailsByLocation with 200 OK response
    @Test
    fun `returns TrailsResponse on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val lat = 59.91
        val lon = 10.75
        val page = 3
        val trails = listOf(
            Trail("t1", "Hilltop", "Description1", "Url1", 1, true),
            Trail("t2", "Lakeside", "Description2", "Url2", 2, false)
        )
        val expected = TrailsResponse(trails = trails, pageNumber = page)
        val jsonText = Json.encodeToString(TrailsResponse.serializer(), expected)

        val mockEngine = MockEngine { requestData ->
            // Verify that URL and header are correct
            assertEquals(
                "$baseUrl/trails/list?latitude=$lat&longitude=$lon&pageNumber=$page",
                requestData.url.toString()
            )
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])
            assertEquals(HttpMethod.Get, requestData.method)

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsByLocationTestable(client, baseUrl, token, lat, lon, page)

        // Assert
        assertEquals(expected, actual)
    }
    // Test for fetchTrailsByLocation with 404 response
    @Test
    fun `returns empty list when 404 Not Found`() = runTest {
        // Arrange
        val token = "mockToken"
        val lat = 0.0
        val lon = 0.0
        val page = 1

        val mockEngine = MockEngine {
            respond(
                content = """{"trails":[],"pageNumber":$page}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsByLocationTestable(client, baseUrl, token, lat, lon, page)

        assertEquals(emptyList<Trail>(), actual.trails)
        assertEquals(page, actual.pageNumber)
    }

    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchTrailsByLocationTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        latitude: Double,
        longitude: Double,
        pageNumber: Int
    ): TrailsResponse {
        val response: HttpResponse = client.get(
            "$baseUrl/trails/list?latitude=$latitude&longitude=$longitude&pageNumber=$pageNumber"
        ) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching favorite trails
class FetchFavoriteTrailsTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Test for fetchFavoriteTrails to return favorites on 200 OK
    @Test
    fun `fetchFavoriteTrailsTestable returns favorites on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val favorites = listOf(
            Trail("t1", "Mountain Loop", "Description1", "Url1", 1, true),
            Trail("t2", "River Walk", "Description2", "Url2", 2, false)
        )
        val expected = TrailsResponse(trails = favorites, pageNumber = 1)
        val jsonText = Json.encodeToString(TrailsResponse.serializer(), expected)

        val mockEngine = MockEngine { requestData ->
            // Verify that URL and header are correct
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/trails/favorites", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFavoriteTrailsTestable(client, baseUrl, token)

        // Assert
        assertEquals(expected, actual)
    }
    // Test for fetchFavoriteTrails to return empty list on 404
    @Test
    fun `fetchFavoriteTrailsTestable returns empty list on non-OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val mockEngine = MockEngine {
            respond(
                content = """{"trails":[]}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFavoriteTrailsTestable(client, baseUrl, token)

        // Assert
        // Deserialize [] to TrailsResponse(trails = [], pageNumber=1)
        assertEquals(emptyList<Trail>(), actual.trails)
        assertEquals(0, actual.pageNumber)
    }
    // Testable variant
    private suspend fun fetchFavoriteTrailsTestable(
        client: HttpClient,
        baseUrl: String,
        token: String
    ): TrailsResponse {
        val response: HttpResponse = client.get("$baseUrl/trails/favorites") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching trails relations / trails friends and following have shared
class FetchTrailsRelationsTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Test for fetchTrailsRelations to return trails on 200 OK
    @Test
    fun `returns TrailsResponse on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val page = 5
        val trails = listOf(
            Trail("t1", "Valley Path", "Description1", "Url1", 1, true),
            Trail("t2", "Forest Loop", "Description2", "Url2", 2, false)
        )
        val expected = TrailsResponse(trails = trails, pageNumber = page)
        val jsonText = Json.encodeToString(TrailsResponse.serializer(), expected)

        val mockEngine = MockEngine { requestData ->
            // Verify URL, method, header
            assertEquals(
                "$baseUrl/trails/relations?friends=true&following=true&pagenumber=$page",
                requestData.url.toString()
            )
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsRelationsTestable(client, baseUrl, token, page)

        // Assert
        assertEquals(expected, actual)
    }
    // Test for throwing exception on non-OK status
    @Test
    fun `throws exception on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"
        val page = 3

        val mockEngine = MockEngine {
            respond(
                content = """{"error":"Something went wrong"}""",
                status = HttpStatusCode.BadGateway,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act & Assert
        assertFailsWith<Exception> {
            fetchTrailsRelationsTestable(client, baseUrl, token, page)
        }
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchTrailsRelationsTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        pageNumber: Int
    ): TrailsResponse {
        val response: HttpResponse = client.get(
            "$baseUrl/trails/relations?friends=true&following=true&pagenumber=$pageNumber"
        ) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Client request (${response.request.url}) failed: ${response.status}.")
        }
    }
}

// Test for fetching trails on map
class FetchTrailsOnMapTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Test for fetchTrailsOnMap to return list of MapTrail on 200 OK
    @Test
    fun `returns list of MapTrail on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val lat = 59.91
        val lon = 10.75
        val zoom = 12
        val trails = listOf(
            MapTrail("m1", "Loop", 59.91, 10.75),
            MapTrail("m2", "Valley", 59.92, 10.76)
        )
        val jsonText = Json.encodeToString(ListSerializer(MapTrail.serializer()), trails)

        val mockEngine = MockEngine { requestData ->
            // Verify URL and header
            assertEquals(
                "$baseUrl/trails/map?latitude=$lat&longitude=$lon&zoomlevel=$zoom",
                requestData.url.toString()
            )
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsOnMapTestable(client, baseUrl, token, lat, lon, zoom)

        // Assert
        assertEquals(trails, actual)
    }
    // Test for fetchTrailsOnMap to return empty list on non-OK status
    @Test
    fun `returns empty list on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"
        val lat = 0.0
        val lon = 0.0
        val zoom = 1

        val mockEngine = MockEngine {
            respond(
                content = """{"title":"Error","errors":["Bad"]}""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailsOnMapTestable(client, baseUrl, token, lat, lon, zoom)

        // Assert
        assertEquals(emptyList<MapTrail>(), actual)
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchTrailsOnMapTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        latitude: Double,
        longitude: Double,
        zoomLevel: Int
    ): List<MapTrail> {
        val response: HttpResponse = client.get(
            "$baseUrl/trails/map?latitude=$latitude&longitude=$longitude&zoomlevel=$zoomLevel"
        ) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            emptyList()
        }
    }
}

// Test for fetching trail updates
class FetchTrailUpdatesTest {

    private val baseUrl = "https://hopla.onrender.com"

    @Test
    fun `returns list of updates on 200 OK`() = runTest {
        // Arrange
        val trailId = "trail42"
        val page = 1
        val token = "mockToken"
        val updates = listOf(
            TrailUpdate("u1", "New marker added", "url1", 5, "2025-04-01T10:00:00Z", "Alias1"),
            TrailUpdate("u2", "Route cleared", "url2", 1, "2025-04-02T12:30:00Z", "Alias2")
        )
        val jsonText = Json.encodeToString(ListSerializer(TrailUpdate.serializer()), updates)

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, and header
            assertEquals(HttpMethod.Get, requestData.method)
            val url = requestData.url
            assertEquals("/trails/updates", url.encodedPath)
            assertEquals(trailId, url.parameters["trailId"])
            assertEquals(page.toString(), url.parameters["pageNumber"])
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailUpdatesTestable(client, baseUrl, trailId, page, token)

        // Assert
        assertEquals(updates, actual)
    }
    // Test for fetchTrailUpdates with 404 response
    @Test
    fun `returns empty list on non-OK status`() = runTest {
        // Arrange
        val trailId = "trail99"
        val page = 2
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailUpdatesTestable(client, baseUrl, trailId, page, token)

        // Assert: deserialiserer empty list from JSON
        assertEquals(emptyList<TrailUpdate>(), actual)
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchTrailUpdatesTestable(
        client: HttpClient,
        baseUrl: String,
        trailId: String,
        pageNumber: Int,
        token: String
    ): List<TrailUpdate> {
        val response: HttpResponse = client.get("$baseUrl/trails/updates") {
            parameter("trailId", trailId)
            parameter("pageNumber", pageNumber)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching trail filters
class FetchTrailFiltersTest {

    private val baseUrl = "https://hopla.onrender.com"

    // Test for fetchTrailFilters with 200 OK response
    @Test
    fun `fetchTrailFiltersTestable returns list of filters on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val filters = listOf(
            TrailFilter("123", "f1", "f1", "Lenght", listOf("Short"), Json.parseToJsonElement("\"Short\"")),
            TrailFilter("234", "f2", "f2", "Difficulty", listOf("Easy"), Json.parseToJsonElement("\"Easy\""))
        )
        val jsonText = Json.encodeToString(ListSerializer(TrailFilter.serializer()), filters)

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, and headers
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/trailfilters/all", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])
            assertEquals(ContentType.Application.Json.toString(),
                requestData.headers[HttpHeaders.ContentType])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
            }
        }

        // Act
        val actual = fetchTrailFiltersTestable(client, baseUrl, token)

        // Assert
        assertEquals(filters, actual)
    }
    // Test for fetchTrailFilters with 500 response
    @Test
    fun `fetchTrailFiltersTestable returns empty list on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.BadGateway,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchTrailFiltersTestable(client, baseUrl, token)

        // Assert: non-OK still deserializes JSON array
        assertEquals(emptyList<TrailFilter>(), actual)
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchTrailFiltersTestable(
        client: HttpClient,
        baseUrl: String,
        token: String
    ): List<TrailFilter> {
        val response: HttpResponse = client.get("$baseUrl/trailfilters/all") {
            header("Authorization", "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
        return response.body()
    }
}

// Test for fetching all users
class FetchAllUsersTest {

    private val url = "https://hopla.onrender.com/users/all"
    // Test for fetchAllUsers with 200 OK response
    @Test
    fun `returns list of OtherUsers on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val users = listOf(
            OtherUsers("u1", "alice", "pic1", "alice"),
            OtherUsers("u2", "bob", "pic2", "bob")
        )
        val jsonText = Json.encodeToString(ListSerializer(OtherUsers.serializer()), users)

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals(url, requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchAllUsersTestable(client, url, token)

        // Assert
        assertEquals(users, actual)
    }
    // Test for fetchAllUsers with 404 response
    @Test
    fun `returns empty list on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchAllUsersTestable(client, url, token)

        // Assert: 404 still deserializes JSON array
        assertEquals(emptyList<OtherUsers>(), actual)
    }
    // Testable variant that accepts HttpClient and URL
    private suspend fun fetchAllUsersTestable(
        client: HttpClient,
        url: String,
        token: String
    ): List<OtherUsers> {
        val response: HttpResponse = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching user relation requests
class FetchUserRelationRequestsTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Test for fetchUserRelationRequests with 200 OK response
    @Test
    fun `returns list of requests on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val requests = listOf(
            UserRelationRequest("u1", "u2", "Alice", "Alice"),
            UserRelationRequest("u2", "u1", "Bob", "Bob")
        )
        val jsonText = Json.encodeToString(
            ListSerializer(UserRelationRequest.serializer()),
            requests
        )

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/userrelations/requests", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
            }
        }

        // Act
        val actual = fetchUserRelationRequestsTestable(client, baseUrl, token)

        // Assert
        assertEquals(requests, actual)
    }
    // Test for fetchUserRelationRequests with 404 response
    @Test
    fun `returns empty list on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
            }
        }

        // Act
        val actual = fetchUserRelationRequestsTestable(client, baseUrl, token)

        // Assert: even on 404, deserializes [] into empty list
        assertEquals(emptyList<UserRelationRequest>(), actual)
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchUserRelationRequestsTestable(
        client: HttpClient,
        baseUrl: String,
        token: String
    ): List<UserRelationRequest> {
        val response: HttpResponse = client.get("$baseUrl/userrelations/requests") {
            header("Authorization", "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching friends
class FetchFriendsTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Test for fetchFriends with 200 OK response
    @Test
    fun `fetchFriendsTestable returns list of friends on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val friends = listOf(
            Friend("f1", "Alice", "Alice", "url1"),
            Friend("f2", "Bob", "Bob", "url2")
        )
        val jsonText = Json.encodeToString(ListSerializer(Friend.serializer()), friends)

        val mockEngine = MockEngine { requestData ->
            // Verify GET URL, method, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/userrelations/friends", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFriendsTestable(client, baseUrl, token)

        // Assert
        assertEquals(friends, actual)
    }
    // Test for fetchFriends with 400 response
    @Test
    fun `fetchFriendsTestable returns empty list on non-OK`() = runTest {
        // Arrange
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFriendsTestable(client, baseUrl, token)

        // Assert: non-OK still deserializes JSON array into empty list
        assertEquals(emptyList<Friend>(), actual)
    }
    private suspend fun fetchFriendsTestable(
        client: HttpClient,
        baseUrl: String,
        token: String
    ): List<Friend> {
        val response: HttpResponse = client.get("$baseUrl/userrelations/friends") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching following
class FetchFollowingTest {

    private val baseUrl = "https://hopla.onrender.com"

    // Test for fetchFollowing with 200 OK response
    @Test
    fun `returns list of following on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val followingList = listOf(
            Following("u1", "Alice", "Alice", "pic1"),
            Following("u2", "Bob", "Bob", "pic2")
        )
        val jsonText = Json.encodeToString(ListSerializer(Following.serializer()), followingList)

        val mockEngine = MockEngine { requestData ->
            // Verify GET URL, method, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/userrelations/following", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFollowingTestable(client, baseUrl, token)

        // Assert
        assertEquals(followingList, actual)
    }
    // Test for fetchFollowing with 400 response
    @Test
    fun `returns empty list on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFollowingTestable(client, baseUrl, token)

        // Assert: even on 400, deserializes [] into empty list
        assertEquals(emptyList<Following>(), actual)
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchFollowingTestable(
        client: HttpClient,
        baseUrl: String,
        token: String
    ): List<Following> {
        val response: HttpResponse = client.get("$baseUrl/userrelations/following") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching friend profile
class FetchFriendProfileTest {

    private val baseUrl = "https://hopla.onrender.com"

    // Test for fetchFriendProfile with 200 OK response
    @Test
    fun `returns FriendProfile on 200 OK`() = runTest {
        // Arrange
        val userId = "user123"
        val token = "mockToken"
        val expectedProfile = FriendProfile(
            id = userId,
            name = "Bob",
            pictureUrl = "url1",
            alias = "Bobern",
            description = "Bob's profile",
            dob = "1990-01-01",
            createdAt = "2023-01-01T00:00:00Z",
            friendsCount = 10,
            horseCount = 5,
            relationStatus = "FRIEND"
        )
        val jsonText = Json.encodeToString(FriendProfile.serializer(), expectedProfile)

        val mockEngine = MockEngine { requestData ->
            // Verify method, URL, parameters, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("/users/profile", requestData.url.encodedPath)
            assertEquals(userId, requestData.url.parameters["userId"])
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFriendProfileTestable(client, baseUrl, userId, token)

        // Assert
        assertEquals(expectedProfile, actual)
    }
    // Test for fetchFriendProfile with 404 response
    @Test
    fun `throws on non-OK status`() = runTest {
        // Arrange
        val userId = "missing"
        val token = "mockToken"

        val mockEngine = MockEngine {
            respond(
                content = """{}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act & Assert
        assertFailsWith<JsonConvertException> {
            fetchFriendProfileTestable(client, baseUrl, userId, token)
        }
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchFriendProfileTestable(
        client: HttpClient,
        baseUrl: String,
        userId: String,
        token: String
    ): FriendProfile {
        val response: HttpResponse = client.get("$baseUrl/users/profile") {
            parameter("userId", userId)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        return response.body()
    }
}

// Test for fetching stables
class FetchStablesTest {
    private val baseUrl = "https://hopla.onrender.com"

    private val req = FetchStableRequest(
        token      = "mockToken",
        search     = "farm",
        userId     = "user1",
        latitude   = 59.9,
        longitude  = 10.7,
        pageNumber = 1,
    )
    // Test for fetchStables with 200 OK response
    @Test
    fun `returns list of stables on 200 OK`() = runTest {
        // Arrange
        val stables = listOf(
            Stable("s1","Red Barn",59.9, true, "url1"),
            Stable("s2","Green Farm",59.8, false, "url2")
        )
        val jsonText = Json.encodeToString(ListSerializer(Stable.serializer()), stables)

        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(
                "$baseUrl/stables/all?search=${req.search}&userid=${req.userId}" +
                        "&latitude=${req.latitude}&longitude=${req.longitude}&pagenumber=${req.pageNumber}",
                request.url.toString()
            )
            assertEquals("Bearer ${req.token}", request.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchStablesTestable(client, baseUrl, req)

        // Assert
        assertEquals(stables, actual)
    }
    // Test for fetchStables with 404 response
    @Test
    fun `returns empty list on non-OK`() = runTest {
        // Arrange
        val mockEngine = MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.BadGateway,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchStablesTestable(client, baseUrl, req)

        // Assert
        assertEquals(emptyList<Stable>(), actual)
    }
    private suspend fun fetchStablesTestable(
        client: HttpClient,
        baseUrl: String,
        req: FetchStableRequest
    ): List<Stable> {
        val url = "$baseUrl/stables/all" +
                "?search=${req.search}" +
                "&userid=${req.userId}" +
                "&latitude=${req.latitude}" +
                "&longitude=${req.longitude}" +
                "&pagenumber=${req.pageNumber}"

        val response: HttpResponse = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer ${req.token}")
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            emptyList()
        }
    }
}

// Test for fetching stable details
class FetchStableDetailsTest {

    private val baseUrl = "https://hopla.onrender.com"

    // Test for fetchStableDetails with 200 OK response
    @Test
    fun `returns StableDetails on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val stableId = "stable42"
        val expected = StableDetails(
            id = stableId,
            name = "Sunny Stables",
            description = "Valley Farm",
            pictureUrl = "url1",
            isMember = true
        )
        val jsonText = Json.encodeToString(StableDetails.serializer(), expected)

        val mockEngine = MockEngine { requestData ->
            // verify URL, method, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/stables/$stableId", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 180_000
                connectTimeoutMillis = 180_000
                socketTimeoutMillis = 180_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchStableDetailsTestable(client, baseUrl, token, stableId)

        // Assert
        assertEquals(expected, actual)
    }
    // Test cases for exception handling
    @Test
    fun `returns null on exception or non-OK status`() = runTest {
        val token = "mockToken"
        val stableId = "stable99"

        // Case 1: non-OK status
        val mockEngine404 = MockEngine {
            respond(
                content = """{"error":"Not Found"}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client404 = HttpClient(mockEngine404) {
            install(HttpTimeout) {
                requestTimeoutMillis = 180_000
                connectTimeoutMillis = 180_000
                socketTimeoutMillis = 180_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        assertNull(fetchStableDetailsTestable(client404, baseUrl, token, stableId))

        // Case 2: network exception
        val mockEngineError = MockEngine { throw RuntimeException("Network down") }
        val clientError = HttpClient(mockEngineError) {
            install(HttpTimeout) {
                requestTimeoutMillis = 180_000
                connectTimeoutMillis = 180_000
                socketTimeoutMillis = 180_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        assertNull(fetchStableDetailsTestable(clientError, baseUrl, token, stableId))
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchStableDetailsTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        stableId: String
    ): StableDetails? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/stables/$stableId") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}

// Test for fetching stable messages
class FetchStableMessagesTest {
    private val baseUrl = "https://hopla.onrender.com"

    // Test for fetchStableMessages with 200 OK response
    @Test
    fun `returns list of messages on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val stableId = "stable123"
        val page = 2
        val messages = listOf(
            Message("Hello", "2025-04-01T10:00:00Z", "id1", "Alice"),
            Message("Hi", "2025-04-01T10:05:00Z", "id2", "Bob")
        )
        val jsonText = Json.encodeToString(ListSerializer(Message.serializer()), messages)

        val mockEngine = MockEngine { requestData ->
            // Verify URL, method, header
            assertEquals(HttpMethod.Get, requestData.method)
            assertEquals("$baseUrl/stablemessages/$stableId?pagenumber=$page", requestData.url.toString())
            assertEquals("Bearer $token", requestData.headers[HttpHeaders.Authorization])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        // Act
        val actual = fetchStableMessagesTestable(client, baseUrl, token, stableId, page)

        // Assert
        assertEquals(messages, actual)
    }
    // Test for exception handling
    @Test
    fun `returns empty list on exception or non-OK status`() = runTest {
        val token = "mockToken"
        val stableId = "stable404"
        val page = 1

        // Non-OK status
        val client404 = HttpClient(MockEngine {
            respond(
                content = """[]""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        assertEquals(emptyList<Message>(), fetchStableMessagesTestable(client404, baseUrl, token, stableId, page))

        // Exception
        val clientError = HttpClient(MockEngine { throw RuntimeException("Network down") }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        assertEquals(emptyList<Message>(), fetchStableMessagesTestable(clientError, baseUrl, token, stableId, page))
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchStableMessagesTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        stableId: String,
        pageNumber: Int
    ): List<Message> {
        val url = "$baseUrl/stablemessages/$stableId?pagenumber=$pageNumber"
        return try {
            val response: HttpResponse = client.get(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

// Test for fetching feed
class FetchFeedTest {

    private val baseUrl = "https://hopla.onrender.com"

    // Test for fetchFeedTestable with 200 OK response
    @Test
    fun `fetchFeedTestable returns FeedResponse on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val page = 1
        val items = listOf(
            FeedItem("i1","trail","Nice trail", "description1", "url1", "action1", "12/5/2025", "uid1", "Bob", 20.1, "pic1", 2, true),
            FeedItem("i2","horse","Fast horse", "description2", "url2", "action2", "12/6/2025", "uid2", "Alice", 1.1, "pic2", 3, false)
        )
        val expected = FeedResponse(
            totalCount = 10,
            pageNumber = page,
            pageSize = 10,
            hasNextPage = true,
            items = items
        )
        val jsonText = Json.encodeToString(FeedResponse.serializer(), expected)

        val mockEngine = MockEngine { request ->
            // Verify URL parameters
            assertEquals(HttpMethod.Get, request.method)
            val url = request.url
            assertEquals("/feed/all", url.encodedPath)
            assertEquals("1", url.parameters["pageNumber"])
            assertEquals("userhikes,trails,trailreviews,horses", url.parameters["show"])
            // default flags are false, no onlyFriendsAndFollowing etc.
            assertEquals("Bearer $token", request.headers[HttpHeaders.Authorization])
            assertEquals(ContentType.Application.Json.toString(),
                request.headers[HttpHeaders.ContentType])

            respond(
                content = jsonText,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType,
                    ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFeedTestable(client, baseUrl, token, page)

        // Assert
        assertEquals(expected, actual)
    }
    // Test for fetchFeedTestable with 500 response
    @Test
    fun `fetchFeedTestable returns null on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"
        val page = 2

        val mockEngine = MockEngine {
            respond(
                content = """{"items":[],"pageNumber":2}""",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType,
                    ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Act
        val actual = fetchFeedTestable(client, baseUrl, token, page)

        // Assert
        assertNull(actual)
    }
    // Testable variant that accepts HttpClient and baseUrl
    private suspend fun fetchFeedTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        pageNumber: Int,
        onlyFriendsAndFollowing: Boolean = false,
        onlyLikedTrails: Boolean = false,
        latitude: Double? = null,
        longitude: Double? = null,
        sortByLikes: Boolean = false
    ): FeedResponse? {
        val url = URLBuilder(baseUrl).apply {
            path("feed", "all")
            parameters.append("show", "userhikes,trails,trailreviews,horses")
            parameters.append("pageNumber", pageNumber.toString())
            if (onlyFriendsAndFollowing) parameters.append("onlyFriendsAndFollowing", "true")
            if (onlyLikedTrails)       parameters.append("onlyLikedTrails", "true")
            if (latitude != null && longitude != null) {
                parameters.append("userlat", latitude.toString())
                parameters.append("userlong", longitude.toString())
                parameters.append("radius", "20")
            }
            if (sortByLikes)           parameters.append("sort", "likes")
        }.buildString()

        val response: HttpResponse = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            null
        }
    }
}
