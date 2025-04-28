package com.example.hopla

import com.example.hopla.universalData.ReactionRequest
import com.example.hopla.universalData.StableActionRequest
import com.example.hopla.universalData.StableResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import io.ktor.client.request.setBody
import kotlinx.serialization.encodeToString

// Test of delete requests in relation to horses
class HorseServiceTest {

    @Test
    fun `deleteHorse should return expected response`() = runTest {
        // Arrange
        val expectedResponse = "Horse deleted successfully"
        val horseId = "123"
        val token = "test_token"

        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Delete, request.method)
            assertEquals("http://hopla.onrender.com/horses/delete/$horseId", request.url.toString())
            assertEquals("Bearer $token", request.headers["Authorization"])

            respond(
                content = expectedResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        // Act
        val response = deleteHorseTestable(client, token, horseId)

        // Assert
        assertEquals(expectedResponse, response)
    }

    // A testable version of the function with client as a parameter
    private suspend fun deleteHorseTestable(client: HttpClient, token: String, horseId: String): String {
        val url = "http://hopla.onrender.com/horses/delete/$horseId"
        var response: HttpResponse = client.delete(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.TemporaryRedirect) {
            val redirectUrl = response.headers[HttpHeaders.Location]
            if (redirectUrl != null) {
                response = client.delete(redirectUrl) {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                }
            }
        }

        return response.bodyAsText()
    }
}

// Test of delete requests in relation to user relations
class RelationServiceTest {

    @Serializable
    data class UserRelationChangeRequest(
        val userId: String,
        val relationType: String
    )

    @Test
    fun `relationRequestDelete should return expected response`() = runTest {
        // Arrange
        val expectedResponse = "Relation deleted"
        val token = "test_token"
        val request = UserRelationChangeRequest(userId = "456", relationType = "unfriend")
        val apiUrl = "https://example.com/api/" // just for the test

        val mockEngine = MockEngine { call ->
            assertEquals(HttpMethod.Delete, call.method)
            assertEquals("${apiUrl}userrelations", call.url.toString())
            assertEquals("Bearer $token", call.headers["Authorization"])

            respond(
                content = expectedResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        // Act
        val response = relationRequestDeleteTestable(client, token, request, apiUrl)

        // Assert
        assertEquals(expectedResponse, response)
    }

    // Testable version of the function with client and apiUrl as parameters
    private suspend fun relationRequestDeleteTestable(
        client: HttpClient,
        token: String,
        request: UserRelationChangeRequest,
        apiUrl: String
    ): String {
        val response: HttpResponse = client.delete(apiUrl + "userrelations") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return response.body<String>()
    }
}

// Tests for delete requests in relation to trails
class TrailServiceTest {

    @Test
    fun testRemoveFavoriteTrailReturnsSuccess() = runTest {
        // Arrange
        val expectedResponse = "Trail removed successfully"
        val mockEngine = MockEngine { request ->
            assertEquals("http://example.com/trails/favorite", request.url.toString())
            assertEquals(HttpMethod.Delete, request.method)

            respond(
                content = expectedResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        val token = "mockToken"
        val trailId = "trail123"

        // Act
        val response = removeFavoriteTrailTestable(client, "http://example.com/", token, trailId)

        // Assert
        assertEquals(expectedResponse, response)
    }

    private suspend fun removeFavoriteTrailTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        trailId: String
    ): String {
        val response: HttpResponse = client.delete(baseUrl + "trails/favorite") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(mapOf("TrailId" to trailId))
        }

        return response.bodyAsText()
    }

}

// Tests for delete requests in relation to stables
class StableServiceTest {

    @Test
    fun testLeaveStableReturnsCorrectResponse() = runTest {
        // Arrange
        val expectedResponse = StableResponse(message = "Left the stable.")
        val mockEngine = MockEngine { request ->
            assertEquals("http://example.com/stables/leave", request.url.toString())
            assertEquals(HttpMethod.Delete, request.method)

            respond(
                content = Json.encodeToString(expectedResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        val token = "mockToken"
        val request = StableActionRequest(stableId = "123") // Just an example

        // Act
        val response = leaveStableTestable(client, "http://example.com/", token, request)

        // Assert
        assertEquals(expectedResponse.message, response.message)
    }

    // Testable version of the function with client and baseUrl as parameters
    private suspend fun leaveStableTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        stableActionRequest: StableActionRequest
    ): StableResponse {
        val response: HttpResponse = client.delete(baseUrl + "stables/leave") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(stableActionRequest)
        }

        val responseBody: String = response.bodyAsText()
        return Json.decodeFromString(StableResponse.serializer(), responseBody)
    }

}

class ReactionsServiceTest {

    @Test
    fun testDeleteReactionReturnsExpectedResponse() = runTest {
        // Arrange
        val token = "mockToken"
        val entityId = "post123"
        val expectedResponse = "Reaction deleted successfully"

        val mockEngine = MockEngine { _ ->
            respond(
                content = expectedResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        // Act
        val actualResponse = deleteReactionTestable(client, token, entityId)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    // Testable version of the function with client and url as parameters
    private suspend fun deleteReactionTestable(
        client: HttpClient,
        token: String,
        entityId: String,
        url: String = "https://hopla.onrender.com/reactions"
    ): String {
        val requestBody = ReactionRequest(entityId = entityId)
        val response: HttpResponse = client.delete(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val responseBody: String = response.bodyAsText()
        return responseBody
    }

}
