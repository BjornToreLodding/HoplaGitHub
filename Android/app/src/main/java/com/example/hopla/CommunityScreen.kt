package com.example.hopla

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Data class to represent a community group
data class CommunityGroup(
    val image: Painter,
    val name: String,
    val description: String
)

// Composable function to display the community screen
@Composable
fun CommunityScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var showLikedOnly by remember { mutableStateOf(false) }
    val likedGroups = remember { mutableStateListOf<CommunityGroup>() }
    val groups = listOf(
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "Horse community",
            "A modern stable with excellent facilities."
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg2),
            "Ponny community",
            "SANDNES OG JÆREN RIDEKLUBB"
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "Donkey community",
            "BÆRUM RIDEKLUBB"
        )
    )
    val filteredGroups = groups.filter {
        it.name.contains(searchQuery, ignoreCase = true) &&
                (!showLikedOnly || likedGroups.contains(it))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopTextCommunity(showLikedOnly) { showLikedOnly = it }
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(text = stringResource(R.string.search)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(filteredGroups) { group ->
                    CommunityCard(group, navController, likedGroups)
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("addCommunityScreen") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

// Composable function to display a community group card
@Composable
fun CommunityCard(group: CommunityGroup, navController: NavController, likedGroups: MutableList<CommunityGroup>) {
    var isLiked by remember { mutableStateOf(likedGroups.contains(group)) }

    // Display the community group card
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("communityDetail/${group.name}") }
    ) {
        // Inner box for the group card
        Box {
            Column {
                // The image of the group
                Image(
                    painter = group.image,
                    contentDescription = group.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                // The name of the group
                Text(
                    text = group.name,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            // Box for the like button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.background, shape = CircleShape)
            ) {
                // Like button icon
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        if (isLiked) {
                            likedGroups.add(group)
                        } else {
                            likedGroups.remove(group)
                        }
                    }
                ) {
                    // Icon changing from outlined to filled based on the like status
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isLiked) stringResource(R.string.liked) else stringResource(R.string.not_liked),
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

// Top text for filtering the groups based on position and liked status
@Composable
fun TopTextCommunity(showLikedOnly: Boolean, onShowLikedOnlyChange: (Boolean) -> Unit) {
    // Column for the top text
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(3.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.position),
                    fontSize = 10.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.liked),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun CommunityDetailScreen(navController: NavController, communityGroup: CommunityGroup) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Text(
                    text = communityGroup.name,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = communityGroup.description,
            fontSize = 16.sp
        )
    }
}

@Composable
// Function to retrieve the CommunityGroup object based on the communityName
fun getCommunityGroupByName(name: String): CommunityGroup? {
    val groups = listOf(
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "This is the first group.",
            "A modern stable with excellent facilities."
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg2),
            "This is the second group.",
            "SANDNES OG JÆREN RIDEKLUBB"
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "This is the third group.",
            "BÆRUM RIDEKLUBB"
        )
    )
    return groups.find { it.name == name }
}

// Add a new community group screen
@Composable
fun AddCommunityScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Text(
                    text = "Add",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}