package com.example.hopla.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.hopla.apiService.fetchFeed
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.FeedItem
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.getCurrentLocation

// Home screen of the app
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
            Icons.Outlined.PeopleOutline -> PostList(
                navController = navController,
                onlyFriendsAndFollowing = true
            )

            Icons.Outlined.Cable -> PostList(navController = navController, onlyLikedTrails = true)
            Icons.Outlined.LocationOn -> PostList(
                navController = navController,
                latitude = latitude,
                longitude = longitude
            )

            Icons.Outlined.ThumbUp -> PostList(navController = navController, sortByLikes = true)
        }
    }
}

// Fetch the feed items based on the selected filter from top text
@Composable
fun PostList(
    navController: NavController,
    onlyFriendsAndFollowing: Boolean = false,
    onlyLikedTrails: Boolean = false,
    latitude: Double? = null,
    longitude: Double? = null,
    sortByLikes: Boolean = false
) {
    val token = UserSession.token
    var pageNumber by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var hasMorePosts by remember { mutableStateOf(true) }
    var feedItems by remember { mutableStateOf(listOf<FeedItem>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(
        pageNumber,
        onlyFriendsAndFollowing,
        onlyLikedTrails,
        latitude,
        longitude,
        sortByLikes
    ) {
        isLoading = true
        try {
            val newFeedResponse = if (sortByLikes) {
                fetchFeed(
                    token,
                    pageNumber,
                    onlyFriendsAndFollowing,
                    onlyLikedTrails,
                    latitude,
                    longitude,
                    sortByLikes = true
                )
            } else {
                fetchFeed(
                    token,
                    pageNumber,
                    onlyFriendsAndFollowing,
                    onlyLikedTrails,
                    latitude,
                    longitude
                )
            }
            if (newFeedResponse == null || newFeedResponse.items.isEmpty()) {
                hasMorePosts = false
                if (feedItems.isEmpty()) {
                    errorMessage = "Ingen innlegg tilgjengelig for øyeblikket"
                }
            } else {
                feedItems = feedItems + newFeedResponse.items
            }
        } catch (e: Exception) {
            errorMessage = "En feil har skjedd mens vi henter innlegg. Vennligst prøv igjen senere"
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
            Text(
                text = errorMessage!!,
                style = underheaderTextStyle,
                color = MaterialTheme.colorScheme.tertiary
            )
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
