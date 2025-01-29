package com.example.hopla

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Face
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource


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
                        topBar = { TopBar() },
                        bottomBar = { BottomNavigationBar(navController, languageViewModel) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Home.route) { HomeScreen() }
                            composable(Screen.Routes.route) { RoutesScreen() }
                            composable(Screen.Map.route) { MapScreen() }
                            composable(Screen.Stables.route) { StablesScreen() }
                            composable(Screen.Profile.route) { SettingsScreen(languageViewModel, themeViewModel, userViewModel)}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo1), // Load the image
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .height(40.dp) // Adjust size
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}



@Composable
fun BottomNavigationBar(navController: NavHostController, languageViewModel: LanguageViewModel) {
    val items = listOf(
        Screen.Home,
        Screen.Routes,
        Screen.Map,
        Screen.Stables,
        Screen.Profile
    )
    val context = LocalContext.current
    BottomNavigation(
        modifier = Modifier.height(100.dp)
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    when(screen) {
                        Screen.Home -> Icon(Icons.Outlined.Home, contentDescription = null)
                        Screen.Profile -> Icon(Icons.Outlined.Person, contentDescription = null)
                        Screen.Map -> Icon(Icons.Outlined.Add, contentDescription = null)
                        Screen.Routes -> Icon(Icons.Outlined.LocationOn, contentDescription = null)
                        Screen.Stables -> Icon(Icons.Outlined.Face, contentDescription = null)
                    }
                },
                label = { Text(screen.titleProvider(context)) },
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
    val loginText = stringResource(R.string.log_in)
    val buttonText = stringResource(R.string.log_in)

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

sealed class Screen(val route: String, val titleProvider: (Context) -> String) {
    data object Home : Screen("home", { context -> context.getString(R.string.home) })
    data object Routes : Screen("routes", { context -> context.getString(R.string.routes) })
    data object Map : Screen("map", { context -> context.getString(R.string.map) })
    data object Stables : Screen("stables", { context -> context.getString(R.string.stables) })
    data object Profile : Screen("profile", { context -> context.getString(R.string.profile) })
}


