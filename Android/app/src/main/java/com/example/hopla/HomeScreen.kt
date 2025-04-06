package com.example.hopla

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.apiService.fetchFeed
import com.example.hopla.apiService.fetchTrails
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.FeedItem
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.Trail
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.formatDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TopTextColumn(selectedItem: ImageVector, onItemSelected: (ImageVector) -> Unit) {
    val items = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.Person,
        Icons.Outlined.FavoriteBorder,
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
    var selectedItem by remember { mutableStateOf(Icons.Outlined.Home) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopTextColumn(selectedItem) { selectedItem = it }
        when (selectedItem) {
            Icons.Outlined.Home -> PostList(navController = navController)
            Icons.Outlined.Person -> PostList(navController = navController)
            Icons.Outlined.FavoriteBorder -> PostList(navController = navController)
            Icons.Outlined.LocationOn -> PostList(navController = navController)
            Icons.Outlined.ThumbUp -> PostList(navController = navController)
        }
    }
}

@Composable
fun PostList(navController: NavController) {
    val token = UserSession.token
    var pageNumber by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var hasMorePosts by remember { mutableStateOf(true) }
    var feedItems by remember { mutableStateOf(listOf<FeedItem>()) }
    val listState = rememberLazyListState()

    LaunchedEffect(pageNumber) {
        isLoading = true
        val newFeedResponse = fetchFeed(token, pageNumber)
        if (newFeedResponse.items.isEmpty()) {
            hasMorePosts = false
        } else {
            feedItems = feedItems + newFeedResponse.items
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

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        items(feedItems) { item: FeedItem ->
            PostItem(feedItem = item, navController = navController)
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PostItem(feedItem: FeedItem, navController: NavController) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    val (formattedDate, formattedTime) = formatDateTime(feedItem.createdAt)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
    ) {
        // User info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp)
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
                        .then(
                            if (feedItem.entityName == "Trail") {
                                Modifier.clickable {
                                    Log.d("PostItem", "Trail clicked: ${feedItem.entityId}")
                                    gatherMoreInfo(feedItem.title, UserSession.token) { response ->
                                        // Handle the response here
                                    }
                                }
                            } else {
                                Modifier
                            }
                        )
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
                    .align(Alignment.TopEnd)
            ) {
                Box {
                    IconButton(onClick = { isDropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onBackground
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

fun gatherMoreInfo(title: String, token: String, onResult: (List<Trail>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = fetchTrails(token, 1, title, "")
            val filteredTrails = response.trails.filter { it.name.contains(title, ignoreCase = true) }
            Log.d("gatherMoreInfo", "Filtered Trails: $filteredTrails")
            onResult(filteredTrails)
        } catch (e: Exception) {
            Log.e("fetchTrails", "Error fetching trails", e)
        }
    }
}