package com.example.hopla

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hopla.ui.theme.HoplaTheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.hopla.ui.theme.ThemeViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val languageViewModel: LanguageViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LanguageViewModel(applicationContext, SavedStateHandle()) as T
            }
        }
    }

    private val themeViewModel: ThemeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val currentLanguage = languageViewModel.selectedLanguage.value
            setLocale(this, if (currentLanguage == "Norwegian") "no" else "en")

            HoplaTheme {
                val navController = rememberNavController()
                val isLoggedIn by userViewModel.isLoggedIn

                if (isLoggedIn) {
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController, languageViewModel) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Home.route) { HomeScreen(languageViewModel) }
                            composable(Screen.Profile.route) { ProfileScreen(languageViewModel) }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    languageViewModel,
                                    themeViewModel,
                                    userViewModel
                                )
                            }
                        }
                    }
                } else {
                    LoginScreen(languageViewModel = languageViewModel) { userViewModel.logIn() }
                }
            }
        }
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, languageViewModel: LanguageViewModel) {
    val items = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.Settings
    )
    val language = languageViewModel.selectedLanguage.value
    BottomNavigation(
        modifier = Modifier.height(100.dp)
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { /* Add your icon here */ },
                label = { Text(screen.titleProvider(language)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun LoginScreen(languageViewModel: LanguageViewModel, onLogin: () -> Unit) {
    val language = languageViewModel.selectedLanguage.value
    val loginText = if (language == "Norwegian") "Vennligst logg inn" else "Please log in"
    val buttonText = if (language == "Norwegian") "Logg Inn" else "Log In"

    Column {
        Text(loginText)
        Button(onClick = onLogin) {
            Text(buttonText)
        }
    }
}

class UserViewModel : ViewModel() {
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    fun logIn() {
        _isLoggedIn.value = true
    }

    fun logOut() {
        _isLoggedIn.value = false
    }
    fun deleteUser() {
        _isLoggedIn.value = false
    }
}

sealed class Screen(val route: String, val titleProvider: (String) -> String) {
    data object Home : Screen("home", { language -> if (language == "Norwegian") "Hjem" else "Home" })
    data object Profile : Screen("profile", { language -> if (language == "Norwegian") "Profil" else "Profile" })
    data object Settings : Screen("settings", { language -> if (language == "Norwegian") "Innstillinger" else "Settings" })
}