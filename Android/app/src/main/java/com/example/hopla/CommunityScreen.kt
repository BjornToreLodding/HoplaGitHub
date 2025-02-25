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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.TextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

// Composable function to display the community screen
@Composable
fun CommunityScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var showLikedOnly by remember { mutableStateOf(false) }
    val likedCommunities = remember { mutableStateListOf<Community>() }
    val communities = listOf(
        Community(
            name = "Horse community",
            imageResource = R.drawable.stockimg1,
            description = "A stable for horses ONLY"
        ),
        Community(
            name = "Ponny community",
            imageResource = R.drawable.stockimg2,
            description = "A stable for ponies ONLY"
        ),
        Community(
            name = "Donkey community",
            imageResource = R.drawable.stockimg1,
            description = "A stable for donkeys ONLY"
        )
    )
    val filteredCommunities = communities.filter {
        it.name.contains(searchQuery, ignoreCase = true) &&
                (!showLikedOnly || likedCommunities.contains(it))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopTextCommunity(currentPage = if (showLikedOnly) "liked" else "position") { showLikedOnly = it }
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(filteredCommunities) { community ->
                    CommunityCard(community, navController, likedCommunities)
                }
            }
        }
        AddButton(onClick = { navController.navigate("addCommunityScreen") })
    }
}

// Composable function to display a community group card
@Composable
fun CommunityCard(community: Community, navController: NavController, likedCommunities: MutableList<Community>) {
    var isLiked by remember { mutableStateOf(likedCommunities.contains(community)) }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("communityDetail/${community.name}") }
    ) {
        Box {
            Column {
                Image(
                    painter = painterResource(id = community.imageResource),
                    contentDescription = community.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Text(
                    text = community.name,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.background, shape = CircleShape)
            ) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        if (isLiked) {
                            likedCommunities.add(community)
                        } else {
                            likedCommunities.remove(community)
                        }
                    }
                ) {
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
fun TopTextCommunity(currentPage: String, onShowLikedOnlyChange: (Boolean) -> Unit) {
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
                    .clickable { onShowLikedOnlyChange(false) }
                    .background(if (currentPage == "position") colorResource(id = R.color.transparentWhite) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.position),
                    fontSize = 10.sp,
                    color = if (currentPage == "position") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange(true) }
                    .background(if (currentPage == "liked") colorResource(id = R.color.transparentWhite) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.liked),
                    fontSize = 10.sp,
                    color = if (currentPage == "liked") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// Details about a specific community
@Composable
fun CommunityDetailScreen(navController: NavController, community: Community) {
    var showDialog by remember { mutableStateOf(false) }
    var newMessage by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }

    // Load messages from the database
    LaunchedEffect(community.name) {
        val fetchedMessages = fetchMessages(community.name)
        messages.addAll(fetchedMessages)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .border(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Image(
                painter = painterResource(id = community.imageResource),
                contentDescription = community.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Text(
                    text = community.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.info)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        MessageBox(messages, newMessage, onMessageChange = { newMessage = it }, community)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = { Text(text = community.description) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }
}

// Function to retrieve the CommunityGroup object based on the communityName
@Composable
fun getCommunityByName(name: String): Community? {
    val communities = listOf(
        Community(
            name = "Horse community",
            imageResource = R.drawable.stockimg1,
            description = "A community for horse lovers ONLY"
        ),
        Community(
            name = "Ponny community",
            imageResource = R.drawable.stockimg2,
            description = "A community for ponny lovers ONLY"
        ),
        Community(
            name = "Donkey community",
            imageResource = R.drawable.stockimg1,
            description = "A community for donkey lovers ONLY"
        )
    )
    return communities.find { it.name == name }
}

// Add a new community group screen
@Composable
fun AddCommunityScreen(navController: NavController, onAdd: (Community) -> Unit) {
    var name by remember { mutableStateOf("") }
    var imageResource by remember { mutableIntStateOf(0) }
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

