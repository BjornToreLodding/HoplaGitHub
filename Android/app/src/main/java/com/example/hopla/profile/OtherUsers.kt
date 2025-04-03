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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.fetchFollowing
import com.example.hopla.apiService.fetchFriendProfile
import com.example.hopla.apiService.fetchFriends
import com.example.hopla.apiService.fetchUserFriends
import com.example.hopla.apiService.sendUserRelationRequest
import com.example.hopla.apiService.sendUserRelationRequestDelete
import com.example.hopla.apiService.sendUserRelationRequestPut
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.dropdownMenuTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.ui.theme.underlinedTextStyleSmall
import com.example.hopla.universalData.AddButton
import com.example.hopla.universalData.CustomButton
import com.example.hopla.universalData.Friend
import com.example.hopla.universalData.FriendProfile
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.OtherUsers
import com.example.hopla.universalData.PersonStatus
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.SearchBar
import com.example.hopla.universalData.UserItem
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.formatDate
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
    var reloadTrigger by remember { mutableIntStateOf(0) }
    var showBlockConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId, reloadTrigger) {
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
                        style = headerTextStyleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options", tint = MaterialTheme.colorScheme.secondary)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.block_user), style = dropdownMenuTextStyle, color = MaterialTheme.colorScheme.secondary) },
                        onClick = { showBlockConfirmationDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.report_user), style = dropdownMenuTextStyle, color = MaterialTheme.colorScheme.secondary) },
                        onClick = { showReportDialog = true }
                    )
                }
            }

            if (showBlockConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showBlockConfirmationDialog = false },
                    title = { Text(text = stringResource(R.string.block_user)) },
                    text = { Text(text = stringResource(R.string.are_you_sure_block)) },
                    confirmButton = {
                        TextButton(onClick = {
                            showBlockConfirmationDialog = false
                            val request = UserRelationChangeRequest(
                                TargetUserId = userId,
                                Status = "BLOCK"
                            )
                            coroutineScope.launch {
                                try {
                                    val response = if (profile.relationStatus == PersonStatus.NONE.name) {
                                        sendUserRelationRequest(UserSession.token, request)
                                    } else {
                                        sendUserRelationRequestPut(UserSession.token, request)
                                    }
                                    Log.d("changeRelations", "Response: ${response.message}")
                                    reloadTrigger++
                                } catch (e: Exception) {
                                    Log.e("changeRelations", "Error sending user relation request", e)
                                    reloadTrigger++
                                }
                            }
                        }) {
                            Text(text = stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBlockConfirmationDialog = false }) {
                            Text(text = stringResource(R.string.no))
                        }
                    }
                )
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
                        .border(5.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = profile.name, style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary)
                    if (profile.relationStatus == PersonStatus.FRIENDS.name) {
                        Text(
                            text = stringResource(R.string.friends) + ": ${profile.friendsCount}",
                            style = underlinedTextStyleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.clickable {
                                navController.navigate("friends_list/${profile.id}")
                            }
                        )
                        Text(
                            text = stringResource(R.string.horses) + ": ${profile.horseCount}",
                            style = underlinedTextStyleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.clickable {
                                navController.navigate("user_horses/$userId")
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    profile.relationStatus?.let { Text(text = it, style = underheaderTextStyle) }
                    if (profile.relationStatus == PersonStatus.PENDING.name)  {
                        CustomButton(text = stringResource(R.string.pending)) { /*Handle button click*/ }
                    }
                    if (profile.relationStatus == PersonStatus.FOLLOWING.name) {
                        CustomButton(text = stringResource(R.string.stop_following)) {
                            val deleteRequest = UserRelationChangeRequest(
                                TargetUserId = userId
                            )
                            coroutineScope.launch {
                                try {
                                    val response = sendUserRelationRequestDelete(UserSession.token, deleteRequest)
                                    Log.d("changeRelations", "Response: $response")
                                    reloadTrigger++
                                } catch (e: Exception) {
                                    Log.e("changeRelations", "Error stop following", e)
                                }
                            }
                        }
                        CustomButton(text = stringResource(R.string.add_friend)) {
                            val request = UserRelationChangeRequest(
                                TargetUserId = userId,
                                Status = "PENDING"
                            )
                            coroutineScope.launch {
                                try {
                                    val response = sendUserRelationRequest(UserSession.token, request)
                                    Log.d("changeRelations", "Response: ${response.message}")
                                    reloadTrigger++
                                } catch (e: Exception) {
                                    Log.e("changeRelations", "Error sending user relation request", e)
                                }
                            }
                        }
                    }
                    if (profile.relationStatus == PersonStatus.NONE.name) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Sende en venneforespørsel hvis ingen relasjon fra før
                            CustomButton(text = stringResource(R.string.add)) {
                                val request = UserRelationChangeRequest(
                                    TargetUserId = userId,
                                    Status = "PENDING"
                                )
                                coroutineScope.launch {
                                    try {
                                        val response = sendUserRelationRequest(UserSession.token, request)
                                        Log.d("changeRelations", "Response: ${response.message}")
                                        reloadTrigger++
                                    } catch (e: Exception) {
                                        Log.e("changeRelations", "Error sending user relation request", e)
                                    }
                                }
                            }
                            CustomButton(text = stringResource(R.string.follow)) {
                                val request = UserRelationChangeRequest(
                                    TargetUserId = userId,
                                    Status = "FOLLOWING"
                                )
                                coroutineScope.launch {
                                    try {
                                        val response = sendUserRelationRequest(UserSession.token, request)
                                        Log.d("changeRelations", "Response: ${response.message}")
                                        reloadTrigger++
                                    } catch (e: Exception) {
                                        Log.e("changeRelations", "Error sending user relation request", e)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(8.dp)
                    .animateContentSize() // Animate size changes
            ) {
                Column {
                    Text(
                        text = if (profile.description.isNullOrEmpty()) "N/A" else if (showFullDescription) profile.description else profile.description.take(100).plus("..."),
                        style = generalTextStyle,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = if (showFullDescription) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if ((profile.description?.length ?: 0) > 100) {
                        IconButton(onClick = { showFullDescription = !showFullDescription }) {
                            Icon(
                                imageVector = if (showFullDescription) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (showFullDescription) "Show less" else "Show more",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.trips),
                style = underheaderTextStyle,
                color = MaterialTheme.colorScheme.secondary,
            )
            profile.userHikes.forEach { hike ->
                HikeItem(hike)
            }
            if (userHikes.isNotEmpty() || profile.userHikes.isNotEmpty())  {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { loadMoreHikes() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.load_more),
                        style = buttonTextStyle,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val formattedDate = profile.created_at?.let { formatDate(it) } ?: "Unknown"
                Text(
                    text = "Created at: $formattedDate",
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
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
        ReportDialog(
            entityId = userId,
            entityName = "Users",
            token = UserSession.token,
            onDismiss = { showReportDialog = false }
        )
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
                    style = underheaderTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
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
            .background(MaterialTheme.colorScheme.onBackground)
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
            Text(text = userItem.name, style = headerTextStyleSmall, color = MaterialTheme.colorScheme.secondary)
            Text(text = userItem.alias, style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun UserItemComposable(user: OtherUsers, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
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
            Text(text = user.name ?: "Unknown", style = headerTextStyleSmall, color = MaterialTheme.colorScheme.secondary)
            Text(text = user.alias ?: "Unknown", style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary)
        }
    }
}