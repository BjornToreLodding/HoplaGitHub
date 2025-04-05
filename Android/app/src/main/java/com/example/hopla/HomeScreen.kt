package com.example.hopla

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.apiService.fetchFeed
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.universalData.FeedItem
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.UserSession

@Composable
fun HomeScreen() {
    var selectedItem by remember { mutableStateOf(Icons.Outlined.Home) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopTextColumn(selectedItem) { selectedItem = it }
        when (selectedItem) {
            Icons.Outlined.Home -> PostList()
            Icons.Outlined.Person -> PostList()
            Icons.Outlined.FavoriteBorder -> PostList()
            Icons.Outlined.LocationOn -> PostList()
            Icons.Outlined.ThumbUp -> PostList()
        }
    }
}

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
fun PostList() {
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
                if (lastVisibleItemIndex == listState.layoutInfo.totalItemsCount - 1 && hasMorePosts && !isLoading) {
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
            PostItem(feedItem = item)
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        } else if (!hasMorePosts) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_more_posts),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun PostItem(feedItem: FeedItem) {
    var isLogoClicked by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                Text(
                    text = feedItem.title,
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = if (isLogoClicked) R.drawable.logo_filled_white else R.drawable.logo_white),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { isLogoClicked = !isLogoClicked }
            )
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