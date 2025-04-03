package com.example.hopla.profile

import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.fetchTrailFilters
import com.example.hopla.apiService.fetchUserHikes
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.ui.theme.underlinedTextStyleSmall
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.TrailFilter
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.launch

@Composable
fun MyTripsScreen(navController: NavController) {
    var userHikes by remember { mutableStateOf<List<Hike>>(emptyList()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var filters by remember { mutableStateOf<List<TrailFilter>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(pageNumber) {
        coroutineScope.launch {
            try {
                val newHikes = fetchUserHikes(token, pageNumber)
                userHikes = userHikes + newHikes
            } catch (e: Exception) {
                Log.e("UserHikesScreen", "Error fetching user hikes", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                filters = fetchTrailFilters(token)
            } catch (e: Exception) {
                Log.e("UserHikesScreen", "Error fetching trail filters", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, stringResource(R.string.my_trips))

            if (userHikes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userHikes) { hike ->
                        HikeItem(hike = hike, onEditTrip = { /* Handle edit trip */ }, isMyTripsScreen = true)
                    }
                    item {
                        Button(
                            onClick = { pageNumber++ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = stringResource(R.string.load_more), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HikeItem(hike: Hike, onEditTrip: (Hike) -> Unit, isMyTripsScreen: Boolean) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditTripDialog(
            hike = hike,
            onDismiss = { showEditDialog = false },
            onSave = { name, description, imageBitmap ->
                // Handle save logic here
                showEditDialog = false
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
            .clickable {
                // Handle item click
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = hike.pictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = hike.trailName, style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary)
            Text(text = stringResource(R.string.length) + ": ${hike.length} km", style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
            Text(
                text = stringResource(R.string.duration) + ": ${hike.duration} min",
                style = generalTextStyle,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isMyTripsScreen) {
                Row {
                    Text(
                        text = stringResource(R.string.edit_trip),
                        style = generalTextStyle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            showEditDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.share_trip),
                        style = generalTextStyle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            // Handle share trip click
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditTripDialog(
    hike: Hike,
    onDismiss: () -> Unit,
    onSave: (String, String, Bitmap?) -> Unit
) {
    var name by remember { mutableStateOf(hike.trailName) }
    var description by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_trip),
                    style = underheaderTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
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
                Spacer(modifier = Modifier.height(8.dp))
                ImagePicker(
                    onImageSelected = { bitmap -> imageBitmap = bitmap },
                    text = stringResource(R.string.add_image)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onSave(name, description, imageBitmap)
                }) {
                    Text(
                        text = stringResource(R.string.save),
                        style = buttonTextStyle
                    )
                }
                Button(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = buttonTextStyle
                    )
                }
            }
        }
    }
}