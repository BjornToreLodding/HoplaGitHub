package com.example.hopla

//noinspection UsingMaterialAndMaterial3Libraries
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.apiService.createStable
import com.example.hopla.apiService.fetchStableDetails
import com.example.hopla.apiService.fetchStableMessages
import com.example.hopla.apiService.fetchStables
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.universalData.AddButton
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.Message
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.SearchBar
import com.example.hopla.universalData.SimpleMapScreen
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.StableDetails
import com.example.hopla.universalData.StableRequest
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.formatDateTime
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.time.ZonedDateTime


@Composable
fun CommunityScreen(navController: NavController, token: String) {
    var searchQuery by remember { mutableStateOf("") }
    val likedStables = remember { mutableStateListOf<Stable>() }
    var stables by remember { mutableStateOf(listOf<Stable>()) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf("position") }

    Log.d("CommunityScreen", "CommunityScreen launched")

    LaunchedEffect(Unit) {
        Log.d("CommunityScreen", "LaunchedEffect triggered")
        getCurrentLocation(context) { location ->
            Log.d("CommunityScreen", "Location received: ${location.latitude}, ${location.longitude}")
            latitude = location.latitude
            longitude = location.longitude
            coroutineScope.launch {
                Log.d("CommunityScreen", "Coroutine launched")
                val fetchedStables = fetchStables(token, searchQuery, "", latitude, longitude, pageNumber)
                Log.d("CommunityScreen", "Stables fetched: ${fetchedStables.size}")
                stables = fetchedStables
            }
        }
    }

    fun loadMoreStables() {
        if (!loading) {
            loading = true
            coroutineScope.launch {
                val newPageNumber = pageNumber + 1
                val fetchedStables = fetchStables(token, searchQuery, if (currentPage == "liked") UserSession.userId else "", latitude, longitude, newPageNumber)
                if (fetchedStables.isNotEmpty()) {
                    stables = stables + fetchedStables
                    pageNumber = newPageNumber
                }
                loading = false
            }
        }
    }

    fun fetchFilteredStables() {
        coroutineScope.launch {
            loading = true
            pageNumber = 1
            val fetchedStables = fetchStables(token, searchQuery, if (currentPage == "liked") UserSession.userId else "", latitude, longitude, pageNumber)
            stables = fetchedStables
            loading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopTextCommunity(currentPage = currentPage) { page ->
                currentPage = page
                fetchFilteredStables()
            }
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    searchQuery = query
                    fetchFilteredStables()
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 1.dp)
            ) {
                items(stables, key = { it.stableId }) { stable ->
                    StableCard(stable, navController, likedStables)
                }
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
        AddButton(onClick = { navController.navigate("addCommunityScreen") })
    }
}

@Composable
fun StableCard(stable: Stable, navController: NavController, likedStables: MutableList<Stable>) {
    var isLiked by remember { mutableStateOf(stable.member || likedStables.contains(stable)) }
    val coroutineScope = rememberCoroutineScope()

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
                        coroutineScope.launch {
                            // Send a new GET request when the heart icon is pressed
                            val fetchedStables = fetchStables(
                                token = UserSession.token,
                                search = "",
                                userid = if (isLiked) UserSession.userId else "",
                                latitude = 0.0,
                                longitude = 0.0,
                                pageNumber = 1
                            )
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
fun TopTextCommunity(currentPage: String, onShowLikedOnlyChange: (String) -> Unit) {
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
                    .clickable { onShowLikedOnlyChange("position") }
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
                    .clickable { onShowLikedOnlyChange("liked") }
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

// Details about a specific community
@Composable
fun CommunityDetailScreen(navController: NavController, stableId: String, token: String) {
    var showDialog by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var stable by remember { mutableStateOf<StableDetails?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load stable details from the server
    LaunchedEffect(stableId) {
        coroutineScope.launch {
            stable = fetchStableDetails(token, stableId)
        }
    }

    // Whole screen
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        stable?.let { community ->
            // Box for the header of the screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
            ) {
                // Image of the community group
                Image(
                    painter = rememberAsyncImagePainter(community.pictureUrl),
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
            MessageBox(stableId = stableId, token = token)
        }
    }

    // Show a dialog with the description of the community group and a report button
    if (showDialog) {
        stable?.let { community ->
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
    }

    if (showReportDialog) {
        ReportDialog(
            entityId = stableId,
            entityName = "Community",
            token = UserSession.token,
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
fun MessageBox(stableId: String, token: String) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    var newMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Function to load messages
    fun loadMessages() {
        if (!loading) {
            loading = true
            coroutineScope.launch {
                val fetchedMessages = fetchStableMessages(token, stableId, pageNumber)
                if (fetchedMessages.isNotEmpty()) {
                    messages = messages + fetchedMessages
                    pageNumber += 1
                }
                loading = false
            }
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        loadMessages()
    }

    // Group messages by date
    val groupedMessages = messages.groupBy { formatDateTime(it.timestamp).first }

    Column(modifier = Modifier.fillMaxSize()) {
        // LazyColumn to display messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true // To display the latest message at the top
        ) {
            // Loading indicator at the top
            item {
                if (loading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Display grouped messages
            groupedMessages.forEach { (date, messagesForDate) ->
                items(messagesForDate) { message ->
                    MessageCard(message)
                }
                item {
                    Text(
                        text = date,
                        style = generalTextStyleBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Load more messages when scrolled to the top
            item {
                LaunchedEffect(Unit) {
                    loadMessages()
                }
            }
        }

        // TextField and Button to send a new message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = stringResource(R.string.enter_you_message)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newMessage.isNotBlank()) {
                    val message = Message(
                        senderId = UserSession.userId,
                        senderAlias = "Me",
                        content = newMessage,
                        timestamp = ZonedDateTime.now().toString()
                    )
                    messages = listOf(message) + messages
                    newMessage = ""
                }
            }) {
                Text(text = stringResource(R.string.publish))
            }
        }
    }
}

@Composable
fun MessageCard(message: Message) {
    val backgroundColor = if (message.senderId == UserSession.userId) {
        MaterialTheme.colorScheme.primary // Green color
    } else {
        MaterialTheme.colorScheme.surface // Default color
    }

    val displayAlias = if (message.senderId == UserSession.userId) {
        stringResource(R.string.me) // "Me"
    } else {
        message.senderAlias
    }

    val (_, formattedTime) = formatDateTime(message.timestamp)

    val alignment = if (message.senderId == UserSession.userId) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 100.dp, max = (0.75f * LocalConfiguration.current.screenWidthDp).dp)
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = displayAlias, style = generalTextStyle, modifier = Modifier.align(Alignment.CenterVertically))
                    Text(text = formattedTime, style = generalTextStyle, modifier = Modifier.align(Alignment.CenterVertically))
                }
                Text(text = message.content, style = generalTextStyle)
            }
        }
    }
}

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Now inside composable scope
            val privateString = stringResource(R.string.private_string)
            val publicString = stringResource(R.string.public_string)
            val context = LocalContext.current

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
                onImageSelected = { bitmap: Bitmap? -> imageBitmap = bitmap },
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
                            Image = imageBitmap!!,
                            Latitude = selectedPosition!!.latitude,
                            Longitude = selectedPosition!!.longitude,
                            PrivateGroup = isPrivate
                        )
                        coroutineScope.launch {
                            val response = createStable(token, stableRequest, context)
                            Log.d("createStable", "Response: $response")
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