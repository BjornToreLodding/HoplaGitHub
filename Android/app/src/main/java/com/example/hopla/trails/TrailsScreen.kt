package com.example.hopla.trails

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.addFavoriteTrail
import com.example.hopla.apiService.fetchFavoriteTrails
import com.example.hopla.apiService.fetchTrailFilters
import com.example.hopla.apiService.fetchTrails
import com.example.hopla.apiService.fetchTrailsByLocation
import com.example.hopla.apiService.fetchTrailsRelations
import com.example.hopla.apiService.removeFavoriteTrail
import com.example.hopla.ui.theme.HeartColor
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.ui.theme.StarColor
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.ContentBoxInfo
import com.example.hopla.universalData.MapScreen
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.SearchBar
import com.example.hopla.universalData.Trail
import com.example.hopla.universalData.TrailFilter
import com.example.hopla.universalData.UserSession
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

private const val NUM_STARS = 5
private const val TR_VALUE = 0.5f

// Function to display the main screen of trails page
@Composable
fun TrailsScreen(navController: NavController) {
    var isMapClicked by remember { mutableStateOf(false) }
    var isCloseByClicked by remember { mutableStateOf(false) }
    var isFavoriteClicked by remember { mutableStateOf(false) }
    var isFollowingClicked by remember { mutableStateOf(false) }
    var isFiltersClicked by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isRouteClicked by remember { mutableStateOf(false) }
    var selectedContentBoxInfo by remember { mutableStateOf<ContentBoxInfo?>(null) }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var trails by remember { mutableStateOf<List<Trail>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    var pageNumber by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var noResults by remember { mutableStateOf(false) }
    var showFiltersDialog by remember { mutableStateOf(false) }
    var trailFilters by remember { mutableStateOf<List<TrailFilter>>(emptyList()) }
    val selectedFilters = remember { mutableStateMapOf<String, MutableList<String>>() }
    var filtersQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    var searchResponse by remember { mutableStateOf<List<Trail>>(emptyList()) }
    val isHighlighted = isMapClicked || (
            !isCloseByClicked &&
                    !isFavoriteClicked &&
                    !isFollowingClicked &&
                    !isFiltersClicked &&
                    !showOnlyFavorites
            )

    LaunchedEffect(searchQuery) {
        pageNumber = 1
        if (searchQuery.isEmpty()) {
            trails = emptyList()
            noResults = false
        } else {
            coroutineScope.launch {
                try {
                    isLoading = true
                    val trailsResponse = fetchTrails(token, pageNumber, searchQuery, filtersQuery)
                    trails = trailsResponse.trails
                    searchResponse = trailsResponse.trails
                    noResults = trails.isEmpty()
                } catch (e: Exception) {
                    Log.e("TrailsScreen", "Error fetching trails", e)
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(32.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isRouteClicked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            isMapClicked = !isMapClicked
                            if (isMapClicked) {
                                isCloseByClicked = false
                                isFavoriteClicked = false
                                isFollowingClicked = false
                                isFiltersClicked = false
                                isDropdownExpanded = false
                                showOnlyFavorites = false

                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val trailsResponse = fetchTrails(token, 1, "", filtersQuery)
                                        trails = trailsResponse.trails
                                        noResults = trails.isEmpty()
                                    } catch (e: Exception) {
                                        Log.e("TrailsScreen", "Error fetching all trails", e)
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },

                        Modifier
                            .background(
                                if (isHighlighted) Color.White.copy(alpha = TR_VALUE) else Color.Transparent,
                                shape = RectangleShape
                            )
                            .padding(horizontal = 16.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isMapClicked) {
                                Icons.AutoMirrored.Outlined.List
                            } else {
                                Icons.Outlined.Language
                            },
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            pageNumber = 1
                            isCloseByClicked = !isCloseByClicked
                            if (isCloseByClicked) {
                                isFavoriteClicked = false
                                isFollowingClicked = false
                                isMapClicked = false
                                coroutineScope.launch {
                                    isLoading = true
                                    val hasFineLocationPermission =
                                        ContextCompat.checkSelfPermission(
                                            context, Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarseLocationPermission =
                                        ContextCompat.checkSelfPermission(
                                            context, Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFineLocationPermission || hasCoarseLocationPermission) {
                                        fusedLocationClient.lastLocation
                                            .addOnSuccessListener { location: Location? ->
                                                location?.let {
                                                    val latitude = it.latitude
                                                    val longitude = it.longitude
                                                    Log.d(
                                                        "TrailsScreen",
                                                        "Latitude: $latitude, Longitude: $longitude"
                                                    )
                                                    coroutineScope.launch {
                                                        try {
                                                            val trailsResponse =
                                                                fetchTrailsByLocation(
                                                                    token,
                                                                    latitude,
                                                                    longitude,
                                                                    pageNumber
                                                                )
                                                            trails = trailsResponse.trails
                                                            noResults = trails.isEmpty()
                                                        } catch (e: Exception) {
                                                            Log.e(
                                                                "TrailsScreen",
                                                                "Error fetching trails by location",
                                                                e
                                                            )
                                                        } finally {
                                                            isLoading = false
                                                        }
                                                    }
                                                }
                                            }
                                    } else {
                                        Log.e(
                                            "TrailsScreen",
                                            "Location permissions are not granted"
                                        )
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                if (isCloseByClicked) Color.White.copy(alpha = TR_VALUE) else Color.Transparent,
                                shape = RectangleShape
                            )
                            .padding(horizontal = 16.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            pageNumber = 1
                            isFavoriteClicked = !isFavoriteClicked
                            if (isFavoriteClicked) {
                                isMapClicked = false
                                isCloseByClicked = false
                                isFollowingClicked = false

                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val trailsResponse = fetchFavoriteTrails(token)
                                        trails = trailsResponse.trails
                                        noResults = trails.isEmpty()
                                    } catch (e: Exception) {
                                        Log.e("TrailsScreen", "Error fetching favorite trails", e)
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                // Reset trails to an empty list or fetch all trails again if needed
                                trails = emptyList()
                                noResults = false
                            }
                        },
                        modifier = Modifier
                            .background(
                                if (isFavoriteClicked) Color.White.copy(alpha = TR_VALUE) else Color.Transparent,
                                shape = RectangleShape
                            )
                            .padding(horizontal = 16.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            pageNumber = 1
                            isFollowingClicked = !isFollowingClicked
                            if (isFollowingClicked) {
                                isMapClicked = false
                                isCloseByClicked = false
                                isFavoriteClicked = false
                                isFiltersClicked = false
                                isDropdownExpanded = false
                                showOnlyFavorites = false

                                coroutineScope.launch {
                                    isLoading = true
                                    try {
                                        val trailsResponse = fetchTrailsRelations(token, pageNumber)
                                        trails = trailsResponse.trails
                                        noResults = trails.isEmpty()
                                    } catch (e: Exception) {
                                        Log.e("TrailsScreen", "Error fetching trails relations", e)
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                if (isFollowingClicked) Color.White.copy(alpha = TR_VALUE) else Color.Transparent,
                                shape = RectangleShape
                            )
                            .padding(horizontal = 16.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null
                        )
                    }

                    Column {
                        Box {
                            IconButton(
                                onClick = {
                                    showFiltersDialog = true
                                    coroutineScope.launch {
                                        try {
                                            trailFilters = fetchTrailFilters(token)
                                        } catch (e: Exception) {
                                            Log.e(
                                                "fetchTrailFilters",
                                                "Error fetching trail filters",
                                                e
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .background(
                                        if (isFiltersClicked) Color.White.copy(alpha = TR_VALUE) else Color.Transparent,
                                        shape = RectangleShape
                                    )
                                    .padding(horizontal = 16.dp)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = "Filters"
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showFiltersDialog) {
            Dialog(onDismissRequest = { showFiltersDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.tertiary,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.filters),
                            style = headerTextStyleSmall,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(trailFilters) { filter ->
                                Text(
                                    text = filter.displayName,
                                    style = underheaderTextStyle,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(8.dp)
                                )
                                if (filter.options.isEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        var isYesChecked by remember { mutableStateOf(false) }
                                        var isNoChecked by remember { mutableStateOf(false) }

                                        Checkbox(
                                            checked = isYesChecked,
                                            onCheckedChange = {
                                                isYesChecked = it
                                                if (it) isNoChecked = false
                                            }
                                        )
                                        Text(
                                            text = stringResource(R.string.yes),
                                            style = generalTextStyle,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                        Checkbox(
                                            checked = isNoChecked,
                                            onCheckedChange = {
                                                isNoChecked = it
                                                if (it) isYesChecked = false
                                            }
                                        )
                                        Text(
                                            text = stringResource(R.string.no),
                                            style = generalTextStyle,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                } else {
                                    Column {
                                        filter.options.forEach { option ->
                                            FilterOptionRow(
                                                filterId = filter.id,
                                                option = option,
                                                selectedFilters = selectedFilters
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { showFiltersDialog = false }, shape = RectangleShape) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            Button(onClick = {
                                // Apply filter logic here
                                filtersQuery =
                                    selectedFilters.entries.joinToString(separator = ";") { (id, options) ->
                                        "$id:${options.joinToString(separator = ",")}"
                                    }
                                Log.d("SelectedFilters", filtersQuery)

                                coroutineScope.launch {
                                    try {
                                        val trailsResponse =
                                            fetchTrails(token, 1, searchQuery, filtersQuery)
                                        trails =
                                            trailsResponse.trails
                                        noResults = trails.isEmpty()
                                    } catch (e: Exception) {
                                        Log.e("fetchTrails", "Error fetching trails", e)
                                    }
                                }

                                showFiltersDialog = false
                            }, shape = RectangleShape) {
                                Text(text = stringResource(R.string.apply))
                            }
                        }
                    }
                }
            }
        }

        if (isRouteClicked) {
            selectedContentBoxInfo?.let { contentBoxInfo ->
                RouteClicked(
                    navController = navController,
                    contentBoxInfo = contentBoxInfo,
                    onBackClick = { isRouteClicked = false })
            }
        } else if (isMapClicked) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MapScreen()
            }
        } else {
            if (showOnlyFavorites) {
                trails.filter { it.isFavorite }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                }
                if (noResults && pageNumber == 1) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.no_trails_found))
                        }
                    }
                } else {
                    items(trails.size) { index ->
                        val trail = trails[index]
                        ContentBox(
                            info = ContentBoxInfo(
                                id = trail.id,
                                title = trail.name,
                                imageResource = listOf(trail.pictureUrl),
                                isFavorite = trail.isFavorite,
                                starRating = trail.averageRating,
                                description = trail.description ?: "N/A",
                                filters = trail.filters?.map { it.value } ?: emptyList()
                            ),
                            onHeartClick = {
                                val newState = !trail.isFavorite
                                trails = trails.toMutableList().apply {
                                    this[index] = trail.copy(isFavorite = newState)
                                }
                            },
                            onBoxClick = {
                                selectedContentBoxInfo = ContentBoxInfo(
                                    id = trail.id,
                                    title = trail.name,
                                    imageResource = listOf(trail.pictureUrl),
                                    isFavorite = trail.isFavorite,
                                    starRating = trail.averageRating,
                                    description = trail.description ?: "N/A",
                                    filters = trail.filters?.map { it.value } ?: emptyList()
                                )
                                isRouteClicked = true
                            }
                        )
                    }
                    item {
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LaunchedEffect(Unit) {
                                coroutineScope.launch {
                                    pageNumber++
                                    isLoading = true
                                    try {
                                        val trailsResponse = fetchTrails(
                                            token,
                                            pageNumber,
                                            searchQuery,
                                            filtersQuery
                                        )
                                        if (trailsResponse.trails.isNotEmpty()) {
                                            trails = trails + trailsResponse.trails
                                        } else {
                                            noResults = true
                                        }
                                    } catch (e: Exception) {
                                        Log.e("TrailsScreen", "Error fetching trails", e)
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Function to display the filter options in a row
@Composable
fun FilterOptionRow(filterId: String, option: String, selectedFilters: SnapshotStateMap<String, MutableList<String>>) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                isChecked = checked
                if (checked) {
                    selectedFilters.getOrPut(filterId) { mutableListOf() }.add(option)
                } else {
                    selectedFilters[filterId]?.remove(option)
                    if (selectedFilters[filterId].isNullOrEmpty()) {
                        selectedFilters.remove(filterId)
                    }
                }
            }
        )
        Text(
            text = option,
            style = generalTextStyle,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// Function to display the content of the trails (main page for all trails)
@Composable
fun ContentBox(info: ContentBoxInfo, onHeartClick: () -> Unit, onBoxClick: () -> Unit) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(180.dp)
            .clickable(onClick = onBoxClick)
    ) {
        // Main Box containing image + overlay + filters
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            val firstImageResource = info.imageResource.firstOrNull() ?: R.drawable.logo1
            val painter = when (firstImageResource) {
                is String -> rememberAsyncImagePainter(model = firstImageResource)
                is Int -> painterResource(id = firstImageResource)
                else -> painterResource(id = R.drawable.stockimg1)
            }

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = TR_VALUE))
            )

            // Top-right icons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        try {
                            if (info.isFavorite) {
                                removeFavoriteTrail(token, info.id)
                            } else {
                                addFavoriteTrail(token, info.id)
                            }
                            onHeartClick()
                        } catch (e: Exception) {
                            Log.e("ContentBox", "Error updating favorite status", e)
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (info.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (info.isFavorite) HeartColor else PrimaryWhite
                    )
                }

                Box {
                    IconButton(onClick = { isDropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = PrimaryWhite
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.report)) },
                            onClick = {
                                isDropdownExpanded = false
                                showReportDialog = true
                            }
                        )
                    }
                }
            }

            // Title bottom-left
            Text(
                text = info.title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = underheaderTextStyle,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 5.dp, bottom = 35.dp)
            )

            // Stars bottom-right
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 5.dp, bottom = 35.dp)
            ) {
                repeat(NUM_STARS) { index ->
                    Icon(
                        imageVector = if (index < info.starRating) Icons.Filled.Star else Icons.TwoTone.Star,
                        contentDescription = null,
                        tint = StarColor
                    )
                }
            }

            // Filters row at bottom
            val scrollState = rememberScrollState()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .horizontalScroll(scrollState)
            ) {
                info.filters.forEach { filter ->
                    Text(
                        text = filter,
                        style = generalTextStyle,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            entityId = info.id,
            entityName = "Trails",
            token = UserSession.token,
            onDismiss = { showReportDialog = false }
        )
    }
}
