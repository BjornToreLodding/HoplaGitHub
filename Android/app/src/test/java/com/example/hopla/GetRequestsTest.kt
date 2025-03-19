package com.example.hopla

import com.example.hopla.apiService.fetchHorses
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

class GetRequestsTest {

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