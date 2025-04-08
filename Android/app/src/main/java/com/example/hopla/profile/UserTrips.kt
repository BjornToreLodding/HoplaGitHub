package com.example.hopla.profile

//noinspection UsingMaterialAndMaterial3Libraries
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.createTrail
import com.example.hopla.apiService.fetchHorses
import com.example.hopla.apiService.fetchTrailFilters
import com.example.hopla.apiService.fetchUserHikes
import com.example.hopla.apiService.updateUserHike
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.CreateTrailRequest
import com.example.hopla.universalData.FilterData
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.Horse
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.MapButton
import com.example.hopla.universalData.MapButtonTrail
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.TrailFilter
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MyTripsScreen(navController: NavController) {
    var userHikes by remember { mutableStateOf<List<Hike>>(emptyList()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var filters by remember { mutableStateOf<List<TrailFilter>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    fun reloadHikes() {
        coroutineScope.launch {
            try {
                userHikes = fetchUserHikes(token, pageNumber)
            } catch (e: Exception) {
                Log.e("UserHikesScreen", "Error fetching user hikes", e)
            }
        }
    }

    LaunchedEffect(pageNumber) {
        reloadHikes()
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
                        HikeItem(hike = hike, filters = filters, isMyTripsScreen = true, onHikesReload = { reloadHikes() })
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
fun HikeItem(
    hike: Hike,
    filters: List<TrailFilter> = emptyList(),
    isMyTripsScreen: Boolean,
    onHikesReload: () -> Unit = {}
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    if (showEditDialog) {
        EditTripDialog(
            hike = hike,
            onDismiss = { showEditDialog = false },
            onSave = { tripName, tripDescription, selectedImage, selectedHorseId ->
                // Handle save action
                showEditDialog = false
            },
            onHikesReload = onHikesReload
        )
    }

    if (showShareDialog) {
        ShareTripDialog(
            hike = hike,
            filters = filters,
            onDismiss = { showShareDialog = false },
            onSave = { name: String, description: String, imageBitmap: Bitmap?, userHikeId: String, filterData: List<FilterData> ->
                coroutineScope.launch {
                    val createTrailRequest = CreateTrailRequest(
                        Name = name,
                        description = description,
                        UserHikeId = userHikeId,
                        Filters = filterData
                    )
                    try {
                        val response = createTrail(token, imageBitmap!!, createTrailRequest)
                        Log.d("createTrail", "Response: $response")
                        showShareDialog = false
                    } catch (e: Exception) {
                        Log.e("createTrail", "Error creating trail", e)
                    }
                }
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
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = hike.title ?: " ",
                style = underheaderTextStyle,
                color = MaterialTheme.colorScheme.secondary
            )
            if(!hike.trailButton) {
                Text (
                    text = stringResource(R.string.trail) + ": ${hike.trailName}",
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(text = stringResource(R.string.length) + ": ${hike.length} km", style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
            Text(
                text = stringResource(R.string.duration2) + ": ${hike.duration} min",
                style = generalTextStyle,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isMyTripsScreen && hike.trailButton) {
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
                            showShareDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MapButton(userHikeId = hike.id, token = UserSession.token)
                }
            }
            if (isMyTripsScreen && !hike.trailButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.edit_trip),
                        style = generalTextStyle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            showEditDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    hike.trailId?.let { trailId ->
                        MapButtonTrail(trailId = trailId, token = UserSession.token)
                    }
                }
            }
        }
    }
}

@Composable
fun ShareTripDialog(
    hike: Hike,
    filters: List<TrailFilter>,
    onDismiss: () -> Unit,
    onSave: (String, String, Bitmap?, String, List<FilterData>) -> Unit
) {
    var name by remember { mutableStateOf(hike.trailName) }
    var description by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(filters.firstOrNull()?.name ?: "") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val selectedOptions = remember { mutableStateMapOf<String, MutableSet<String>>() }

    val isSaveEnabled = name.isNotBlank() && description.isNotBlank() && selectedOptions.isNotEmpty() && imageBitmap != null

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
                    style = headerTextStyleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.name), style = generalTextStyle, color = MaterialTheme.colorScheme.secondary) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = stringResource(R.string.description), style = generalTextStyle, color = MaterialTheme.colorScheme.secondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 2.dp)
                )
                Box(modifier = Modifier.height(150.dp)) {
                    LazyColumn {
                        items(filters) { filter ->
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = filter.displayName,
                                    style = underheaderTextStyle,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    filter.options.forEach { option ->
                                        val isSelected = selectedOptions[filter.name]?.contains(option) == true
                                        Text(
                                            text = option,
                                            style = generalTextStyle,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                                .clickable {
                                                    selectedOptions[filter.name] = selectedOptions[filter.name]?.toMutableSet()?.apply {
                                                        if (isSelected) remove(option) else add(option)
                                                    } ?: mutableSetOf(option)
                                                }
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val filterData = selectedOptions.flatMap { (filterName, options) ->
                                options.map { option ->
                                    FilterData(
                                        FilterDefinitionId = filters.find { it.name == filterName }?.id ?: "",
                                        Value = option
                                    )
                                }
                            }
                            onSave(name, description, imageBitmap, hike.id, filterData)
                        },
                        enabled = isSaveEnabled
                    ) {
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
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditTripDialog(
    hike: Hike,
    onDismiss: () -> Unit,
    onSave: (String, String, Bitmap?, String?) -> Unit,
    onHikesReload: () -> Unit
) {
    var tripName by remember { mutableStateOf(hike.title) }
    var tripDescription by remember { mutableStateOf(hike.comment ?: "") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var horses by remember { mutableStateOf(listOf<String>()) }
    var selectedHorse by remember { mutableStateOf("") }
    var horseMap by remember { mutableStateOf(mapOf<String, Horse>()) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val fetchedHorses = fetchHorses("", UserSession.token)
        horses = fetchedHorses.map { it.name }
        horseMap = fetchedHorses.associateBy { it.name }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.edit_trip), style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary) },
        text = {
            Column {
                tripName?.let {
                    TextField(
                        value = it,
                        onValueChange = { tripName = it },
                        label = { Text(text = stringResource(R.string.trip_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = tripDescription,
                    onValueChange = { tripDescription = it },
                    label = { Text(text = stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        readOnly = true,
                        value = if (selectedHorse.isEmpty()) "No Horse Selected" else selectedHorse,
                        onValueChange = {},
                        label = { Text("Select Horse") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                selectedHorse = ""
                                expanded = false
                            }
                        ) {
                            Text("No Horse Selected", color = MaterialTheme.colorScheme.onSurface)
                        }
                        horses.forEach { horse ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedHorse = horse
                                    expanded = false
                                }
                            ) {
                                Text(horse, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ImagePicker(
                    onImageSelected = { bitmap -> selectedImage = bitmap },
                    text = stringResource(R.string.change_image),
                )
                selectedImage?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedTitle = if (tripName != hike.title) tripName else null
                val updatedDescription = if (tripDescription != hike.comment) tripDescription else null
                val updatedHorseId = if (selectedHorse.isNotEmpty()) horseMap[selectedHorse]?.id else null

                CoroutineScope(Dispatchers.IO).launch {
                    updateUserHike(
                        token = UserSession.token,
                        userHikeId = hike.id,
                        title = updatedTitle,
                        horseId = updatedHorseId,
                        imageBitmap = selectedImage,
                        description = updatedDescription
                    )
                }
                onSave(tripName ?: "", tripDescription ?: "", selectedImage, updatedHorseId)
                onHikesReload()
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}