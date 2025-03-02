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
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.ui.theme.StarColor
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrailsScreen(navController: NavController) {
    var isMapClicked by remember { mutableStateOf(false) }
    var isCloseByClicked by remember { mutableStateOf(false) }
    var isFavoriteClicked by remember { mutableStateOf(false) }
    var isFollowingClicked by remember { mutableStateOf(false) }
    var isFiltersClicked by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isRouteClicked by remember { mutableStateOf(false) }
    var selectedContentBoxInfo by remember { mutableStateOf<ContentBoxInfo?>(null) }
    val selectedItems = remember { mutableStateOf(setOf<String>()) }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val testData = remember {
        mutableStateListOf(
            ContentBoxInfo(
                title = "Boredalstien",
                imageResource = setOf(R.drawable.stockimg1),
                isHeartClicked = false,
                starRating = 3,
                filters = Filters(setOf(presetFilters[0], presetFilters[1]), Difficulty.EASY),
                description = "This is a description of the trail"
            ),
            ContentBoxInfo(
                title = "Skogsstien",
                imageResource = setOf( R.drawable.stockimg2, R.drawable.stockimg2),
                isHeartClicked = true,
                starRating = 4,
                filters = Filters(setOf(presetFilters[0], presetFilters[1], presetFilters[2], presetFilters[3]), Difficulty.MEDIUM),
                description = "The hike is a paradise for nature lovers. It tends to get very busy during peak season, so it is best to go early in the morning or late in the afternoon."
            ),
            ContentBoxInfo(
                title = "Fjellstien",
                imageResource = setOf( R.drawable.stockimg1, R.drawable.stockimg2),
                isHeartClicked = false,
                starRating = 5,
                filters = Filters(setOf(presetFilters[1]), Difficulty.HARD),
                description = "This is a description of the trail"
            )
        )
    }

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
                            showOnlyFavorites = false
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
                            showOnlyFavorites = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isCloseByClicked) Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                            contentDescription = null
                        )
                    }
                    // Third icon: Favorite, everything else is set to false
                    IconButton(onClick = {
                        showOnlyFavorites = !showOnlyFavorites
                        if (showOnlyFavorites) {
                            isMapClicked = false
                            isCloseByClicked = false
                            isFavoriteClicked = false
                            isFollowingClicked = false
                        }
                    }) {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
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
                            showOnlyFavorites = false
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
            selectedContentBoxInfo?.let { contentBoxInfo ->
                RouteClicked(navController = navController, contentBoxInfo = contentBoxInfo, onBackClick = { isRouteClicked = false })
            }
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
            val routesToDisplay = if (showOnlyFavorites) {
                testData.filter { it.isHeartClicked }
            } else {
                testData
            }
            // Display the trails
            LazyColumn {
                items(routesToDisplay.size) { index ->
                    val contentBoxInfo = routesToDisplay[index]
                    ContentBox(
                        info = contentBoxInfo,
                        onHeartClick = {
                            val newState = !contentBoxInfo.isHeartClicked
                            testData[testData.indexOf(contentBoxInfo)] = contentBoxInfo.copy(isHeartClicked = newState)
                        },
                        onBoxClick = {
                            selectedContentBoxInfo = contentBoxInfo
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
fun ContentBox(info: ContentBoxInfo, onHeartClick: () -> Unit, onBoxClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(180.dp)
            .clickable(onClick = onBoxClick)
    ) {
        Column {
            // Main Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(140.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                val firstImageResource = info.imageResource.firstOrNull() ?: R.drawable.logo1
                Image(
                    painter = painterResource(id = firstImageResource),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Overlay for visibility
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                // Heart Icon
                IconButton(
                    onClick = onHeartClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = if (info.isHeartClicked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (info.isHeartClicked) Color(0xFFFF6666) else PrimaryWhite
                    )
                }
                // Star Rating
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < info.starRating) Icons.Filled.Star else Icons.TwoTone.Star,
                            contentDescription = null,
                            tint = StarColor
                        )
                    }
                }
                // Trip Title
                Text(
                    text = info.title,
                    color = PrimaryWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(5.dp)
                )
            }

            // White box for filters
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            ) {
                val scrollState = rememberScrollState()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(scrollState)
                ) {
                    // Display filter names
                    info.filters.filterStrings.forEach { filter ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Gray)
                                .background(Color.LightGray)
                                .padding(2.dp)
                                .height(18.dp)
                        ) {
                            Text(
                                text = filter.replaceFirstChar { it.uppercaseChar() },
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Display difficulty if available
                    info.filters.difficulty?.let {
                        Text(
                            text = it.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


// Function to display the trail that have been clicked
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RouteClicked(navController: NavController, contentBoxInfo: ContentBoxInfo, onBackClick: () -> Unit) {
    var currentImageIndex by remember { mutableIntStateOf(0) }
    var userRating by remember { mutableIntStateOf(0) }
    var showMessageBox by remember { mutableStateOf(false) }

    val images = contentBoxInfo.imageResource.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                .height(60.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            // Inner box header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Row in header to display items next to each other
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
                        text = contentBoxInfo.title,
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
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Column for the picture
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                .height(190.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
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
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                contentBoxInfo.filters.filterStrings.forEach { filter ->
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = filter, color = Color.Black, fontSize = 14.sp)
                                    }
                                }
                            }
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
                            .background(MaterialTheme.colorScheme.primary)
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
                            .background(MaterialTheme.colorScheme.primary)
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
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = contentBoxInfo.description, modifier = Modifier.padding(start = 8.dp))
                    }
                    // Assessment box with star rating set
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
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
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
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
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner, clickable box for latest update
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .clickable { showMessageBox = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.latest_update_about_the_route))
                        }
                    }
                }
            }
        }
    }

    if (showMessageBox) {
        Dialog(onDismissRequest = { showMessageBox = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.Black)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.latest_update_about_the_route),
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Bold
                    )

                    // Sample messages list
                    val messages = listOf(
                        Message(id = "1", content = "Trail is clear and well-maintained.", timestamp = System.currentTimeMillis(), imageUrl = "https://c8.alamy.com/compde/b7n5n2/schneebedeckte-umgesturzten-baum-in-einem-wald-in-haanja-estland-b7n5n2.jpg", username = "Bob"),
                        Message(id = "2", content = "Watch out for fallen branches.", timestamp = System.currentTimeMillis() - 3600000, username = "Alice")
                    )

                    LazyColumn {
                        items(messages) { message ->
                            MessageItem(message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        message.imageUrl?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(text = message.content)
        Text(
            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(message.timestamp)),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.End)
        )
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
fun UpdateScreen(navController: NavController) {
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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = stringResource(R.string.new_updates),
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
                Text(text = stringResource(R.string.comment), color = Color.Gray)
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
                    .padding(8.dp)
                    .size(40.dp),
                containerColor = Color(0xFFD9CFC4)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Comment")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
            onClick = {
                comment = "" // Clear the comment box
                navController.popBackStack() // Navigate back to the previous screen
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9CFC4))
        ) {
            Text(text = stringResource(R.string.publish), color = Color.Gray)
        }
    }
}
