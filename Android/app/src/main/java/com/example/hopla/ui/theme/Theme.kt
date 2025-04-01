package com.example.hopla.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Colors for the dark theme of the app
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2F463E),        // Green
    background = Color(0xFF161818),     // Dark grey
    tertiary = Color(0xFF493B2F),       // light beige
    onBackground = Color(0xFF303030),   // light beige
    onPrimary = Color(0xFFFFFFFF),       // light beige
    secondary = Color(0xFFFFFFFF)       // light beige
)

// Colors for the light theme of the app
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF456559),        // Green
    background = Color(0xFFEAE6E1),     // light beige
    tertiary = Color(0xFF745E4D),       // light beige
    onBackground = Color(0xFFFFFFFF),   // light beige
    onPrimary = Color(0xFFFFFFFF),       // light beige
    secondary = Color(0xFF000000)       // light beige
)

// ViewModel to manage the theme state
class ThemeViewModel : ViewModel() {
    val isDarkTheme = MutableLiveData(false)

    fun setDarkTheme(isDark: Boolean) {
        isDarkTheme.value = isDark
    }
}

// Composable function to apply the theme to the app
@Composable
fun HoplaTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme.observeAsState(false)
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}