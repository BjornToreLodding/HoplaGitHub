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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
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

        // If the "Map" toggle is active, show only "Map" text
        if (isMapClicked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Map", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else {
            // Regular content display
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
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ContentBox(isHeartClicked: Boolean, starRating: Int, onHeartClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(150.dp)
            .background(Color.Gray)
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
                    "Title",
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
                Text("Description", color = Color.Gray)
            }
        }
    }
}