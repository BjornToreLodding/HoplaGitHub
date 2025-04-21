package com.example.hopla

import com.example.hopla.apiService.fetchHorses
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

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
        val mockEngine = MockEngine { request ->
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
        val mockEngine = MockEngine { request ->
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        val client = HttpClient(mockEngine) {
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