package com.example.hopla

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddRoad
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hopla.community.AddCommunityScreen
import com.example.hopla.community.CommunityDetailScreen
import com.example.hopla.community.CommunityScreen
import com.example.hopla.home.HomeScreen
import com.example.hopla.login.CheckLoginState
import com.example.hopla.login.LoginScreen
import com.example.hopla.newTrip.NewTripScreen
import com.example.hopla.profile.AddNewType
import com.example.hopla.profile.FollowingScreen
import com.example.hopla.profile.FriendsListScreen
import com.example.hopla.profile.FriendsScreen
import com.example.hopla.profile.HorseDetailScreen
import com.example.hopla.profile.LanguageViewModel
import com.example.hopla.profile.MyHorsesScreen
import com.example.hopla.profile.MyTripsScreen
import com.example.hopla.profile.NotificationsScreen
import com.example.hopla.profile.ProfileScreen
import com.example.hopla.profile.SettingsScreen
import com.example.hopla.profile.UserHorsesScreen
import com.example.hopla.profile.UsersProfileScreen
import com.example.hopla.ui.theme.HoplaTheme
import com.example.hopla.ui.theme.ThemeViewModel
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.universalData.DeleteUserRequest
import com.example.hopla.universalData.DeleteUserResponse
import com.example.hopla.universalData.MapScreen
import com.example.hopla.universalData.StartTripMapScreen
import com.example.hopla.universalData.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale


class MainActivity : ComponentActivity() {
    private val bottomBarViewModel: BottomBarViewModel by viewModels()

    private val languageViewModel: LanguageViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LanguageViewModel(application as Application, SavedStateHandle()) as T
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
                val isBottomBarVisible by bottomBarViewModel.isBottomBarVisible

                CheckLoginState(navController = navController, onLogin = {
                    userViewModel.logIn(navController)
                })

                if (isLoggedIn) {
                    Scaffold(
                        topBar = { TopBar() },
                        bottomBar = {
                            if (isBottomBarVisible) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Home.route) { HomeScreen(navController) }
                            composable(Screen.Trails.route) { TrailsScreen(navController) }
                            composable(Screen.NewTrip.route) { NewTripScreen(bottomBarViewModel = bottomBarViewModel) }
                            composable(Screen.Community.route) { CommunityScreen(navController, UserSession.token) }
                            composable(Screen.Profile.route) { ProfileScreen( navController) }
                            composable("friends_list/{userId}") { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                                FriendsListScreen(navController, userId)
                            }
                            composable("settings") { SettingsScreen(languageViewModel, themeViewModel, userViewModel, navController) }
                            composable("my_trips") { MyTripsScreen(navController) }
                            composable("map_screen") { MapScreen() }
                            composable("start_trip_map/{trailId}") { backStackEntry ->
                                val trailId = backStackEntry.arguments?.getString("trailId") ?: ""
                                StartTripMapScreen(trailId, navController)
                            }
                            composable("my_horses") { MyHorsesScreen(navController) }
                            composable("friends") { FriendsScreen(navController) }
                            composable("user_horses/{userId}") { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                                UserHorsesScreen(navController, userId)
                            }
                            composable("following") { FollowingScreen(navController) }
                            composable(
                                "stableDetail/{stableId}",
                                arguments = listOf(navArgument("stableId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val stableId = backStackEntry.arguments?.getString("stableId") ?: return@composable
                                Log.d("fetchStableDetails", "Navigating to CommunityDetailScreen with stableId: $stableId")
                                CommunityDetailScreen(navController, stableId, UserSession.token)
                            }
                            composable("addCommunityScreen") {
                                AddCommunityScreen(navController, UserSession.token)
                            }
                            composable("addFriendScreen") {
                                AddNewType(navController, "Friend")
                            }
                            composable("addHorseScreen") {
                                AddNewType(navController, "Horse")
                            }
                            composable("update_screen") { UpdateScreen(navController) }
                            composable(
                                "friend_profile/{userId}",
                                arguments = listOf(navArgument("userId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                                Log.d("Navigation", "Navigating to friend_profile with userId: $userId")
                                UsersProfileScreen(navController, userId)
                                Log.d("Navigation", "UsersProfileScreen loaded successfully")
                            }
                            composable(
                                "horse_detail/{horseId}",
                                arguments = listOf(
                                    navArgument("horseId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val horseId = backStackEntry.arguments?.getString("horseId") ?: ""
                                HorseDetailScreen(navController, horseId)
                            }
                            composable("notifications") { NotificationsScreen(navController) }
                        }
                    }
                } else {
                    LoginScreen(
                        navController = navController,
                        onLogin = { userViewModel.logIn(navController) },
                        onCreateUser = { /* Handle create user action */ }
                    )
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
                    painter = painterResource(id = R.drawable.logo_white), // Load the image
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
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Trails,
        Screen.NewTrip,
        Screen.Community,
        Screen.Profile
    )
    val context = LocalContext.current
    // Variables for double pressing to return to start destination og navigation item
    var lastSelectedItem by remember { mutableStateOf<Screen?>(null) }
    var lastPressTime by remember { mutableLongStateOf(0L) }

    BottomNavigation(
        modifier = Modifier.height(100.dp),
        backgroundColor = MaterialTheme.colorScheme.primary
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    when(screen) {
                        Screen.Home -> Icon(Icons.Outlined.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Screen.Profile -> Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Screen.NewTrip -> Icon(Icons.Outlined.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Screen.Trails -> Icon(Icons.Outlined.AddRoad, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Screen.Community -> Icon(Icons.Outlined.Face, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                label = { Text(screen.titleProvider(context), fontSize = 10.sp, maxLines = 1, style = generalTextStyle, color = MaterialTheme.colorScheme.onPrimary) },
                selected = currentRoute == screen.route,
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (lastSelectedItem == screen && currentTime - lastPressTime < 300) {
                        navController.navigate(screen.route) {
                            popUpTo(screen.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    lastSelectedItem = screen
                    lastPressTime = currentTime
                }
            )
        }
    }
}

class UserViewModel : ViewModel() {
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    fun logIn(navController: NavController) {
        _isLoggedIn.value = true
        navController.navigate(Screen.Profile.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun logOut() {
        _isLoggedIn.value = false
        UserSession.clear()
    }

    fun deleteUser(token: String, password: String) {
        viewModelScope.launch {
            try {
                val client = HttpClient {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            encodeDefaults = true
                        })
                    }
                }

                val response = client.patch("https://hopla.onrender.com/users/delete") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(DeleteUserRequest(Password = password))
                }

                val responseBody: String = response.bodyAsText()
                Log.d("deleteUser", "Response: $responseBody")
                client.close()

                val deleteUserResponse = Json.decodeFromString(DeleteUserResponse.serializer(), responseBody)
                Log.d("deleteUser", "Message: ${deleteUserResponse.message}")

                _isLoggedIn.value = false
                UserSession.clear()
            } catch (e: Exception) {
                Log.e("deleteUser", "Error deleting user", e)
            }
        }
    }
}

sealed class Screen(val route: String, val titleProvider: (Context) -> String) {
    data object Home : Screen("home", { context -> context.getString(R.string.home) })
    data object Trails : Screen("trails", { context -> context.getString(R.string.trails) })
    data object NewTrip : Screen("new_trip", { context -> context.getString(R.string.new_trip) })
    data object Community : Screen("community", { context -> context.getString(R.string.community) })
    data object Profile : Screen("profile", { context -> context.getString(R.string.profile) })
}

class BottomBarViewModel : ViewModel() {
    private val _isBottomBarVisible = mutableStateOf(true)
    val isBottomBarVisible: State<Boolean> = _isBottomBarVisible

    fun showBottomBar() {
        _isBottomBarVisible.value = true
    }

    fun hideBottomBar() {
        _isBottomBarVisible.value = false
    }
}
