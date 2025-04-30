package com.example.hopla

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
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

