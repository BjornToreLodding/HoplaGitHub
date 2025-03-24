package com.example.hopla.apiService

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.hopla.R
import com.example.hopla.universalData.ErrorResponse
import com.example.hopla.universalData.LoginRequest
import com.example.hopla.universalData.ResetPasswordRequest
import com.example.hopla.universalData.StableRequest
import com.example.hopla.universalData.User
import com.example.hopla.universalData.UserReportRequest
import com.example.hopla.universalData.UserReportResponse
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.apiUrl
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

fun handleLogin(
    username: String,
    password: String,
    context: Context,
    coroutineScope: CoroutineScope,
    onLogin: () -> Unit,
    setErrorMessage: (String) -> Unit,
    setShowErrorDialog: (Boolean) -> Unit,
    setIsLoading: (Boolean) -> Unit
) {
    val trimmedUsername = username.trim()
    val trimmedPassword = password.trim()

    if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
        setErrorMessage(context.getString(R.string.input_fields_cannot_be_empty))
        setShowErrorDialog(true)
    } else {
        setIsLoading(true)
        coroutineScope.launch {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    })
                }
            }
            try {
                val response: HttpResponse = client.post(apiUrl + "users/login/") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(email = trimmedUsername, password = trimmedPassword))
                }
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val loginResponse = response.body<User>()
                        UserSession.token = loginResponse.token
                        UserSession.userId = loginResponse.userId
                        UserSession.email = trimmedUsername
                        UserSession.name = loginResponse.name
                        UserSession.alias = loginResponse.alias
                        UserSession.profilePictureURL = loginResponse.pictureUrl
                        UserSession.telephone = loginResponse.telephone
                        UserSession.description = loginResponse.description
                        UserSession.dob = loginResponse.dob
                        UserSession.redirect = loginResponse.redirect
                        onLogin()
                    }
                    HttpStatusCode.Unauthorized -> {
                        val errorResponse = response.body<ErrorResponse>()
                        setErrorMessage(errorResponse.message)
                        setShowErrorDialog(true)
                    }
                    else -> {
                        Log.e(
                            "LoginError",
                            "Status: ${response.status}, Body: ${response.bodyAsText()}"
                        )
                        setErrorMessage(context.getString(R.string.not_available_right_now))
                        setShowErrorDialog(true)
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Exception: ${e.message}")
                setErrorMessage(context.getString(R.string.not_available_right_now))
                setShowErrorDialog(true)
            } finally {
                client.close()
                setIsLoading(false)
            }
        }
    }
}

//---------- Post requests for creating a user report ---------------
suspend fun createUserReport(token: String, reportRequest: UserReportRequest): UserReportResponse {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val response: HttpResponse = client.post(apiUrl+"userreports/create") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(reportRequest)
    }

    val responseBody: String = response.bodyAsText()
    Log.d("createUserReport", "Response: $responseBody")
    client.close()
    return Json.decodeFromString(UserReportResponse.serializer(), responseBody)
}

//----------- Post request for changing email -----------------------------
suspend fun changeEmail(newEmail: String, password: String): String {
    val url = apiUrl+"users/change-email"
    val requestBody = JSONObject().apply {
        put("NewEmail", newEmail)
        put("Password", password)
    }.toString()

    Log.d("changeEmail", "Request URL: $url")
    Log.d("changeEmail", "Request Body: $requestBody")

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer ${UserSession.token}")
        .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d("changeEmail", "Response Code: ${response.code}")
            Log.d("changeEmail", "Response Body: $responseBody")

            if (response.isSuccessful) {
                responseBody ?: "Success"
            } else {
                responseBody ?: "Error: ${response.message}"
            }
        } catch (e: Exception) {
            Log.e("changeEmail", "Exception: ${e.message}")
            "Exception: ${e.message}"
        }
    }
}

//-------------- Create a new user -> 1st step---------------------
suspend fun registerUser(email: String, password: String): Pair<String, Int> {
    val url = apiUrl + "users/register"
    val requestBody = JSONObject().apply {
        put("Email", email)
        put("Password", password)
    }.toString()

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d("registerUser", "Response Code: ${response.code}")
            Log.d("registerUser", "Response Body: $responseBody")

            Pair(responseBody ?: "Success", response.code)
        } catch (e: Exception) {
            Log.e("registerUser", "Exception: ${e.message}")
            Pair("Exception: ${e.message}", -1)
        }
    }
}

//----------------------Community Post Request-------------------------
suspend fun createStable(token: String, stableRequest: StableRequest, context: Context): String {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    val byteArrayOutputStream = ByteArrayOutputStream()
    stableRequest.Image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val imageBytes = byteArrayOutputStream.toByteArray()

    val response: HttpResponse = httpClient.use { client ->
        client.post("https://hopla.onrender.com/stables/create") {
            header("Authorization", "Bearer $token")
            setBody(MultiPartFormDataContent(
                formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"stable.jpg\"")
                    })
                    append("Name", stableRequest.Name)
                    append("Description", stableRequest.Description)
                    append("Latitude", stableRequest.Latitude.toString())
                    append("Longitude", stableRequest.Longitude.toString())
                    append("PrivateGroup", stableRequest.PrivateGroup.toString())
                }
            ))
        }
    }

    val responseBody: String = response.bodyAsText()
    Log.d("createStable", "Response Status: ${response.status}")
    Log.d("createStable", "Response Headers: ${response.headers}")
    Log.d("createStable", "Response Body: $responseBody")
    return responseBody
}

//------------------ Post requests for login page ---------------------------------------------
suspend fun resetPassword(email: String): String {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    val requestBody = ResetPasswordRequest(email)
    val response: HttpResponse = client.post(apiUrl + "users/reset-password-request") {
        contentType(ContentType.Application.Json)
        setBody(requestBody)
    }

    val responseBody: String = response.body()
    Log.d("resetPassword", "Response: $responseBody")
    client.close()
    return responseBody
}