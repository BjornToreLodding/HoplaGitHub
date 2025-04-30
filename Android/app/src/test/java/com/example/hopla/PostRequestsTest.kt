package com.example.hopla

import android.graphics.Bitmap
import com.example.hopla.apiService.handleLogin
import com.example.hopla.universalData.CreateTrailRequest
import com.example.hopla.universalData.HorseRequest
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.UserRelationResponse
import com.example.hopla.universalData.UserReportRequest
import com.example.hopla.universalData.UserReportResponse
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import org.json.JSONObject
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowBitmap
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import android.content.Context
import com.example.hopla.universalData.DateOfBirth
import com.example.hopla.universalData.ErrorResponse
import com.example.hopla.universalData.User
import io.ktor.client.call.body
import io.mockk.coEvery
import java.time.LocalDate

// Test for creating a user report
class CreateUserReportTest {

    private val baseUrl = "https://hopla.onrender.com"
    // Testing the creation of a user report successfully
    @Test
    fun `returns UserReportResponse on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val request = UserReportRequest(
            entityId = "123",
            entityName = "Trail",
            category = "Trail",
            message = "Bad trail"
        )
        val expectedResponse = UserReportResponse(message = "Report created")

        val jsonText = Json.encodeToString(UserReportResponse.serializer(), expectedResponse)

        val mockEngine = MockEngine { call ->
            // Assert request method, URL, and headers
            assertEquals(HttpMethod.Post, call.method)
            assertEquals("$baseUrl/userreports/create", call.url.toString())
            assertEquals("Bearer $token", call.headers[HttpHeaders.Authorization])

            // Respond with mocked JSON
            respond(
                content = jsonText,
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
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000
                connectTimeoutMillis = 120_000
                socketTimeoutMillis = 120_000
            }
        }

        // Act
        val actualResponse = createUserReportTestable(client, baseUrl, token, request)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }
    // Testing the creation of a user report with a non-OK status
    @Test
    fun `throws on non-OK status`() = runTest {
        // Arrange
        val token = "mockToken"
        val errorJson = """{"success":false,"message":"Error"}"""
        val request = UserReportRequest(
            entityId = "123",
            entityName = "Trail",
            category = "Trail",
            message = "Bad trail"
        )

        val mockEngine = MockEngine {
            respond(
                content = errorJson,
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
                json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
            }
        }

        // Act & Assert
        val actual = createUserReportTestable(client, baseUrl, token, request)

        assertEquals("Error", actual.message)
    }
    // Testable function to create a user report
    private suspend fun createUserReportTestable(
        client: HttpClient,
        baseUrl: String,
        token: String,
        request: UserReportRequest
    ): UserReportResponse {
        val response: HttpResponse = client.post("$baseUrl/userreports/create") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val bodyText = response.bodyAsText()
        return Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
            .decodeFromString(UserReportResponse.serializer(), bodyText)
    }
}

// Test for sending a user relation request
class SendUserRelationRequestTest {

    private val apiUrl = "https://hopla.onrender.com/"
    // Testable function to send a user relation request
    private suspend fun sendUserRelationRequestTestable(
        client: HttpClient,
        token: String,
        request: UserRelationChangeRequest
    ): UserRelationResponse {
        val response: HttpResponse = client.post(apiUrl + "userrelations") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val responseBody = response.bodyAsText()
        return Json.decodeFromString(UserRelationResponse.serializer(), responseBody)
    }

    // Testing the sending of a user relation request successfylly
    @Test
    fun `sendUserRelationRequest returns correct response on 200 OK`() = runTest {
        // Arrange
        val token = "mockToken"
        val request = UserRelationChangeRequest(
            targetUserId = "123",
            status = "FRIEND"
        )

        val expectedResponse = UserRelationResponse(
            message = "Follow request sent",
            status = "NA"
        )

        val jsonResponse = Json.encodeToString(UserRelationResponse.serializer(), expectedResponse)

        val mockEngine = MockEngine { call ->
            assertEquals(apiUrl + "userrelations", call.url.toString())
            assertEquals(HttpMethod.Post, call.method)
            assertEquals("Bearer $token", call.headers["Authorization"])

            respond(
                content = jsonResponse,
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

        // Act
        val actualResponse = sendUserRelationRequestTestable(client, token, request)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }
}

// Test for changing a user's email
class ChangeEmailTest {

    private val baseUrl = "https://hopla.onrender.com/"
    private val mediaType = "application/json".toMediaTypeOrNull()
    // Testable function to change a user's email
    private suspend fun changeEmailTestable(
        newEmail: String,
        password: String,
        token: String,
        mockResponse: Response
    ): String {
        val client = object : OkHttpClient() {
            override fun newCall(request: Request): Call {
                return object : Call {
                    override fun enqueue(responseCallback: Callback) {}
                    override fun isExecuted() = false
                    override fun cancel() {}
                    override fun isCanceled() = false
                    override fun clone() = this
                    override fun execute(): Response = mockResponse
                    override fun request(): Request = request
                    override fun timeout(): Timeout = Timeout.NONE
                }
            }
        }

        val requestBodyMap = mapOf(
            "NewEmail" to newEmail,
            "Password" to password
        )
        val requestBody = Gson().toJson(requestBodyMap).toRequestBody(mediaType)

        val request = Request.Builder()
            .url(baseUrl + "users/change-email")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    responseBody ?: "Success"
                } else {
                    responseBody ?: "Error: ${response.message}"
                }
            } catch (e: Exception) {
                "Exception: ${e.message}"
            }
        }
    }
    // Testing the change of email successfully
    @Test
    fun `returns success message when email change is successful`() = runTest {
        // Arrange
        val newEmail = "test@example.com"
        val password = "password123"
        val token = "mockToken"
        val successJson = "{\"message\":\"Email updated\"}"

        val mockResponse = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(successJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = changeEmailTestable(newEmail, password, token, mockResponse)

        // Assert
        assertEquals(successJson, result)
    }
    // Testing the change of email with an error
    @Test
    fun `returns error message when email change fails`() = runTest {
        // Arrange
        val newEmail = "test@example.com"
        val password = "wrongpass"
        val token = "mockToken"
        val errorJson = "{\"message\":\"Invalid password\"}"

        val mockResponse = Response.Builder()
            .code(400)
            .message("Bad Request")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(errorJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = changeEmailTestable(newEmail, password, token, mockResponse)

        // Assert
        assertEquals(errorJson, result)
    }
}

// Test for creating a horse
@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowBitmap::class])
class CreateHorseTest {

    private val baseUrl = "https://hopla.onrender.com/"
    private val mediaType = "application/json".toMediaTypeOrNull()
    // Testable function to create a horse
    private suspend fun createHorseTestable(
        token: String,
        horseRequest: HorseRequest,
        mockResponse: Response
    ): String {
        val client = object : OkHttpClient() {
            override fun newCall(request: Request): Call {
                return object : Call {
                    override fun enqueue(responseCallback: Callback) {}
                    override fun isExecuted() = false
                    override fun cancel() {}
                    override fun isCanceled() = false
                    override fun clone() = this
                    override fun execute(): Response = mockResponse
                    override fun request(): Request = request
                    override fun timeout(): Timeout = Timeout.NONE
                }
            }
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        horseRequest.image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "image", "horse.jpg",
                imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("Name", horseRequest.name)
            .addFormDataPart("Breed", horseRequest.breed)
            .addFormDataPart("Year", horseRequest.year)
            .addFormDataPart("Month", horseRequest.month)
            .addFormDataPart("Day", horseRequest.day)
            .build()

        val request = Request.Builder()
            .url(baseUrl + "horses/create")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    responseBody ?: "Success"
                } else {
                    responseBody ?: "Error: ${response.message}"
                }
            } catch (e: Exception) {
                "Exception: ${e.message}"
            }
        }
    }
    // Testing the creation of a horse successfully
    @Test
    fun `returns success message when horse is created`() = runTest {
        // Arrange
        val token = "mockToken"
        val successJson = "{\"message\":\"Horse created successfully\"}"

        // Use a placeholder or mock for the Bitmap
        val horseRequest = HorseRequest(
            name = "Test Horse",
            breed = "Breed",
            year = "2020",
            month = "01",
            day = "01",
            image = mockk(relaxed = true) // Mock the Bitmap
        )

        val mockResponse = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(successJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = createHorseTestable(token, horseRequest, mockResponse)

        // Assert
        assertEquals(successJson, result)
    }
    // Testing the creation of a horse with an error
    @Test
    fun `returns error message when horse creation fails`() = runTest {
        // Arrange
        val token = "mockToken"
        val errorJson = "{\"message\":\"Missing horse name\"}"

        // Mock Bitmap.createBitmap method
        mockkStatic(Bitmap::class) // Mock static methods of Bitmap
        val bitmapMock = mockk<Bitmap>(relaxed = true) // Create a relaxed mock for Bitmap
        every { Bitmap.createBitmap(any(), any(), any()) } returns bitmapMock

        val horseRequest = HorseRequest(
            name = "",  // intentionally left blank
            breed = "Friesian",
            year = "2010",
            month = "03",
            day = "22",
            image = bitmapMock  // Used the mocked Bitmap here
        )

        val mockResponse = Response.Builder()
            .code(400)
            .message("Bad Request")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(errorJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = createHorseTestable(token, horseRequest, mockResponse)

        // Assert
        assertEquals(errorJson, result)
    }
}

// Test for creating a trail
@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowBitmap::class])
class CreateTrailTest {

    private val baseUrl = "https://hopla.onrender.com/"
    private val mediaType = "application/json".toMediaTypeOrNull()

    // Testable function to create a trail
    private suspend fun createTrailTestable(
        token: String,
        image: Bitmap,
        createTrailRequest: CreateTrailRequest,
        mockResponse: Response
    ): String {
        val client = object : OkHttpClient() {
            override fun newCall(request: Request): Call {
                return object : Call {
                    override fun enqueue(responseCallback: Callback) {}
                    override fun isExecuted() = false
                    override fun cancel() {}
                    override fun isCanceled() = false
                    override fun clone() = this
                    override fun execute(): Response = mockResponse
                    override fun request(): Request = request
                    override fun timeout(): Timeout = Timeout.NONE
                }
            }
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val jsonString = Json.encodeToString(createTrailRequest)

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "image", "trail.jpg",
                imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("dataJson", jsonString)
            .build()

        val request = Request.Builder()
            .url(baseUrl + "trails/create")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                responseBody ?: "Error: Empty response"
            } catch (e: Exception) {
                "Exception: ${e.message}"
            }
        }
    }

    // Testing the creation of a trail successfully
    @Test
    fun `returns success message when trail is created`() = runTest {
        // Arrange
        val token = "mockToken"
        val successJson = "{\"message\":\"Trail created successfully\"}"

        val imageMock = mockk<Bitmap>(relaxed = true) // Mock the Bitmap
        val createTrailRequest = CreateTrailRequest(
            name = "Test Trail",
            description = "A beautiful trail",
            userHikeId = "12345",
            filters = emptyList()
        )

        val mockResponse = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(successJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = createTrailTestable(token, imageMock, createTrailRequest, mockResponse)

        // Assert
        assertEquals(successJson, result)
    }

    // Testing the creation of a trail with an error
    @Test
    fun `returns error message when trail creation fails`() = runTest {
        // Arrange
        val token = "mockToken"
        val errorJson = "{\"message\":\"Missing trail name\"}"

        val imageMock = mockk<Bitmap>(relaxed = true) // Mock the Bitmap
        val createTrailRequest = CreateTrailRequest(
            name = "", // Intentionally left blank
            description = "A beautiful trail",
            userHikeId = "12345",
            filters = emptyList()
        )

        val mockResponse = Response.Builder()
            .code(400)
            .message("Bad Request")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(errorJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = createTrailTestable(token, imageMock, createTrailRequest, mockResponse)

        // Assert
        assertEquals(errorJson, result)
    }
}

// Test for registering a user
class RegisterUserTest {

    @Before
    fun setup() {
        // Mock JSONObject constructor and methods
        mockkConstructor(JSONObject::class)

        // Mock the `put` method for specific types
        every { anyConstructed<JSONObject>().put(any(), any<Any>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Boolean>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Double>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Int>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Long>()) } returns JSONObject()
    }

    private val baseUrl = "https://hopla.onrender.com/"
    private val mediaType = "application/json".toMediaTypeOrNull()

    // Testable function for registerUser
    private suspend fun registerUserTestable(
        email: String,
        password: String,
        mockResponse: Response
    ): Pair<String, Int> {
        val client = object : OkHttpClient() {
            override fun newCall(request: Request): Call {
                return object : Call {
                    override fun enqueue(responseCallback: Callback) {}
                    override fun isExecuted() = false
                    override fun cancel() {}
                    override fun isCanceled() = false
                    override fun clone() = this
                    override fun execute(): Response = mockResponse
                    override fun request(): Request = request
                    override fun timeout(): Timeout = Timeout.NONE
                }
            }
        }

        val requestBody = JSONObject().apply {
            put("Email", email)
            put("Password", password)
        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(baseUrl + "users/register")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Pair(responseBody ?: "Success", response.code)
            } catch (e: Exception) {
                Pair("Exception: ${e.message}", -1)
            }
        }
    }

    // Test for successful registration
    @Test
    fun `returns success message when registration is successful`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val successJson = "{\"message\":\"User registered successfully\"}"

        val mockResponse = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(successJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = registerUserTestable(email, password, mockResponse)

        // Assert
        assertEquals(Pair(successJson, 200), result)
    }

    // Test for failed registration
    @Test
    fun `returns error message when registration fails`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val errorJson = "{\"message\":\"Email already exists\"}"

        val mockResponse = Response.Builder()
            .code(400)
            .message("Bad Request")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(errorJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = registerUserTestable(email, password, mockResponse)

        // Assert
        assertEquals(Pair(errorJson, 400), result)
    }
}

// Test for resetting a password
class ResetPasswordTest {

    private val baseUrl = "https://hopla.onrender.com/"
    private val mediaType = "application/json".toMediaTypeOrNull()

    @Before
    fun setup() {
        // Mock JSONObject constructor and methods
        mockkConstructor(JSONObject::class)

        // Mock the `put` method for specific types
        every { anyConstructed<JSONObject>().put(any(), any<Any>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Boolean>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Double>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Int>()) } returns JSONObject()
        every { anyConstructed<JSONObject>().put(any(), any<Long>()) } returns JSONObject()
    }

    private suspend fun resetPasswordTestable(
        email: String,
        mockResponse: Response
    ): String {
        val client = object : OkHttpClient() {
            override fun newCall(request: Request): Call {
                return object : Call {
                    override fun enqueue(responseCallback: Callback) {}
                    override fun isExecuted() = false
                    override fun cancel() {}
                    override fun isCanceled() = false
                    override fun clone() = this
                    override fun execute(): Response = mockResponse
                    override fun request(): Request = request
                    override fun timeout(): Timeout = Timeout.NONE
                }
            }
        }

        val requestBody = JSONObject().apply {
            put("Email", email)
        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(baseUrl + "users/reset-password-request")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                responseBody ?: "Success"
            } catch (e: Exception) {
                "Exception: ${e.message}"
            }
        }
    }

    @Test
    fun `returns success message when password reset request is successful`() = runTest {
        // Arrange
        val email = "test@example.com"
        val successJson = "{\"message\":\"Password reset email sent\"}"

        val mockResponse = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(successJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = resetPasswordTestable(email, mockResponse)

        // Assert
        assertEquals(successJson, result)
    }

    @Test
    fun `returns error message when password reset request fails`() = runTest {
        // Arrange
        val email = "test@example.com"
        val errorJson = "{\"message\":\"Email not found\"}"

        val mockResponse = Response.Builder()
            .code(404)
            .message("Not Found")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(baseUrl).build())
            .body(errorJson.toResponseBody(mediaType))
            .build()

        // Act
        val result = resetPasswordTestable(email, mockResponse)

        // Assert
        assertEquals(errorJson, result)
    }
}

