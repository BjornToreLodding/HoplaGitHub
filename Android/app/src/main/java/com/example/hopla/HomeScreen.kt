package com.example.hopla

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun HomeScreen(languageViewModel: LanguageViewModel) {
    val language = languageViewModel.selectedLanguage.value
    val helloWorldText = stringResource(R.string.hello_world)
    Spacer(modifier = Modifier.height(50.dp))
    //Text(text = if (language == "Norwegian") "Hjem Skjerm" else "Home Screen")
    Spacer(modifier = Modifier.height(60.dp))
    Text(text = helloWorldText)
}