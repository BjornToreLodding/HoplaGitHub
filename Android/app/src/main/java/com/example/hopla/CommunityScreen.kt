package com.example.hopla

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Composable function to display the community screen
@Composable
fun CommunityScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var showLikedOnly by remember { mutableStateOf(false) }
    val likedCommunities = remember { mutableStateListOf<Community>() }
    // List of community groups (replace with database)
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

    // Filter the communities based on the search query and liked status
    val filteredCommunities = communities.filter {
        it.name.contains(searchQuery, ignoreCase = true) &&
                (!showLikedOnly || likedCommunities.contains(it))
    }

    // Column for the community screen (whole screen)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top text for filtering the groups based on position and liked status
            TopTextCommunity(currentPage = if (showLikedOnly) "liked" else "position") { showLikedOnly = it }
            // A search bar to search for community groups
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
            // Scrollview for displaying the community groups
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 1.dp)
            ) {
                // Display the community groups
                items(filteredCommunities) { community ->
                    CommunityCard(community, navController, likedCommunities)
                }
            }
        }
        // Add button to add a new community group
        AddButton(onClick = { navController.navigate("addCommunityScreen") })
    }
}

// Composable function to display a community group card
@Composable
fun CommunityCard(community: Community, navController: NavController, likedCommunities: MutableList<Community>) {
    var isLiked by remember { mutableStateOf(likedCommunities.contains(community)) }

    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .clickable { navController.navigate("communityDetail/${community.name}") }
    ) {
        Box {
            Image(
                painter = painterResource(id = community.imageResource),
                contentDescription = community.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = community.name,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
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
                        tint = if (isLiked) colorResource(id = R.color.likedHeart) else Color.White,
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
            // Box for the position text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange(false) }
                    .background(if (currentPage == "position") colorResource(id = R.color.transparentWhite) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                // The position icon
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = stringResource(R.string.position),
                )
            }
            // Box for the liked text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange(true) }
                    .background(if (currentPage == "liked") colorResource(id = R.color.transparentWhite) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                // The liked icon
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.liked),
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
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    // Load messages from the database
    LaunchedEffect(community.name) {
        val fetchedMessages = fetchMessages(community.name)
        messages.addAll(fetchedMessages)
    }

    // Whole screen
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Box for the header of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
        ) {
            // Image of the community group
            Image(
                painter = painterResource(id = community.imageResource),
                contentDescription = community.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Transparent box as overlay over the image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
            // Row for the back button, title, and info button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                // Title of the community group
                Text(
                    text = community.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                // Info button to show details about the community
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.info)
                    )
                }
            }
        }
        // Column for the messages posted in the group
        MessageBox(messages, newMessage, onMessageChange = { newMessage = it }, community)
    }

    // Show a dialog with the description of the community group and a report button
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = community.description,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { isDropdownExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Report") },
                                onClick = {
                                    isDropdownExpanded = false
                                    showReportDialog = true
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }


    if (showReportDialog) {
        ReportDialog(onDismiss = { showReportDialog = false })
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
    // Return the community group with the given name
    return communities.find { it.name == name }
}

// Add a new community group screen
@Composable
fun AddCommunityScreen(navController: NavController, onAdd: (Community) -> Unit) {
    var name by remember { mutableStateOf("") }
    var imageResource by remember { mutableIntStateOf(0) }
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    // Column for the add community screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header of the screen
        ScreenHeader(navController = navController, headerText = stringResource(R.string.add_new_community))

        Spacer(modifier = Modifier.height(16.dp))

        // Input fields for the name and description of the community group
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

        // Image picker to select an image for the community group
        if (imageBitmap != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp)
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

        // Clickable boxes for Private, Public, and Friends
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(stringResource(R.string.private_string), stringResource(R.string.public_string), stringResource(R.string.friends)).forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clickable { selectedOption = option }
                        .background(
                            if (selectedOption == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (selectedOption == option) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to add the new community group, that makes sure the input fields are filled
        Button(
            onClick = {
                if (selectedOption != null) {
                    val newCommunity = Community(name, imageResource, description)
                    onAdd(newCommunity)
                    navController.popBackStack()
                } else {
                    // Show a message to select an option
                }
            },
            enabled = name.isNotBlank() && description.isNotBlank() && selectedOption != null
        ) {
            Text(text = stringResource(R.string.add_new_community))
        }
    }
}

