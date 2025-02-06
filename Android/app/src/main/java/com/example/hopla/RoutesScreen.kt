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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.stringResource

@Composable
fun RoutesScreen() {
    var isMapClicked by remember { mutableStateOf(false) }
    var isCloseByClicked by remember { mutableStateOf(false) }
    var isFavoriteClicked by remember { mutableStateOf(false) }
    var isFollwingClicked by remember { mutableStateOf(false) }
    var isFiltersClicked by remember { mutableStateOf(false) }
    var starRating by remember { mutableIntStateOf(3) }
    var numRoutes by remember { mutableIntStateOf(5) }
    var heartStates by remember { mutableStateOf(List(numRoutes) { false }) }
    var isRouteClicked by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        if (!isRouteClicked) {
            // Top box with icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.Gray)
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        isMapClicked = !isMapClicked
                        if (isMapClicked) {
                            isCloseByClicked = false
                            isFavoriteClicked = false
                            isFollwingClicked = false
                            isFiltersClicked = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isMapClicked) Icons.Outlined.Check else Icons.Outlined.List,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        isCloseByClicked = !isCloseByClicked
                        if (isCloseByClicked) {
                            isMapClicked = false
                            isFavoriteClicked = false
                            isFollwingClicked = false
                            isFiltersClicked = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isCloseByClicked) Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        isFavoriteClicked = !isFavoriteClicked
                        if (isFavoriteClicked) {
                            isMapClicked = false
                            isCloseByClicked = false
                            isFollwingClicked = false
                            isFiltersClicked = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavoriteClicked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        isFollwingClicked = !isFollwingClicked
                        if (isFollwingClicked) {
                            isMapClicked = false
                            isCloseByClicked = false
                            isFavoriteClicked = false
                            isFiltersClicked = false
                        }
                    }) {
                        Icon(
                            imageVector = if (isFollwingClicked) Icons.Filled.Star else Icons.TwoTone.Star,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        isFiltersClicked = !isFiltersClicked
                        if (isFiltersClicked) {
                            isMapClicked = false
                            isCloseByClicked = false
                            isFavoriteClicked = false
                            isFollwingClicked = false
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        if (isRouteClicked) {
            RouteClicked(onBackClick = { isRouteClicked = false })
        } else if (isMapClicked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Map", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else {
            val favoriteRoutes = heartStates.mapIndexedNotNull { index, isFavorite ->
                if (isFavorite) index else null
            }

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

@Composable
fun ContentBox(isHeartClicked: Boolean, starRating: Int, onHeartClick: () -> Unit, onBoxClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(150.dp)
            .background(Color.Gray)
            .clickable(onClick = onBoxClick)
    ) {
        Column {
            // MainBox
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(95.dp)
                    .background(Color.Blue)
            ) {
                Text(
                    "Boredalstien",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(5.dp)
                )
                IconButton(
                    onClick = onHeartClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = if (isHeartClicked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < starRating) Icons.Filled.Star else Icons.TwoTone.Star,
                            contentDescription = null,
                            tint = Color.Yellow
                        )
                    }
                }
            }
            // SmallBox
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp, start = 5.dp, end = 5.dp)
                    .height(45.dp)
                    .background(Color.Red)
            ) {
                Text("Asfalt, Grus, Parkering", color = Color.Gray)
            }
        }
    }
}

@Composable
fun RouteClicked(onBackClick: () -> Unit) {
    var currentImageIndex by remember { mutableStateOf(0) }
    val images = listOf(R.drawable.stockimg1, R.drawable.stockimg2)

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                .height(60.dp)
                .background(Color.Green)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .fillMaxHeight()
                    .background(Color.Yellow)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
                // Pictures + description
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                        .height(250.dp)
                        .background(Color.Green)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                .height(190.dp)
                                .background(Color.Blue)
                        ) {
                            Image(
                                painter = painterResource(id = images[currentImageIndex]),
                                contentDescription = "Route Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = {
                                    currentImageIndex = (currentImageIndex - 1 + images.size) % images.size
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                                    contentDescription = "Left Arrow",
                                    tint = Color.Black
                                )
                            }
                            IconButton(
                                onClick = {
                                    currentImageIndex = (currentImageIndex + 1) % images.size
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowRight,
                                    contentDescription = "Right Arrow",
                                    tint = Color.Black
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                                .height(50.dp)
                                .background(Color.Red)
                        ) {
                            Text("Asfalt, Grus, Parkering", color = Color.Gray)
                        }
                    }
                }
            }

            item {
                // Start tur/ny oppdatering
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.3f)
                            .background(Color.Cyan)
                            .clickable { /* Handle click */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.start_trip))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.7f)
                            .background(Color.Magenta)
                            .clickable { /* Handle click */ },
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
                            .background(Color.Magenta),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = stringResource(R.string.easy_trip_for_everyone_Parking), modifier = Modifier.padding(start = 8.dp))
                    }
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(Color.Magenta),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = stringResource(R.string.assessment), modifier = Modifier.padding(start = 8.dp))
                    }
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(Color.Magenta),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = stringResource(R.string.my_assessment), modifier = Modifier.padding(start = 8.dp))
                    }
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(Color.Magenta),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(Color.Green)
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
