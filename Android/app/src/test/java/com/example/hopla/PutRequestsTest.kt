package com.example.hopla

import com.example.hopla.universalData.ChangePasswordResponse
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.UserRelationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.ktor.client.request.forms.formData
import io.ktor.http.content.PartData

// Class to test changin the profile picture of the logged in user
class UploadProfilePictureTest {

    @Test
    fun testUploadProfilePictureReturnsExpectedResponse() = runTest {
        // Arrange
        val token = "mockToken"
        val userId = "user123"
        val fakeImageBytes = ByteArray(10) { 0x1 } // dummy image data
        val expectedResponse = "Profile picture uploaded"

        val mockEngine = MockEngine { _ ->
            // Optional: assert that it's a PUT with multipart form
            respond(
                content = expectedResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
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
        val actualResponse = uploadProfilePictureTestable(client, token, userId, fakeImageBytes)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    // Function to upload the profile picture in secure way
    private suspend fun uploadProfilePictureTestable(
        client: HttpClient,
        token: String,
        userId: String,
        imageBytes: ByteArray,
        url: String = "https://hopla.onrender.com/upload"
    ): String {
        val response: HttpResponse = client.put(url) {
            header("Authorization", "Bearer $token")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                        })
                        append("table", "Users")
                        append("entityId", userId)
                    }
                )
            )
        }

        return response.bodyAsText()
    }

}

// Class to test changing the password of the logged in user
class ChangePasswordTest {

    @Test
    fun testChangePasswordReturnsCorrectMessageAndStatus() = runTest {
        // Arrange
        val token = "mockToken"
        val oldPass = "old123"
        val newPass = "new456"
        val confirmPass = "new456"
        val expectedMessage = "Password changed successfully"

        val mockEngine = MockEngine { _ ->
            respond(
                content = """{ "message": "$expectedMessage" }""",
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
        val (statusCode, message) = changePasswordTestable(
            client,
            token,
            oldPass,
            newPass,
            confirmPass
        )

        // Assert
        assertEquals(200, statusCode)
        assertEquals(expectedMessage, message)
    }
    private suspend fun changePasswordTestable(
        client: HttpClient,
        token: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        url: String = "https://hopla.onrender.com/users/change-password"
    ): Pair<Int, String> {
        val response: HttpResponse = client.put(url) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "OldPassword" to oldPassword,
                    "NewPassword" to newPassword,
                    "ConfirmPassword" to confirmPassword
                )
            )
        }

        val responseBody: String = response.bodyAsText()
        val message = Json.decodeFromString<ChangePasswordResponse>(responseBody).message
        return response.status.value to message
    }

}

// Class to test updating the user information
class UpdateUserInfoTest {

    @Serializable
    data class UpdateUserResponse(val message: String)

    @Test
    fun testUpdateUserInfoReturnsSuccess() = runTest {
        // Arrange
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"message": "User updated successfully"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
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

        val result = updateUserInfoTestable(
            client = client,
            token = "mock-token",
            alias = "newAlias",
            name = "New Name",
            phone = "12345678",
            description = "Updated bio",
            year = 1990,
            month = 5,
            day = 10
        )

        println("Response: $result")

        // Assert
        assertEquals(200, result.first)
        assertEquals("User updated successfully", result.second)
    }

    @Test
    fun testUpdateUserInfoHandlesTimeout() = runTest {
        val client = HttpClient(MockEngine { throw ConnectTimeoutException("Timeout") }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val result = updateUserInfoTestable(
            client,
            token = "mock-token",
            alias = "alias",
            name = "Name"
        )

        assertEquals(-1, result.first)
        assert(result.second.contains("Connection timeout"))
    }

    private suspend fun updateUserInfoTestable(
        client: HttpClient,
        url: String = "https://hopla.onrender.com/users/update",
        token: String,
        alias: String,
        name: String,
        phone: String? = null,
        description: String? = null,
        password: String? = null,
        year: Int? = null,
        month: Int? = null,
        day: Int? = null
    ): Pair<Int, String> {
        val requestBody = buildJsonObject {
            put("Alias", alias)
            put("Name", name)
            phone?.let { put("Telephone", it) }
            description?.let { put("Description", it) }
            password?.let { put("Password", it) }
            year?.let { put("Year", it.toString()) }
            month?.let { put("Month", it.toString()) }
            day?.let { put("Day", it.toString()) }
        }

        return try {
            val response: HttpResponse = client.put(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            val body = response.body<UpdateUserResponse>()
            response.status.value to body.message
        } catch (e: ConnectTimeoutException) {
            -1 to "Connection timeout: ${e.message}"
        } catch (e: Exception) {
            -1 to "An error occurred: ${e.message}"
        }
    }
}

// Class to test sending user relation requests
class SendUserRelationRequestPutTest {

    private val baseUrl = "https://example.com/api/"

    @Test
    fun `sendUserRelationRequestPutTestable returns deserialized response on success`() = runTest {
        // Arrange
        val token = "mockToken"
        val request = UserRelationChangeRequest(
            targetUserId = "user123",
            status = "block"
        )
        val expectedResponse = UserRelationResponse(
            status = true.toString(),
            message = "User relation updated"
        )
        val mockEngine = MockEngine { call ->
            // Verify request method and URL
            assertEquals(HttpMethod.Put, call.method)
            assertEquals("${baseUrl}userrelations", call.url.toString())
            assertEquals("Bearer $token", call.headers[HttpHeaders.Authorization])

            respond(
                content = Json.encodeToString(expectedResponse),
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
        val actual = sendUserRelationRequestPutTestable(client, baseUrl, token, request)

        // Assert
        assertEquals(expectedResponse, actual)
    }

    @Test
    fun `sendUserRelationRequestPutTestable propagates error on non-2xx`() = runTest {
        // Arrange
        val token = "mockToken"
        val request = UserRelationChangeRequest("user123", "unfriend")
        val errorMessage = """{"success":false,"message":"Not authorized"}"""
        val mockEngine = MockEngine {
            respond(
                content = errorMessage,
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
            }
        }

        // Act
        val actual = sendUserRelationRequestPutTestable(client, baseUrl, token, request)

        // Assert
        assertEquals("Not authorized", actual.message)
    }

    private suspend fun sendUserRelationRequestPutTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        request: UserRelationChangeRequest
    ): UserRelationResponse {
        val response: HttpResponse = client.put(baseUrl + "userrelations") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val responseBody: String = response.bodyAsText()
        return Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
            .decodeFromString(UserRelationResponse.serializer(), responseBody)
    }
}

// Class to test updating user hikes
class UpdateUserHikeTest {

    @Test
    fun testUpdateUserHikeReturnsExpectedResponse() = runTest {
        // Arrange
        val token = "mockToken"
        val userHikeId = "hike123"
        val fakeImageBytes = ByteArray(10) { 0x1 }
        val title = "Sunset Trail"
        val horseId = "horse987"
        val description = "Great ride"
        val expectedResponse = "Hike updated"

        val mockEngine = MockEngine { _ ->
            respond(
                content = expectedResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
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
        val actualResponse = updateUserHikeTestable(
            client = client,
            baseUrl = "https://hopla.onrender.com",
            token = token,
            userHikeId = userHikeId,
            title = title,
            horseId = horseId,
            imageBytes = fakeImageBytes,
            description = description
        )

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    private suspend fun updateUserHikeTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        userHikeId: String,
        title: String? = null,
        horseId: String? = null,
        imageBytes: ByteArray? = null,
        description: String? = null
    ): String {
        val form = formData {
            title?.let { append("Title", it) }
            horseId?.let { append("HorseId", it) }
            description?.let { append("Description", it) }
            imageBytes?.let {
                append("Image", it, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                })
            }
        }

        val response: HttpResponse = client.put("$baseUrl/userhikes/$userHikeId") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(MultiPartFormDataContent(form))
        }

        return response.bodyAsText()
    }
}
