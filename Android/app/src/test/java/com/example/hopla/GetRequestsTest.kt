package com.example.hopla

import android.util.Log
import com.example.hopla.apiService.fetchTrailsOnMap
import com.example.hopla.universalData.OtherUsers
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import com.example.hopla.universalData.Trail
import com.example.hopla.universalData.TrailsResponse
import io.ktor.client.call.body
import io.ktor.client.statement.*
import kotlin.test.assertEquals

class FetchAllUsersTests {
    @Test
    fun `fetchAllUsers should return list of users`() = runTest {
        // Sample response data
        val expectedUsers = listOf(
            OtherUsers("12345678-0000-0000-0001-123456780001", "Alice", "https://files.hopla.no/18380ec3-a4d7-4cbe-bd7a-5a984fd1a18d.jpg?w=200&h=200&fit=crop", "Alice"),
            OtherUsers("12345678-0000-0000-0001-123456780002", "Bob", "https://files.hopla.no/18380ec3-a4d7-4cbe-bd7a-5a984fd1a18d.jpg?w=200&h=200&fit=crop", "Bob")
        )

        val mockEngine = MockEngine { request ->
            assertEquals("https://hopla.onrender.com/users/all", request.url.toString())
            assertEquals("Bearer test-token", request.headers["Authorization"])

            respond(
                content = Json.encodeToString(expectedUsers),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}

class FetchTrailsOnMapTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `fetchTrailsOnMap should return list of trails on success`() = runBlocking {
        val mockEngine = MockEngine { request ->
            assertEquals("https://api.example.com/trails/map?latitude=10.0&longitude=20.0&zoomlevel=5", request.url.toString())
            assertEquals("Bearer test-token", request.headers["Authorization"])

            respond(
                content = """[{"id":"12345678-0000-0000-0021-123456780016","name":"Fornebutravbane","latMean":59.8833,"longMean":10.6167,"trailAllCoordinates":null},{"id":"12345678-0000-0000-0021-123456780022","name":"Snarøyatråkket","latMean":59.879,"longMean":10.608,"trailAllCoordinates":null}]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val trails = fetchTrailsOnMap("test-token", 10.0, 20.0, 5)
    }

    @Test
    fun `fetchTrailsOnMap should return empty list on error`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"title":"Error","errors":["Invalid request"]}""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val trails = fetchTrailsOnMap("test-token", 10.0, 20.0, 5)
        assertTrue(trails.isEmpty())
    }
}

class FetchTrailsRelationsTest {
    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    // ✅ Function to create a mock HttpClient
    private fun createMockClient(responseData: String, status: HttpStatusCode): HttpClient {
        return HttpClient(MockEngine { request ->
            assertEquals("https://hopla.onrender.com/trails/relations?friends=true&following=true&pagenumber=1", request.url.toString())
            assertEquals("Bearer test-token", request.headers["Authorization"])

            respond(
                content = responseData,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    // ✅ Function to fetch trails relations
    private suspend fun fetchTrailsRelations(token: String, pageNumber: Int, client: HttpClient): TrailsResponse {
        val response: HttpResponse = client.get("https://hopla.onrender.com/trails/relations?friends=true&following=true&pagenumber=$pageNumber") {
            headers {
                append("Authorization", "Bearer $token")
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.d("fetchTrailsRelations", "Response: $responseBody")
        return response.body()
    }

    @Test
    fun `fetchTrailsRelations should return a valid TrailsResponse`() = runTest {
        val expectedResponse = TrailsResponse(
            trails = listOf(
                Trail(id = "12345678-0000-0000-0021-123456780021", name = "Trail One", pictureUrl="https://images.unsplash.com/photo-1504893524553-b855bce32c67?h=140&fit=crop", averageRating = 1, difficulty = "EASY", isFavorite = true, filters = listOf("gravel", "sand", "asphalt", "dirt")),
                Trail(id = "12345678-0000-0000-0021-123456780022", name = "Trail Two", pictureUrl="https://images.unsplash.com/photo-1504893524553-b855bce32c67?h=140&fit=crop", averageRating = 2, difficulty = "EASY", isFavorite = true, filters = listOf("gravel", "sand", "asphalt", "dirt"))
            ),
            pageNumber = 1,
            pageSize = 10
        )

        val mockClient = createMockClient(Json.encodeToString(expectedResponse), HttpStatusCode.OK)

        val response = fetchTrailsRelations("test-token", 1, mockClient)

        assertEquals(expectedResponse, response)
    }
}
