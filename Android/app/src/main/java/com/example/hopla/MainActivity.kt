package com.example.hopla

import android.app.Application
import android.content.Context
import android.os.Bundle
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {

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

                if (isLoggedIn) {
                    Scaffold(
                        topBar = { TopBar() },
                        bottomBar = { BottomNavigationBar(navController) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Home.route) { HomeScreen() }
                            composable(Screen.Trails.route) { TrailsScreen(navController) }
                            composable(Screen.NewTrip.route) { NewTripScreen() }
                            composable(Screen.Community.route) { CommunityScreen(navController) }
                            composable(Screen.Profile.route) { ProfileScreen( navController) }
                            composable("settings") { SettingsScreen(languageViewModel, themeViewModel, userViewModel, navController) }
                            composable("my_trips") { MyTripsScreen(navController) }
                            composable("my_horses") { MyHorsesScreen(navController) }
                            composable("friends") { FriendsScreen(navController) }
                            composable("following") { FollowingScreen(navController) }
                            composable(
                                "communityDetail/{communityName}",
                                arguments = listOf(navArgument("communityName") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val communityName = backStackEntry.arguments?.getString("communityName") ?: ""
                                val community = getCommunityByName(communityName)
                                if (community != null) {
                                    CommunityDetailScreen(navController, community)
                                } else {
                                    Text(text = "Community not found")
                                }
                            }
                            composable("addCommunityScreen") {
                                AddCommunityScreen(navController) { newCommunity ->
                                    // Handle the addition of the new community here
                                }
                            }
                            composable("addFriendScreen") {
                                AddNewType(navController, "Friend") { name, imageBitmap, friendType, friendAge ->
                                    // Handle adding a new friend
                                }
                            }
                            composable("addHorseScreen") {
                                AddNewType(navController, "Horse") { name, imageResource, breed, age ->
                                    // Handle adding a new horse
                                }
                            }
                            composable("update_screen") { UpdateScreen(navController) }
                            composable(
                                "person_detail/{personName}/{personImageResource}/{personStatus}",
                                arguments = listOf(
                                    navArgument("personName") { type = NavType.StringType },
                                    navArgument("personImageResource") { type = NavType.IntType },
                                    navArgument("personStatus") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val personName = backStackEntry.arguments?.getString("personName") ?: ""
                                val personImageResource = backStackEntry.arguments?.getInt("personImageResource") ?: 0
                                val personStatus = backStackEntry.arguments?.getString("personStatus") ?: "NONE"
                                val person = Person(name = personName, imageResource = personImageResource, status = PersonStatus.valueOf(personStatus))
                                PersonDetailScreen(navController, person)
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
                        }
                    }
                } else {
                    LoginScreen(
                        onLogin = { userViewModel.logIn() },
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
                        Screen.Home -> Icon(Icons.Outlined.Home, contentDescription = null)
                        Screen.Profile -> Icon(Icons.Outlined.Person, contentDescription = null)
                        Screen.NewTrip -> Icon(Icons.Outlined.Add, contentDescription = null)
                        Screen.Trails -> Icon(Icons.Outlined.LocationOn, contentDescription = null)
                        Screen.Community -> Icon(Icons.Outlined.Face, contentDescription = null)
                    }
                },
                label = { Text(screen.titleProvider(context), fontSize = 10.sp, maxLines = 1) },
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
    data object Trails : Screen("trails", { context -> context.getString(R.string.trails) })
    data object NewTrip : Screen("new_trip", { context -> context.getString(R.string.new_trip) })
    data object Community : Screen("community", { context -> context.getString(R.string.community) })
    data object Profile : Screen("profile", { context -> context.getString(R.string.profile) })
}


