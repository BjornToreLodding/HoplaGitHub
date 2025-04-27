package com.example.hopla.community

//noinspection UsingMaterialAndMaterial3Libraries
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hopla.R
import com.example.hopla.apiService.createStable
import com.example.hopla.apiService.fetchStableDetails
import com.example.hopla.apiService.fetchStables
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyleVerySmall
import com.example.hopla.universalData.AddButton
import com.example.hopla.universalData.FetchStableRequest
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.SearchBar
import com.example.hopla.universalData.SimpleMapScreen
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.StableDetails
import com.example.hopla.universalData.StableRequest
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.getCurrentLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// Community screen that shows a list of communities
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

    val currentContext = rememberUpdatedState(context)

    LaunchedEffect(Unit) {
        Log.d("CommunityScreen", "LaunchedEffect triggered")
        getCurrentLocation(currentContext.value) { location ->
            Log.d(
                "CommunityScreen",
                "Location received: ${location.latitude}, ${location.longitude}"
            )
            latitude = location.latitude
            longitude = location.longitude
            coroutineScope.launch {
                Log.d("CommunityScreen", "Coroutine launched")
                val request = FetchStableRequest(
                    token = token,
                    search = searchQuery,
                    userId = "",
                    latitude = latitude,
                    longitude = longitude,
                    pageNumber = pageNumber
                )

                val fetchedStables = fetchStables(request)
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
                val request = FetchStableRequest(
                    token = token,
                    search = searchQuery,
                    userId = if (currentPage == "liked") UserSession.userId else "",
                    latitude = latitude,
                    longitude = longitude,
                    pageNumber = newPageNumber
                )
                val fetchedStables = fetchStables(request)
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
            val request = FetchStableRequest(
                token = token,
                search = searchQuery,
                userId = if (currentPage == "liked") UserSession.userId else "",
                latitude = latitude,
                longitude = longitude,
                pageNumber = pageNumber
            )

            val fetchedStables = fetchStables(request)
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
                    StableCard(stable, navController, likedStables, token)
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
                    .fillMaxHeight(0.1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
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
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                    // Title of the community group
                    Text(
                        text = community.name,
                        style = headerTextStyleVerySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    // Info button to show details about the community
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            tint = MaterialTheme.colorScheme.onPrimary,
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
                                style = generalTextStyle,
                                color = Color.Black,
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
                    Button(onClick = { showDialog = false }, shape = RectangleShape) {
                        Text(
                            text = stringResource(R.string.close),
                            style = buttonTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
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

// Add a new community group screen
@Composable
fun AddCommunityScreen(navController: NavController, token: String) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMap by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            LocalContext.current

            ScreenHeader(
                navController = navController,
                headerText = stringResource(R.string.add_new_community)
            )
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
                text = if (imageBitmap == null) stringResource(R.string.add_image) else stringResource(
                    R.string.change_image
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showMap = !showMap },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text(
                    text = stringResource(R.string.choose_position),
                    style = buttonTextStyle,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (showMap) {
                SimpleMapScreen { position ->
                    selectedPosition = position
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedPosition != null && imageBitmap != null) {
                        val stableRequest = StableRequest(
                            name = name,
                            description = description,
                            image = imageBitmap!!,
                            latitude = selectedPosition!!.latitude,
                            longitude = selectedPosition!!.longitude,
                            privateGroup = false
                        )
                        coroutineScope.launch {
                            val response = createStable(token, stableRequest)
                            Log.d("createStable", "Response: $response")
                            navController.popBackStack()
                        }
                    } else {
                        // Show a message to select an option, position, and image
                    }
                },
                enabled = name.isNotBlank() && description.isNotBlank() && selectedPosition != null && imageBitmap != null,
                shape = RectangleShape
            ) {
                Text(
                    text = stringResource(R.string.add_new_community),
                    style = buttonTextStyle,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
