package com.example.hopla

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.twotone.Star
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.ui.theme.StarColor

@Composable
fun TrailsScreen(navController: NavController) {
    var isMapClicked by remember { mutableStateOf(false) }
    var isCloseByClicked by remember { mutableStateOf(false) }
    var isFavoriteClicked by remember { mutableStateOf(false) }
    var isFollowingClicked by remember { mutableStateOf(false) }
    var isFiltersClicked by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var starRating by remember { mutableIntStateOf(3) }
    var numRoutes by remember { mutableIntStateOf(5) }
    var heartStates by remember { mutableStateOf(List(numRoutes) { false }) }
    var isRouteClicked by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateOf(setOf<String>()) }

    // Whole page
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 8.dp)
    ) {
        // If the user has not clicked a specific trail
        if (!isRouteClicked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp)
            ) {
                // Row to display the different icons next to each other
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // First icon: List or Map, everything else is set to false
                    IconButton(onClick = {
                        isMapClicked = !isMapClicked
                        if (isMapClicked) {
                            isCloseByClicked = false
                            isFavoriteClicked = false
                            isFollowingClicked = false
                            isFiltersClicked = false
                            isDropdownExpanded = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isMapClicked) Icons.Outlined.Check else Icons.AutoMirrored.Outlined.List,
                            contentDescription = null
                        )
                    }
                    // Second icon: Location/close to you, everything else is set to false
                    IconButton(onClick = {
                        isCloseByClicked = !isCloseByClicked
                        if (isCloseByClicked) {
                            isMapClicked = false
                            isFavoriteClicked = false
                            isFollowingClicked = false
                            isFiltersClicked = false
                            isDropdownExpanded = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isCloseByClicked) Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                            contentDescription = null
                        )
                    }
                    // Third icon: Favorite, everything else is set to false
                    IconButton(onClick = {
                        isFavoriteClicked = !isFavoriteClicked
                        if (isFavoriteClicked) {
                            isMapClicked = false
                            isCloseByClicked = false
                            isFollowingClicked = false
                            isFiltersClicked = false
                            isDropdownExpanded = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavoriteClicked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null
                        )
                    }
                    // Fourth icon: Following, everything else is set to false
                    IconButton(onClick = {
                        isFollowingClicked = !isFollowingClicked
                        if (isFollowingClicked) {
                            isMapClicked = false
                            isCloseByClicked = false
                            isFavoriteClicked = false
                            isFiltersClicked = false
                            isDropdownExpanded = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isFollowingClicked) Icons.Filled.Star else Icons.TwoTone.Star,
                            contentDescription = null
                        )
                    }
                    // Fifth icon: Filters. Column->Box with a dropdown menu
                    Column {
                        Box {
                            IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Filters"
                                )
                            }
                            // Create the dropdown menu
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                val items = listOf("Parkering", "Lite trafikk", "Asfalt", "Grus") // All items in the dropdown menue
                                items.forEach { item ->
                                    val isSelected = selectedItems.value.contains(item)
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Check,
                                                        contentDescription = null,
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                Text(
                                                    text = item,
                                                    color = if (isSelected) Color.Black else Color.DarkGray
                                                )
                                            }
                                        },
                                        onClick = {
                                            val newSet = selectedItems.value.toMutableSet()
                                            if (newSet.contains(item)) {
                                                newSet.remove(item)
                                            } else {
                                                newSet.add(item)
                                            }
                                            selectedItems.value = newSet
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // If the user has clicked a specific trail, display the function RouteClicked
        if (isRouteClicked) {
            RouteClicked(navController = navController, onBackClick = { isRouteClicked = false })
        // If the user has clicked the map icon, display the map
        } else if (isMapClicked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Map", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        // If the user has clicked the location icon, display the trails close to you
        } else {
            val favoriteRoutes = heartStates.mapIndexedNotNull { index, isFavorite ->
                if (isFavorite) index else null
            }
            // Display the trails
            LazyColumn {
                val routesToDisplay = if (isFavoriteClicked) favoriteRoutes else (0 until numRoutes).toList()

                items(routesToDisplay.size) { displayIndex ->
                    val actualIndex = routesToDisplay[displayIndex]
                    ContentBox(
                        isHeartClicked = heartStates[actualIndex],
                        starRating = starRating,
                        onHeartClick = {
                            heartStates = heartStates.toMutableList().apply {
                                this[actualIndex] = !this[actualIndex]
                            }
                        },
                        onBoxClick = {
                            isRouteClicked = true
                        }
                    )
                }
            }
        }
    }
}

// Function to display the content of the trails (main page for all trails)
@Composable
fun ContentBox(isHeartClicked: Boolean, starRating: Int, onHeartClick: () -> Unit, onBoxClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(150.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onBoxClick)
    ) {
        Column {
            // MainBox
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(95.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                //Image of the trip
                Image(
                    painter = painterResource(id = R.drawable.stockimg1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Semi transparent overlay for easier visibility of icons on top of the image
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                // Heart icon (clickable)
                IconButton(
                    onClick = onHeartClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = if (isHeartClicked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = PrimaryWhite
                    )
                }
                // Star rating of the trails
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < starRating) Icons.Filled.Star else Icons.TwoTone.Star,
                            contentDescription = null,
                            tint = StarColor
                        )
                    }
                }
            }
            // Text box below the image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp, start = 5.dp, end = 5.dp)
                    .height(45.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Text("Boredalstien", color = PrimaryWhite, modifier = Modifier.padding(5.dp))
            }
        }
    }
}

// Function to display the trail that have been clicked
@Composable
fun RouteClicked(navController: NavController, onBackClick: () -> Unit) {
    var currentImageIndex by remember { mutableIntStateOf(0) }
    var userRating by remember { mutableIntStateOf(0) }
    val images = listOf(R.drawable.stockimg1, R.drawable.stockimg2)

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                .height(60.dp)
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            // Inner box header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Row in header to display items next to eachother
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back button that takes the user back to the main page of trails
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    // Text in the header
                    Text(
                        text = "Boredalstien",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

        // Scrollable content starts here
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            item {
                // Pictures + description box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                ) {
                    // Column for the picture
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                .height(190.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        ) {
                            // Display the images
                            Image(
                                painter = painterResource(id = images[currentImageIndex]),
                                contentDescription = "Route Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Make the icons in the pictures buttons
                            IconButton(
                                onClick = {
                                    currentImageIndex = (currentImageIndex - 1 + images.size) % images.size
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)
                            ) {
                                // Click left icon
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                    contentDescription = "Left Arrow",
                                    tint = PrimaryBlack
                                )
                            }
                            // Make the icons in the pictures buttons
                            IconButton(
                                onClick = {
                                    currentImageIndex = (currentImageIndex + 1) % images.size
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp)
                            ) {
                                // Click right icon
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                    contentDescription = "Right Arrow",
                                    tint = PrimaryBlack
                                )
                            }
                        }
                        // Description below pictures
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Asfalt, Grus, Parkering", color = PrimaryWhite)
                        }
                    }
                }
            }

            item {
                // Row for start trip and new updates boxes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Start trip clickable box
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.3f)
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable { /* Handle click */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.start_trip))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // New updates clickable box
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.7f)
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable {  navController.navigate("update_screen")  },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.new_updates))
                    }
                }
            }

            item {
                // Description Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = stringResource(R.string.easy_trip_for_everyone_Parking), modifier = Modifier.padding(start = 8.dp))
                    }
                    // Assessment box with star rating set
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.assessment))
                            Row {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = StarColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    // My assessment box wih star rating, changeable
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.my_assessment))
                            StarRating(rating = userRating, onRatingChanged = { userRating = it })
                        }
                    }
                    // Outer box of latest update
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner, clickable box for latest update
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable { /* Handle click */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.latest_update_about_the_route))
                        }
                    }
                }
            }
        }
    }
}

// Function to be able to change the rating
@Composable
fun StarRating(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.TwoTone.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onRatingChanged(index + 1) }
            )
        }
    }
}

// Function to display the update screen where user can add their own update about the route
@Composable
fun UpdateScreen() {
    var location by remember { mutableStateOf("Boredalstien") }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE6DD)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Box (Title + Back Button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB8A999))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Handle Back Action */ }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Ny oppdatering",
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Location Field (Read-only)
        TextField(
            value = location,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comment Box with Floating Add Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(150.dp)
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(text = "Kommentar:", color = Color.Gray)
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            FloatingActionButton(
                onClick = { /* Handle Add Action */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                containerColor = Color(0xFFD9CFC4) // Slightly transparent button color
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Comment")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
            onClick = { /* Handle Publish Action */ },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9CFC4))
        ) {
            Text("Publiser", color = Color.Gray)
        }
    }
}

