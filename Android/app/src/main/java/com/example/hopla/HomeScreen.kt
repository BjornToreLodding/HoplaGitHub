package com.example.hopla

import androidx.compose.runtime.Composable
import androidx.compose.material.Text

@Composable
fun HomeScreen(languageViewModel: LanguageViewModel) {
    val language = languageViewModel.selectedLanguage.value
    Text(text = if (language == "Norwegian") "Hjem Skjerm" else "Home Screen")
}