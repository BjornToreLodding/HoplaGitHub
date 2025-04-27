package com.example.hopla.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color

// Define numeric color values as constants
private const val COLOR_LIGHT_WHITE = 0xFFFFFFFF
private const val COLOR_DARK_BLACK = 0xFF000000
private const val COLOR_LIGHT_BROWN = 0xFF745E4D
private const val COLOR_DARK_GREEN = 0xFF2F463E
private const val COLOR_DARK_GRAY = 0xFF161818
private const val COLOR_DARK_BROWN = 0xFF493B2F
private const val COLOR_LIGHTER_GRAY = 0xFF303030
private const val COLOR_LIGHT_GREEN = 0xFF456559
private const val COLOR_LIGHT_BEIGE = 0xFFEAE6E1



// Use the constants in Color definitions
val LightWhite = Color(COLOR_LIGHT_WHITE) // White
val DarkBlack = Color(COLOR_DARK_BLACK)  // Black
val LightBrown = Color(COLOR_LIGHT_BROWN) // Brown
val DarkGreen = Color(COLOR_DARK_GREEN)  // Green
val DarkGray = Color(COLOR_DARK_GRAY)   // Dark Gray
val DarkBrown = Color(COLOR_DARK_BROWN)  // Brown
val LighterGray = Color(COLOR_LIGHTER_GRAY) // Gray
val LightGreen = Color(COLOR_LIGHT_GREEN) // Green
val LightBeige = Color(COLOR_LIGHT_BEIGE) // Beige

// Colors for the dark theme of the app
private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,        // Green
    background = DarkGray,     // Dark grey
    tertiary = DarkBrown,       // light beige
    onBackground = LighterGray,   // light beige
    onPrimary = LightWhite,       // light beige
    secondary = LightWhite       // light beige
)

// Colors for the light theme of the app
private val LightColorScheme = lightColorScheme(
    primary = LightGreen,        // Green
    background = LightBeige,     // light beige
    tertiary = LightBrown,       // light beige
    onBackground = LightWhite,   // light beige
    onPrimary = LightWhite,       // light beige
    secondary = DarkBlack      // light beige
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
