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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.hopla.universalData.FeedItem
import com.example.hopla.universalData.FeedResponse
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
    val feedResponse by produceState<FeedResponse?>(initialValue = null) {
        value = fetchFeed(token, 1)
    }

    feedResponse?.let { response ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            items(response.items) { item: FeedItem ->
                PostItem(feedItem = item)
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
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { stringResource(R.string.report) },
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