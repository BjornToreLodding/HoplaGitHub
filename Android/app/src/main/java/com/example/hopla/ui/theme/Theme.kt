package com.example.hopla.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

val PrimaryWhite = Color(0xFFFFFFFF)
val StarColor = Color(0xFFFFEE71)
val PrimaryBlack = Color(0xFF000000)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0B3D2B),        // Green
    secondary = Color(0xFF282624),      // Brown
    tertiary = Color(0xFFCFBFAF),       // light beige
    background = Color(0xFF161818),     // Dark grey
    //surface = Color(0xFF161818),
    //onPrimary = CustomWhite,
    //onSecondary = CustomWhite,
    //onTertiary = CustomWhite,
    //onBackground = CustomWhite,
    //onSurface = CustomWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF456559),        // Green
    secondary = Color(0xFF745E4D),      // Brown
    tertiary = Color(0xFF745E4D),       // light beige
    background = Color(0xFFEAE6E1),           // light beige
    //surface = Color(0xFF161818),
    //onPrimary = CustomWhite,
    //onSecondary = CustomWhite,
    //onTertiary = CustomWhite,
    //onBackground = CustomWhite,
    //onSurface = CustomWhite
)

class ThemeViewModel : ViewModel() {
    val isDarkTheme = MutableLiveData(false)

    fun setDarkTheme(isDark: Boolean) {
        isDarkTheme.value = isDark
    }
}

@Composable
fun HoplaTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme.observeAsState(false)
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}