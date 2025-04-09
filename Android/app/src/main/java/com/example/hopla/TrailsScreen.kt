package com.example.hopla

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.hopla.apiService.addFavoriteTrail
import com.example.hopla.apiService.fetchFavoriteTrails
import com.example.hopla.apiService.fetchTrailFilters
import com.example.hopla.apiService.fetchTrailUpdates
import com.example.hopla.apiService.fetchTrails
import com.example.hopla.apiService.fetchTrailsByLocation
import com.example.hopla.apiService.fetchTrailsRelations
import com.example.hopla.apiService.postTrailReview
import com.example.hopla.apiService.rateTrail
import com.example.hopla.apiService.removeFavoriteTrail
import com.example.hopla.ui.theme.HeartColor
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.ui.theme.StarColor
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.ContentBoxInfo
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.MapScreen
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.SearchBar
import com.example.hopla.universalData.Trail
import com.example.hopla.universalData.TrailFilter
import com.example.hopla.universalData.TrailRatingRequest
import com.example.hopla.universalData.TrailUpdate
import com.example.hopla.universalData.UserSession
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 8.dp)
    ) {
        if (!isRouteClicked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 8.dp)
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
                        modifier = Modifier
                            .background(
                                if (isMapClicked || (!isCloseByClicked && !isFavoriteClicked && !isFollowingClicked && !isFiltersClicked && !showOnlyFavorites)) Color.White.copy(
                                    alpha = 0.5f
                                ) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (isMapClicked) Icons.AutoMirrored.Outlined.List else Icons.Outlined.Home,
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
                                    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasFineLocationPermission || hasCoarseLocationPermission) {
                                        fusedLocationClient.lastLocation
                                            .addOnSuccessListener { location: Location? ->
                                                location?.let {
                                                    val latitude = it.latitude
                                                    val longitude = it.longitude
                                                    Log.d("TrailsScreen", "Latitude: $latitude, Longitude: $longitude")
                                                    coroutineScope.launch {
                                                        try {
                                                            val trailsResponse = fetchTrailsByLocation(token, latitude, longitude, pageNumber)
                                                            trails = trailsResponse.trails
                                                            noResults = trails.isEmpty()
                                                        } catch (e: Exception) {
                                                            Log.e("TrailsScreen", "Error fetching trails by location", e)
                                                        } finally {
                                                            isLoading = false
                                                        }
                                                    }
                                                }
                                            }
                                    } else {
                                        Log.e("TrailsScreen", "Location permissions are not granted")
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                if (isCloseByClicked) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
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
                                if (isFavoriteClicked) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
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
                                if (isFollowingClicked) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
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
                                            Log.e("fetchTrailFilters", "Error fetching trail filters", e)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .background(
                                        if (isFiltersClicked) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
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
                            .border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp))
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
                                            FilterOptionRow(filterId = filter.id, option = option, selectedFilters = selectedFilters)
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
                            Button(onClick = { showFiltersDialog = false }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            Button(onClick = {
                                // Apply filter logic here
                                filtersQuery = selectedFilters.entries.joinToString(separator = ";") { (id, options) ->
                                    "$id:${options.joinToString(separator = ",")}"
                                }
                                Log.d("SelectedFilters", filtersQuery)

                                coroutineScope.launch {
                                    try {
                                        val trailsResponse = fetchTrails(token, 1, searchQuery, filtersQuery)
                                        trails = trailsResponse.trails // Update the trails state with the new response
                                        noResults = trails.isEmpty()
                                    } catch (e: Exception) {
                                        Log.e("fetchTrails", "Error fetching trails", e)
                                    }
                                }

                                showFiltersDialog = false
                            }) {
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
                trails.filter { it.isFavorite == true }
            } else {
                trails
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
                                imageResource = if (trail.pictureUrl != null) listOf(trail.pictureUrl) else listOf(
                                    R.drawable.stockimg1
                                ),
                                isFavorite = trail.isFavorite ?: false,
                                starRating = trail.averageRating,
                                description = trail.description ?: "N/A",
                                filters = trail.filters?.map { it.value } ?: emptyList()
                            ),
                            onHeartClick = {
                                val newState = !(trail.isFavorite ?: false)
                                trails = trails.toMutableList().apply {
                                    this[index] = trail.copy(isFavorite = newState)
                                }
                            },
                            onBoxClick = {
                                selectedContentBoxInfo = ContentBoxInfo(
                                    id = trail.id,
                                    title = trail.name,
                                    imageResource = if (trail.pictureUrl != null) listOf(trail.pictureUrl) else listOf(
                                        R.drawable.stockimg1
                                    ),
                                    isFavorite = trail.isFavorite ?: false,
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
                                        val trailsResponse = fetchTrails(token, pageNumber, searchQuery, filtersQuery)
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
                    .background(Color.Black.copy(alpha = 0.5f))
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
                repeat(5) { index ->
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

@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: (Bitmap, String) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.post_review),
                    style = headerTextStyleSmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(model = R.drawable.logo1),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ImagePicker(
                    onImageSelected = { bitmap ->
                        imageBitmap = bitmap
                    },
                    text = stringResource(R.string.select_image)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp) // Fixed height for the TextField
                        .verticalScroll(scrollState) // Scroll content inside the TextField
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(onClick = {
                        imageBitmap?.let { bitmap ->
                            onConfirm(bitmap, message)
                        }
                    }) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

// Function to display the trail that have been clicked
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RouteClicked(navController: NavController, contentBoxInfo: ContentBoxInfo, onBackClick: () -> Unit) {
    var currentImageIndex by remember { mutableIntStateOf(0) }
    var userRating by remember { mutableIntStateOf(0) }
    var showMessageBox by remember { mutableStateOf(false) }
    var trailUpdates by remember { mutableStateOf<List<TrailUpdate>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    var showGiveReview by remember { mutableStateOf(false) }

    val images = contentBoxInfo.imageResource.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Inner box header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Row in header to display items next to each other
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back button that takes the user back to the main page of trails
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    // Text in the header
                    Text(
                        text = contentBoxInfo.title,
                        style = headerTextStyleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

        // Scrollable content starts here
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            item {
                // Pictures + description box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                ) {
                    // Column for the picture
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                .height(190.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                        ) {
                            // Display the images
                            val painter = when (val imageResource = images[currentImageIndex]) {
                                is String -> rememberAsyncImagePainter(model = imageResource)
                                is Int -> painterResource(id = imageResource)
                                else -> painterResource(id = R.drawable.stockimg1)
                            }

                            Image(
                                painter = painter,
                                contentDescription = "Route Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Description below pictures
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                contentBoxInfo.filters.forEach { filter ->
                                    Text(
                                        text = filter,
                                        style = generalTextStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Row for start trip and new updates boxes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Start trip clickable box
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.3f)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { navController.navigate("start_trip_map/${contentBoxInfo.id}") }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.follow_trail), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // New updates clickable box
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.7f)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { showGiveReview = true }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.new_updates), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                    }

                }
            }

            item {
                // Description Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = contentBoxInfo.description, style = generalTextStyle, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(start = 8.dp))
                    }
                    // Assessment box with star rating set
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.assessment), style = generalTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < contentBoxInfo.starRating) Icons.Filled.Star else Icons.TwoTone.Star,
                                        contentDescription = null,
                                        tint = StarColor
                                    )
                                }
                            }
                        }
                    }
                    // My assessment box wih star rating, changeable
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.give_assessment), style = generalTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                            StarRating(trailId = contentBoxInfo.id, rating = userRating, onRatingChanged = { userRating = it })
                        }
                    }
                    // Outer box of latest update
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner, clickable box for latest update
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    showMessageBox = true
                                    coroutineScope.launch {
                                        trailUpdates = fetchTrailUpdates(contentBoxInfo.id, 1, token)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.latest_update_about_the_route), style = generalTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }

    if (showMessageBox) {
        TrailUpdates(showMessageBox, trailUpdates, onDismissRequest = { showMessageBox = false })
    }

    if (showGiveReview) {
        ReviewDialog(
            onDismiss = { showGiveReview = false },
            onConfirm = { image, message ->
                coroutineScope.launch {
                    try {
                        val response = postTrailReview(token, image, contentBoxInfo.id, message)
                        Log.d("postTrailReview", "Response: $response")
                        showGiveReview = false
                    } catch (e: Exception) {
                        Log.e("postTrailReview", "Error posting review", e)
                    }
                }
            }
        )
    }
}

@Composable
private fun TrailUpdates(
    showMessageBox: Boolean,
    trailUpdates: List<TrailUpdate>,
    onDismissRequest: () -> Unit
) {
    if (showMessageBox) {
        Dialog(onDismissRequest = onDismissRequest) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.latest_update_about_the_route),
                        style = headerTextStyleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(trailUpdates) { update ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = update.comment,
                                        style = generalTextStyle,
                                        color = MaterialTheme.colorScheme.secondary,
                                    )

                                    update.pictureUrl.let { imageUrl ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Image(
                                            painter = rememberAsyncImagePainter(model = imageUrl),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "User: ${update.alias}",
                                            style = generalTextStyleBold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                                                SimpleDateFormat(
                                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                                    Locale.getDefault()
                                                ).parse(update.createdAt)
                                            ),
                                            style = generalTextStyleBold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
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

// Function to be able to change the rating
@Composable
fun StarRating(trailId: String, rating: Int, onRatingChanged: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.TwoTone.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        val newRating = index + 1
                        onRatingChanged(newRating)
                        coroutineScope.launch {
                            try {
                                val response = rateTrail(token, TrailRatingRequest(TrailId = trailId, Rating = newRating))
                                Log.d("rateTrail", "Response: ${response.message}")
                            } catch (e: Exception) {
                                Log.e("rateTrail", "Error rating trail", e)
                            }
                        }
                    }
            )
        }
    }
}

// Function to display the update screen where user can add their own update about the route
@Composable
fun UpdateScreen(navController: NavController) {
    val location by remember { mutableStateOf("Boredalstien") }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE6DD)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Box (Title + Back Button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB8A999))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = stringResource(R.string.new_updates),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Location Field (Read-only)
        TextField(
            value = location,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comment Box with Floating Add Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(150.dp)
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.comment), color = Color.Gray)
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            FloatingActionButton(
                onClick = { /* Handle Add Action */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(40.dp),
                containerColor = Color(0xFFD9CFC4)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Comment")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
            onClick = {
                comment = "" // Clear the comment box
                navController.popBackStack() // Navigate back to the previous screen
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9CFC4))
        ) {
            Text(text = stringResource(R.string.publish), color = Color.Gray)
        }
    }
}
