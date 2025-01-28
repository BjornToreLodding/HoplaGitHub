package com.example.hopla

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.material3.*
import kotlinx.coroutines.launch

data class User(
    val id: Int,
    val firstname: String,
    val lastname: String
)

@Composable
fun ProfileScreen(languageViewModel: LanguageViewModel) {
    val language = languageViewModel.selectedLanguage.value
    val userId = 4
    var user by remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        coroutineScope.launch {
            user = RetrofitInstance.api.getUser(userId)
        }
    }

    Column {
        Text(text = if (language == "Norwegian") "Profil Skjerm" else "Profile Screen")
        user?.let {
            Text(text = "Id: ${it.id}")
            Text(text = if(language=="Norwegian") "Navn: ${it.firstname}" else "Name: ${it.firstname}")
            Text(text = if (language=="Norwegian") "Etternavn: ${it.lastname}" else "Lastname: ${it.lastname}")
        } ?: run {
            CircularProgressIndicator()
        }
    }
}
