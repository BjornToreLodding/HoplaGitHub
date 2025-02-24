package com.example.hopla

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.material.TextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

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
            TopTextCommunity { showLikedOnly = it }
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
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
        AddButton(onClick = { navController.navigate("addCommunityScreen") })
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
fun TopTextCommunity(onShowLikedOnlyChange: (Boolean) -> Unit) {
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
fun AddCommunityScreen(navController: NavController, onAdd: (Community) -> Unit) {
    var name by remember { mutableStateOf("") }
    var imageResource by remember { mutableStateOf(0) }
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(navController = navController, headerText = stringResource(R.string.add_new_community))

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Add option to remove image if it is added
        if (imageBitmap != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                )
                IconButton(onClick = { imageBitmap = null }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image"
                    )
                }
            }
        }
        ImagePicker(
            onImageSelected = { bitmap -> imageBitmap = bitmap },
            text = if (imageBitmap == null) stringResource(R.string.add_image) else stringResource(R.string.change_image)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val newCommunity = Community(name, imageResource, description)
            onAdd(newCommunity)
            navController.popBackStack()
        }) {
            Text(text = stringResource(R.string.add_new_community))
        }
    }
}

