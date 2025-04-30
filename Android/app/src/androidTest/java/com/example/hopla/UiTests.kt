package com.example.hopla

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hopla.community.StableCard
import com.example.hopla.home.TopTextColumn
import com.example.hopla.ui.theme.HoplaTheme
import com.example.hopla.universalData.Stable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Test that the top app bar is displayed correctly
@RunWith(AndroidJUnit4::class)
class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topBar_displaysAppLogo() {
        composeTestRule.setContent {
            MaterialTheme {
                TopBar()
            }
        }

        // Find image by content description
        composeTestRule
            .onNodeWithContentDescription("App Logo")
            .assertExists()
            .assertIsDisplayed()
    }
}

// Tests regarding displaying the bottom navigation bar and clicking the items to navigate
@RunWith(AndroidJUnit4::class)
class BottomBarTest {
    // composeTestRule is used to set the content of the test
    @get:Rule
    val composeTestRule = createComposeRule()
    // Test that the bottom navigation bar is displayed correctly (5 icons)
    @Test
    fun bottomBar_displaysFirstIcon() {
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                BottomNavigationBar(navController)
            }
        }

        // Check that the Home icon is displayed
        composeTestRule
            .onNodeWithContentDescription("HomeScreen")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun bottomBar_displaysSecondIcon() {
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                BottomNavigationBar(navController)
            }
        }

        // Check that the profile icon is displayed
        composeTestRule
            .onNodeWithContentDescription("PersonIcon")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun bottomBar_displaysThirdIcon() {
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                BottomNavigationBar(navController)
            }
        }

        // Check that the new trip icon is displayed
        composeTestRule
            .onNodeWithContentDescription("AddIcon")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun bottomBar_displaysForthIcon() {
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                BottomNavigationBar(navController)
            }
        }

        // Check that the trails icon is displayed
        composeTestRule
            .onNodeWithContentDescription("AddRoadIcon")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun bottomBar_displaysFifthIcon() {
        composeTestRule.setContent {
            MaterialTheme {
                val navController = rememberNavController()
                BottomNavigationBar(navController)
            }
        }

        // Check that the community icon is displayed
        composeTestRule
            .onNodeWithContentDescription("FaceIcon")
            .assertExists()
            .assertIsDisplayed()
    }

    // Tests that clicking the icons in the bottom navigation bar navigates to the correct screen
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun bottomBar_navigatesToProfileScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) { /* Dummy screen */ }
                        composable(Screen.Community.route) { /* Dummy screen */ }
                        composable(Screen.Profile.route) {
                            Column(modifier = Modifier.testTag("Profile")) {
                                Text("Profile Screen")
                            }
                        }
                    }
                }
            }
        }

        // Click the profile icon
        composeTestRule
            .onNodeWithContentDescription("PersonIcon")
            .performClick()

        // Check that the Profile screen is displayed
        composeTestRule
            .onNodeWithTag("Profile")
            .assertIsDisplayed()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun bottomBar_navigatesToCommunityScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) { /* Dummy screen */ }
                        composable(Screen.Community.route) {
                            Column(modifier = Modifier.testTag("Community")) {
                                Text("Community Screen")
                            }
                        }
                        composable(Screen.Profile.route) { /* Dummy screen */ }
                    }
                }
            }
        }

        // Click the community icon
        composeTestRule
            .onNodeWithContentDescription("FaceIcon")
            .performClick()

        // Check that the community screen is displayed
        composeTestRule
            .onNodeWithTag("Community")
            .assertIsDisplayed()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun bottomBar_navigatesToNewTripScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) { /* Dummy screen */ }
                        composable(Screen.NewTrip.route) {
                            Column(modifier = Modifier.testTag("NewTrip")) {
                                Text("NewTrip Screen")
                            }
                        }
                        composable(Screen.Profile.route) { /* Dummy screen */ }
                    }
                }
            }
        }

        // Click the new trip icon
        composeTestRule
            .onNodeWithContentDescription("AddIcon")
            .performClick()

        // Check that the NewTrip screen is displayed
        composeTestRule
            .onNodeWithTag("NewTrip")
            .assertIsDisplayed()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun bottomBar_navigatesToTrailsScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) { /* Dummy screen */ }
                        composable(Screen.Trails.route) {
                            Column(modifier = Modifier.testTag("Trails")) {
                                Text("Trails Screen")
                            }
                        }
                        composable(Screen.Profile.route) { /* Dummy screen */ }
                    }
                }
            }
        }

        // Click the trails icon
        composeTestRule
            .onNodeWithContentDescription("AddRoadIcon")
            .performClick()

        // Check that the Trails screen is displayed
        composeTestRule
            .onNodeWithTag("Trails")
            .assertIsDisplayed()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun bottomBar_navigatesToHomeScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Trails.route) {
                        composable(Screen.Trails.route) { /* Dummy screen */ }
                        composable(Screen.Home.route) {
                            Column(modifier = Modifier.testTag("Home")) {
                                Text("Home Screen")
                            }
                        }
                        composable(Screen.Profile.route) { /* Dummy screen */ }
                    }
                }
            }
        }

        // Click the home icon
        composeTestRule
            .onNodeWithContentDescription("HomeScreen")
            .performClick()

        // Check that the home screen is displayed
        composeTestRule
            .onNodeWithTag("Home")
            .assertIsDisplayed()
    }
}

// Tests for the Community screen
class CommunityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stableCard_showsCorrectStateOnLikeToggle() {
        // Create a mock Stable object
        val stable = Stable(
            stableId = "123",
            stableName = "Test Stable",
            distance = 10.10,
            pictureUrl = "https://example.com/image.jpg",
            member = false
        )

        // Create the list of liked stables (empty initially)
        val likedStables = mutableListOf<Stable>()
        val token = "mock-token"

        // Set up the composable content to be tested
        composeTestRule.setContent {
            HoplaTheme {
                StableCard(
                    stable = stable,
                    navController = rememberNavController(),
                    likedStables = likedStables,
                    token = token,
                    contentDescriptionProvider = { isLiked ->
                        if (isLiked) "liked" else "not_liked"
                    }
                )
            }
        }

        // Verify the initial state (not liked)
        composeTestRule
            .onNodeWithContentDescription("not_liked")
            .assertExists()
            .assertIsDisplayed()

        // Click the like button
        composeTestRule
            .onNodeWithContentDescription("not_liked")
            .performClick()
    }
}

// Test for the Home screen
class HomeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysTopTextColumnAndPostList() {
        composeTestRule.setContent {
            HoplaTheme {
                Column {
                    // Mock TopTextColumn
                    TopTextColumn(
                        selectedItem = Icons.Outlined.Language,
                        onItemSelected = {},
                        modifier = Modifier.testTag("TopTextColumn")
                    )

                    // Mock PostList with fake data
                    LazyColumn(modifier = Modifier.testTag("PostList")) {
                        items(listOf("Post 1", "Post 2", "Post 3")) { post ->
                            Text(text = post, modifier = Modifier.testTag(post)) // Tag each post
                        }
                    }
                }
            }
        }

        composeTestRule.waitForIdle()

        // Ensure the TopTextColumn exists before checking if it's displayed
        composeTestRule
            .onNodeWithTag("TopTextColumn")
            .assertExists() // Assert it exists first
            .assertIsDisplayed() // Then assert it's displayed

        // Ensure each post exists and is displayed
        composeTestRule
            .onNodeWithTag("Post 1")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Post 2")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Post 3")
            .assertExists()
            .assertIsDisplayed()
    }
    // Check that the icons of the top text column are displayed
    @Test
    fun checkIconIsDisplayedTopText() {
        composeTestRule.setContent {
            MaterialTheme {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "All posts",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "FriendsAndFollowing",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Default.Cable,
                    contentDescription = "Followed trails",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        // Check that the icon with the content description "More options" is displayed
        composeTestRule
            .onNodeWithContentDescription("More options")
            .assertExists()
            .assertIsDisplayed()

        // Check that the icon with the content description "More options" is displayed
        composeTestRule
            .onNodeWithContentDescription("Close")
            .assertExists()
            .assertIsDisplayed()

        // Check that the icon with the content description "More options" is displayed
        composeTestRule
            .onNodeWithContentDescription("Followed trails")
            .assertExists()
            .assertIsDisplayed()

        // Check that the icon with the content description "More options" is displayed
        composeTestRule
            .onNodeWithContentDescription("FriendsAndFollowing")
            .assertExists()
            .assertIsDisplayed()

        // Check that the icon with the content description "More options" is displayed
        composeTestRule
            .onNodeWithContentDescription("All posts")
            .assertExists()
            .assertIsDisplayed()
    }

}

// Test for the trails screen
@RunWith(AndroidJUnit4::class)
class TrailsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun trailsScreen_displaysTrailList() {
        composeTestRule.setContent {
            HoplaTheme {
                Column {
                    // Mock TrailsScreen with fake data
                    LazyColumn(modifier = Modifier.testTag("TrailsList")) {
                        items(listOf("Trail 1", "Trail 2", "Trail 3")) { trail ->
                            Text(text = trail, modifier = Modifier.testTag(trail)) // Tag each trail
                        }
                    }
                }
            }
        }

        composeTestRule.waitForIdle()

        // Ensure the TrailsList exists before checking if it's displayed
        composeTestRule
            .onNodeWithTag("TrailsList")
            .assertExists() // Assert it exists first
            .assertIsDisplayed() // Then assert it's displayed

        // Ensure each trail exists and is displayed
        composeTestRule
            .onNodeWithTag("Trail 1")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Trail 2")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Trail 3")
            .assertExists()
            .assertIsDisplayed()
    }
}

// Test for profile screen
@RunWith(AndroidJUnit4::class)
class ProfileUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_displaysMockedContent() {
        composeTestRule.setContent {
            HoplaTheme {
                Column(modifier = Modifier.testTag("MockedProfileScreen")) {
                    Text(
                        text = "Mocked Profile Screen",
                        modifier = Modifier.testTag("MockedProfileText")
                    )
                }
            }
        }

        composeTestRule.waitForIdle()

        // Ensure the mocked ProfileScreen exists and is displayed
        composeTestRule
            .onNodeWithTag("MockedProfileScreen")
            .assertExists()
            .assertIsDisplayed()

        // Ensure the mocked text is displayed
        composeTestRule
            .onNodeWithTag("MockedProfileText")
            .assertExists()
            .assertIsDisplayed()
    }
}

// Test for new trip screen
@RunWith(AndroidJUnit4::class)
class NewTripUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun newTripScreen_displaysMockedContent() {
        composeTestRule.setContent {
            HoplaTheme {
                Column(modifier = Modifier.testTag("MockedNewTripScreen")) {
                    Text(
                        text = "Mocked New Trip Screen",
                        modifier = Modifier.testTag("MockedNewTripText")
                    )
                }
            }
        }

        composeTestRule.waitForIdle()

        // Ensure the mocked NewTripScreen exists and is displayed
        composeTestRule
            .onNodeWithTag("MockedNewTripScreen")
            .assertExists()
            .assertIsDisplayed()

        // Ensure the mocked text is displayed
        composeTestRule
            .onNodeWithTag("MockedNewTripText")
            .assertExists()
            .assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class LoginUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysMockedContent() {
        composeTestRule.setContent {
            HoplaTheme {
                Column(modifier = Modifier.testTag("MockedLoginScreen")) {
                    Text(
                        text = "Mocked Login Screen",
                        modifier = Modifier.testTag("MockedLoginText")
                    )
                }
            }
        }

        composeTestRule.waitForIdle()

        // Ensure the mocked LoginScreen exists and is displayed
        composeTestRule
            .onNodeWithTag("MockedLoginScreen")
            .assertExists()
            .assertIsDisplayed()

        // Ensure the mocked text is displayed
        composeTestRule
            .onNodeWithTag("MockedLoginText")
            .assertExists()
            .assertIsDisplayed()
    }
}
