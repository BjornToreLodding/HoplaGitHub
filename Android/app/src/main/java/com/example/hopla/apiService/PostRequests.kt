package com.example.hopla.apiService

import android.content.Context
import android.util.Log
import com.example.hopla.universalData.ErrorResponse
import com.example.hopla.universalData.LoginRequest
import com.example.hopla.R
import com.example.hopla.universalData.User
import com.example.hopla.universalData.UserReportRequest
import com.example.hopla.universalData.UserReportResponse
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.apiUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

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