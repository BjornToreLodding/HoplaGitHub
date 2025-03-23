package com.example.hopla

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.apiService.createStable
import com.example.hopla.apiService.fetchStables
import com.example.hopla.universalData.AddButton
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.SearchBar
import com.example.hopla.universalData.SimpleMapScreen
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.StableRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(navController: NavController, token: String) {
    var searchQuery by remember { mutableStateOf("") }
    var showLikedOnly by remember { mutableStateOf(false) }
    val likedStables = remember { mutableStateListOf<Stable>() }
    var stables by remember { mutableStateOf(listOf<Stable>()) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Log.d("CommunityScreen", "CommunityScreen launched")
    // Fetch the current location when the composable is first launched
    LaunchedEffect(Unit) {
        Log.d("CommunityScreen", "LaunchedEffect triggered")
        getCurrentLocation(context) { location ->
            Log.d("CommunityScreen", "Location received: ${location.latitude}, ${location.longitude}")
            latitude = location.latitude
            longitude = location.longitude
            coroutineScope.launch {
                Log.d("CommunityScreen", "Coroutine launched")
                val fetchedStables = fetchStables(token, "", latitude, longitude, pageNumber)
                Log.d("CommunityScreen", "Stables fetched: ${fetchedStables.size}")
                stables = fetchedStables
            }
        }
    }

    // Function to load more stables
    fun loadMoreStables() {
        if (!loading) {
            loading = true
            coroutineScope.launch {
                val newPageNumber = pageNumber + 1
                val fetchedStables = fetchStables(token, "", latitude, longitude, newPageNumber)
                if (fetchedStables.isNotEmpty()) {
                    stables = stables + fetchedStables
                    pageNumber = newPageNumber
                }
                loading = false
            }
        }
    }

    // Filter the stables based on the search query and liked status
    val filteredStables = stables.filter {
        it.stableName.contains(searchQuery, ignoreCase = true) &&
                (!showLikedOnly || likedStables.contains(it))
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
                items(filteredStables) { stable ->
                    StableCard(stable, navController, likedStables)
                }
                // Show loading indicator at the bottom
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        if (loading) {
                            CircularProgressIndicator()
                        } else {
                            LaunchedEffect(Unit) {
                                loadMoreStables()
                            }
                        }
                    }
                }
            }
        }
        // Add button to add a new community group
        AddButton(onClick = { navController.navigate("addCommunityScreen") })
    }
}

@Composable
fun StableCard(stable: Stable, navController: NavController, likedStables: MutableList<Stable>) {
    var isLiked by remember { mutableStateOf(likedStables.contains(stable)) }

    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .clickable { navController.navigate("stableDetail/${stable.stableId}") }
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(stable.pictureUrl),
                contentDescription = stable.stableName,
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
                text = stable.stableName,
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
                            likedStables.add(stable)
                        } else {
                            likedStables.remove(stable)
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

/*
// Details about a specific community
@Composable
fun CommunityDetailScreen(navController: NavController, community: String) {
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
    // !!!! Change entityID to communityid
    if (showReportDialog) {
        ReportDialog(
            entityId = UserSession.userId,
            entityName = "Community",
            token = UserSession.token,
            onDismiss = { showReportDialog = false }
        )
    }
}
*/

// Add a new community group screen
@Composable
fun AddCommunityScreen(navController: NavController, token: String) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showMap by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val privateString = stringResource(R.string.private_string)
    val publicString = stringResource(R.string.public_string)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            ScreenHeader(navController = navController, headerText = stringResource(R.string.add_new_community))

            Spacer(modifier = Modifier.height(16.dp))

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(privateString, publicString).forEach { option ->
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

            Button(
                onClick = { showMap = !showMap },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.choose_position))
            }

            if (showMap) {
                SimpleMapScreen { position ->
                    selectedPosition = position
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedOption != null && selectedPosition != null && imageBitmap != null) {
                        val isPrivate = selectedOption == privateString
                        val stableRequest = StableRequest(
                            Name = name,
                            Description = description,
                            PictureUrl = "https://hopla.imgix.net/51053800-5fd2-421d-b0bc-f3d9d9cdca3e.jpg?w=200&h=200&fit=crop", // Replace with actual image URL
                            Latitude = selectedPosition!!.latitude,
                            Longitude = selectedPosition!!.longitude,
                            PrivateGroup = isPrivate
                        )
                        coroutineScope.launch {
                            val response = createStable(token, stableRequest)
                            Log.d("AddCommunityScreen", "Response: $response")
                            navController.popBackStack()
                        }
                    } else {
                        // Show a message to select an option, position, and image
                    }
                },
                enabled = name.isNotBlank() && description.isNotBlank() && selectedOption != null && selectedPosition != null && imageBitmap != null
            ) {
                Text(text = stringResource(R.string.add_new_community))
            }
        }
    }
}

fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val locationProvider = LocationManager.GPS_PROVIDER

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)
        if (lastKnownLocation != null) {
            Log.d("CommunityScreen", "Location found: ${lastKnownLocation.latitude}, ${lastKnownLocation.longitude}")
            val userLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
            onLocationReceived(userLocation)
        } else {
            Log.d("CommunityScreen", "No last known location available, requesting location update")
            val locationListener = object : android.location.LocationListener {
                override fun onLocationChanged(location: Location) {
                    Log.d("CommunityScreen", "Location update received: ${location.latitude}, ${location.longitude}")
                    val userLocation = LatLng(location.latitude, location.longitude)
                    onLocationReceived(userLocation)
                    locationManager.removeUpdates(this)
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            locationManager.requestSingleUpdate(locationProvider, locationListener, null)

            // Add a timeout to handle cases where the location update is not received
            val handler = android.os.Handler(context.mainLooper)
            handler.postDelayed({
                Log.d("CommunityScreen", "Location update timeout")
                locationManager.removeUpdates(locationListener)
                // Handle the timeout case, e.g., show a message to the user
            }, 10000) // 10 seconds timeout
        }
    } else {
        Log.d("CommunityScreen", "Location permissions not granted")
        // Handle the case where permissions are not granted
    }
}