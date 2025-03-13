package com.example.hopla.profile

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.AddButton
import com.example.hopla.Friend
import com.example.hopla.FriendProfile
import com.example.hopla.Hike
import com.example.hopla.OtherUsers
import com.example.hopla.Person
import com.example.hopla.PersonStatus
import com.example.hopla.R
import com.example.hopla.ReportDialog
import com.example.hopla.ScreenHeader
import com.example.hopla.SearchBar
import com.example.hopla.Trip
import com.example.hopla.UserItem
import com.example.hopla.UserSession
import com.example.hopla.fetchFollowing
import com.example.hopla.fetchFriendProfile
import com.example.hopla.fetchFriends
import com.example.hopla.fetchUserFriends
import com.example.hopla.formatDate
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryWhite
import kotlinx.coroutines.launch

// Details about a person
@Composable
fun UsersProfileScreen(navController: NavController, userId: String) {
    var friendProfile by remember { mutableStateOf<FriendProfile?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    var showFullDescription by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var userHikes by remember { mutableStateOf<List<Hike>>(emptyList()) }

    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                friendProfile = fetchFriendProfile(userId, token)
            } catch (e: Exception) {
                Log.e("FriendProfileScreen", "Error fetching friend profile", e)
            }
        }
    }

    fun loadMoreHikes() {
        coroutineScope.launch {
            try {
                pageNumber += 1
                val updatedProfile = fetchFriendProfile(userId, token)
                userHikes = userHikes + updatedProfile.userHikes
            } catch (e: Exception) {
                Log.e("FriendProfileScreen", "Error loading more hikes", e)
            }
        }
    }

    friendProfile?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = profile.alias,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.block_user)) },
                        onClick = { /* Handle block user */ }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.report_user)) },
                        onClick = { showReportDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile.pictureUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(5.dp, PrimaryWhite, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = profile.name, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = stringResource(R.string.friends) + ": ${profile.friendsCount}",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                        modifier = Modifier.clickable {
                            navController.navigate("friends_list/${profile.id}")
                        }
                    )
                    Text(
                        text = stringResource(R.string.horses) + ": ${profile.horseCount}",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                        modifier = Modifier.clickable {
                            navController.navigate("user_horses/$userId")
                        }
                    )
                    Text(
                        text = stringResource(R.string.relation_status) + ": ${profile.relationStatus}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
                    .animateContentSize() // Animate size changes
            ) {
                Column {
                    Text(
                        text = if (showFullDescription) profile.description
                            ?: "" else profile.description?.take(100)?.plus("...") ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (showFullDescription) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if ((profile.description?.length ?: 0) > 100) {
                        IconButton(onClick = { showFullDescription = !showFullDescription }) {
                            Icon(
                                imageVector = if (showFullDescription) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (showFullDescription) "Show less" else "Show more"
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.trips),
                style = MaterialTheme.typography.headlineSmall
            )
            profile.userHikes.forEach { hike ->
                HikeItem(hike)
            }
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(100.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { loadMoreHikes() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Load More",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryBlack
                )
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val formattedDate = profile.created_at?.let { formatDate(it) } ?: "Unknown"
                Text(
                    text = "Created at: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                )
            }
        }
    } ?: run {
        // Show a loading indicator
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (showReportDialog) {
        ReportDialog(onDismiss = { showReportDialog = false })
    }
}

@Composable
fun FriendsListScreen(navController: NavController, userId: String) {
    var friends by remember { mutableStateOf<List<Friend>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                friends = fetchUserFriends(userId, token)
            } catch (e: Exception) {
                Log.e("FriendsListScreen", "Error fetching friends", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, stringResource(R.string.friends))

            if (friends.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(friends) { friend ->
                        UserItemComposable(friend, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun PersonDetailScreen(navController: NavController, person: Person) {
    var showConfirmationDialogFriend by remember { mutableStateOf(false) }
    var showConfirmationDialogFollowing by remember { mutableStateOf(false) }
    var showConfirmationDialogPending by remember { mutableStateOf(false) }
    val trips = listOf(
        Trip("Trip to the mountains", "2023-10-01", "10", "2", R.drawable.stockimg1),
        Trip("City walk", "2023-09-15", "5", "1", R.drawable.stockimg2),
        Trip("Beach run", "2023-08-20", "8", "1.5", R.drawable.stockimg2)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.back)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = person.imageResource),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(5.dp, PrimaryWhite, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = person.name, style = MaterialTheme.typography.headlineLarge)
                    Text(
                        text = stringResource(R.string.friends) + " : 5",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = stringResource(R.string.trips_added) + " : 3",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = person.status.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    // If the user is a friend
                    if (person.status == PersonStatus.FRIEND) {
                        Text(
                            text = stringResource(R.string.remove_friend),
                            modifier = Modifier.clickable {
                                showConfirmationDialogFriend = true
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    // If the user is following that person
                    if (person.status == PersonStatus.FOLLOWING) {
                        Text(
                            text = stringResource(R.string.unfollow),
                            modifier = Modifier.clickable {
                                showConfirmationDialogFollowing = true
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    // If the user has a pending friend request
                    if (person.status == PersonStatus.PENDING) {
                        Text(
                            text = stringResource(R.string.friendrequest_pending),
                            modifier = Modifier.clickable {
                                showConfirmationDialogPending = true
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    // If the user has no relation to that user
                    if (person.status == PersonStatus.NONE) {
                        Button(onClick = { /* Handle follow logic here */ }) {
                            Text(text = stringResource(R.string.follow))
                        }
                        Button(onClick = { /* Handle follow logic here */ }) {
                            Text(text = stringResource(R.string.add_friend))
                        }
                    }
                    if (showConfirmationDialogFriend) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialogFriend = false },
                            title = { Text(text = stringResource(R.string.remove_friend)) },
                            text = { Text(text = stringResource(R.string.delete_friend_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialogFriend = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialogFriend = false }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                    if (showConfirmationDialogFollowing) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialogFollowing = false },
                            title = { Text(text = stringResource(R.string.unfollow)) },
                            text = { Text(text = stringResource(R.string.unfollow_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialogFollowing = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialogFollowing = false }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                    if (showConfirmationDialogPending) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialogPending = false },
                            title = { Text(text = stringResource(R.string.friendrequest_pending)) },
                            text = { Text(text = stringResource(R.string.remove_request_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialogPending = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialogPending = false }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(trips) { trip ->
                    TripItem(trip)
                }
            }
        }
    }
}

@Composable
fun UserListScreen(
    navController: NavController,
    title: String,
    fetchUsers: suspend (String) -> List<UserItem>
) {
    var users by remember { mutableStateOf<List<UserItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                users = fetchUsers(UserSession.token)
            } catch (e: Exception) {
                Log.e("UserListScreen", "Error fetching users", e)
            }
        }
    }

    val filteredUsers = users.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.alias.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, title)

            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            if (filteredUsers.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_matches),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredUsers) { user ->
                        UserItemComposable(user, navController)
                    }
                }
            }
        }
        AddButton(onClick = { navController.navigate("addFriendScreen") })
    }
}

@Composable
fun FriendsScreen(navController: NavController) {
    UserListScreen(
        navController = navController,
        title = stringResource(R.string.friends),
        fetchUsers = { token -> fetchFriends(token) }
    )
}

@Composable
fun FollowingScreen(navController: NavController) {
    UserListScreen(
        navController = navController,
        title = stringResource(R.string.following),
        fetchUsers = { token -> fetchFollowing(token) }
    )
}

@Composable
fun UserItemComposable(userItem: UserItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .padding(16.dp)
            .clickable {
                navController.navigate("friend_profile/${userItem.id}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = userItem.pictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = userItem.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = userItem.alias, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun UserItemComposable(user: OtherUsers, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .padding(16.dp)
            .clickable { navController.navigate("friend_profile/${user.id}") }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user.pictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(64.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = user.alias, style = MaterialTheme.typography.bodySmall)
        }
    }
}