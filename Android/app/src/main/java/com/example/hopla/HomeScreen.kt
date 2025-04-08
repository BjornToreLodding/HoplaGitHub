package com.example.hopla

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.ThumbUp
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.apiService.deleteReaction
import com.example.hopla.apiService.fetchFeed
import com.example.hopla.apiService.postReaction
import com.example.hopla.ui.theme.HeartColor
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.FeedItem
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.formatDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TopTextColumn(selectedItem: ImageVector, onItemSelected: (ImageVector) -> Unit) {
    val items = listOf(
        Icons.Outlined.Language,
        Icons.Outlined.PeopleOutline,
        Icons.Outlined.Cable,
        Icons.Outlined.LocationOn,
        Icons.Outlined.ThumbUp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(
                            if (selectedItem == item) colorResource(id = R.color.transparentWhite)
                            else MaterialTheme.colorScheme.primary
                        )
                        .clickable { onItemSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf(Icons.Outlined.Language) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        getCurrentLocation(context) { location ->
            latitude = location.latitude
            longitude = location.longitude
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopTextColumn(selectedItem) { selectedItem = it }
        when (selectedItem) {
            Icons.Outlined.Language -> PostList(navController = navController)
            Icons.Outlined.PeopleOutline -> PostList(navController = navController, onlyFriendsAndFollowing = true)
            Icons.Outlined.Cable -> PostList(navController = navController, onlyLikedTrails = true)
            Icons.Outlined.LocationOn -> PostList(navController = navController, latitude = latitude, longitude = longitude)
            Icons.Outlined.ThumbUp -> PostList(navController = navController, sortByLikes = true)
        }
    }
}

@Composable
fun PostList(navController: NavController, onlyFriendsAndFollowing: Boolean = false, onlyLikedTrails: Boolean = false, latitude: Double? = null, longitude: Double? = null, sortByLikes: Boolean = false) {
    val token = UserSession.token
    var pageNumber by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var hasMorePosts by remember { mutableStateOf(true) }
    var feedItems by remember { mutableStateOf(listOf<FeedItem>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(pageNumber, onlyFriendsAndFollowing, onlyLikedTrails, latitude, longitude, sortByLikes) {
        isLoading = true
        val newFeedResponse = if (sortByLikes) {
            fetchFeed(token, pageNumber, onlyFriendsAndFollowing, onlyLikedTrails, latitude, longitude, sortByLikes = true)
        } else {
            fetchFeed(token, pageNumber, onlyFriendsAndFollowing, onlyLikedTrails, latitude, longitude)
        }
        if (newFeedResponse == null) {
            errorMessage = "Not available right now"
            hasMorePosts = false
        } else {
            if (newFeedResponse.items.isEmpty()) {
                hasMorePosts = false
            } else {
                feedItems = feedItems + newFeedResponse.items
            }
        }
        isLoading = false
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == feedItems.size - 1 && hasMorePosts && !isLoading) {
                    pageNumber++
                }
            }
    }

    if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage!!)
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(feedItems) { feedItem ->
                PostItem(feedItem, navController)
            }
            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(feedItem: FeedItem, navController: NavController) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(feedItem.isLikedByUser) }
    var likesCount by remember { mutableIntStateOf(feedItem.likes) }

    val (formattedDate, formattedTime) = formatDateTime(feedItem.createdAt)
    val token = UserSession.token

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
    ) {
        // User info and more options button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clickable {
                    Log.d("PostItem", "Navigating to friend_profile/${feedItem.userId}")
                    navController.navigate("friend_profile/${feedItem.userId}")
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(feedItem.userProfilePicture),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .clip(CircleShape)
            )
            Text(
                text = feedItem.userAlias,
                style = generalTextStyleBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isDropdownExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.report),
                            style = generalTextStyleBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    onClick = {
                        isDropdownExpanded = false
                        showReportDialog = true
                    }
                )
            }
        }
        // Post content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = feedItem.title,
                    style = underheaderTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                // Description
                Text(
                    text = feedItem.description,
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Image
                Image(
                    painter = rememberAsyncImagePainter(feedItem.pictureUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onBackground)
                        .height(40.dp),
                ) {
                    // Date and Time
                    Text(
                        text = "$formattedDate $formattedTime",
                        style = generalTextStyle,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isLiked = !isLiked
                    likesCount = if (isLiked) likesCount + 1 else likesCount - 1
                    CoroutineScope(Dispatchers.IO).launch {
                        if (isLiked) {
                            postReaction(token, feedItem.entityId)
                        } else {
                            deleteReaction(token, feedItem.entityId)
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) HeartColor else MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = likesCount.toString(),
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        if (showReportDialog) {
            ReportDialog(
                entityId = feedItem.entityId,
                entityName = feedItem.entityName,
                token = UserSession.token,
                onDismiss = { showReportDialog = false }
            )
        }
    }
}

