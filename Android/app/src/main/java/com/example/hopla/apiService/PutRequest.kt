package com.example.hopla.apiService

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.util.Log
import com.example.hopla.universalData.ChangePasswordResponse
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.UserRelationResponse
import com.example.hopla.universalData.apiUrl
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData

suspend fun uploadProfilePicture(token: String, userId: String, imageBitmap: Bitmap): String {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val byteArrayOutputStream = ByteArrayOutputStream()
    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val imageBytes = byteArrayOutputStream.toByteArray()

    val response: HttpResponse = httpClient.use { client ->
        client.put("https://hopla.onrender.com/upload") {
            header("Authorization", "Bearer $token")
            setBody(MultiPartFormDataContent(
                formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                    })
                    append("table", "Users")
                    append("entityId", userId)
                }
            ))
        }
    }

    val responseBody: String = response.bodyAsText()
    Log.d("uploadProfilePicture", "Response: $responseBody")
    return responseBody
}

suspend fun changePassword(
    token: String,
    oldPassword: String,
    newPassword: String,
    confirmPassword: String
): Pair<Int, String> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = httpClient.use { client ->
        client.put("https://hopla.onrender.com/users/change-password") {
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
    }

    val responseBody: String = response.bodyAsText()
    val message = Json.decodeFromString<ChangePasswordResponse>(responseBody).message
    Log.d("changePassword", "Response: $message")
    return response.status.value to message
}

suspend fun updateUserInfo(
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
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 180_000 // 180 seconds
            connectTimeoutMillis = 180_000 // 180 seconds
            socketTimeoutMillis = 180_000 // 180 seconds
        }
    }

    val requestBody = mutableMapOf(
        "Alias" to alias,
        "Name" to name
    )
    phone?.let { requestBody["Telephone"] = it }
    description?.let { requestBody["Description"] = it }
    password?.let { requestBody["Password"] = it }
    year?.let { requestBody["Year"] = it.toString() }
    month?.let { requestBody["Month"] = it.toString() }
    day?.let { requestBody["Day"] = it.toString() }

    Log.d("updateUserInfo", "Request Body: $requestBody")

    return try {
        val response: HttpResponse = httpClient.use { client ->
            client.put(apiUrl + "users/update") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        val responseBody: String = response.bodyAsText()
        Log.d("updateUserInfo", "Response: $responseBody")
        response.status.value to responseBody
    } catch (e: ConnectTimeoutException) {
        Log.e("updateUserInfo", "Connection timeout", e)
        -1 to "Connection timeout: ${e.message}"
    } catch (e: Exception) {
        Log.e("updateUserInfo", "Error updating user info", e)
        -1 to "An error occurred: ${e.message}"
    }
}

//-------------------------PUT requests for relations between users-------------------------
suspend fun sendUserRelationRequestPut(token: String, request: UserRelationChangeRequest): UserRelationResponse {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = client.put(apiUrl + "userrelations") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(request)
    }

    val responseBody: String = response.bodyAsText()
    Log.d("changeRelations", "Response: $responseBody")
    client.close()
    return Json.decodeFromString(UserRelationResponse.serializer(), responseBody)
}

//-----------------------PUT requests for user hikes-------------------------
suspend fun updateUserHike(
    token: String,
    userHikeId: String,
    title: String? = null,
    horseId: String? = null,
    imageBitmap: Bitmap? = null,
    description: String? = null
): String {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val formData = formData {
        title?.let { append("Title", it) }
        horseId?.let { append("HorseId", it) }
        description?.let { append("Description", it) }
        imageBitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            append("Image", imageBytes, Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
            })
        }
    }

    val response: HttpResponse = httpClient.use { client ->
        client.put("https://hopla.onrender.com/userhikes/$userHikeId") {
            header("Authorization", "Bearer $token")
            setBody(MultiPartFormDataContent(formData))
        }
    }

    val responseBody: String = response.bodyAsText()
    Log.d("updateUserHike", "Response code: ${response.status.value}")
    Log.d("updateUserHike" , "Response form data: $formData")
    Log.d("updateUserHike", "Response: $responseBody")
    return responseBody
}