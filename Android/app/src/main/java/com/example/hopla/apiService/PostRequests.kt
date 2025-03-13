package com.example.hopla.apiService

import android.content.Context
import android.util.Log
import com.example.hopla.universalData.ErrorResponse
import com.example.hopla.universalData.LoginRequest
import com.example.hopla.R
import com.example.hopla.universalData.User
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.apiUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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