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
import com.example.hopla.universalData.apiUrl
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

suspend fun updateUserInfo(token: String, alias: String, name: String): Pair<Int, String> {
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
        client.put(apiUrl+"users/update") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "Alias" to alias,
                    "Name" to name
                )
            )
        }
    }

    val responseBody: String = response.bodyAsText()
    Log.d("updateUserInfo", "Response: $responseBody")
    return response.status.value to responseBody
}