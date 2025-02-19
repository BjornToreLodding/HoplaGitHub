package com.example.hopla

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.delay
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color

@Preview
@Composable
fun NewTripScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(0) }
    var distance by remember { mutableStateOf(0.0) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var tripName by remember { mutableStateOf("") }
    var tripNotes by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }
    var selectedWords by remember { mutableStateOf(listOf<String>()) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isRunning) {
                    val newLocation = locationResult.lastLocation
                    if (newLocation != null) {
                        lastLocation?.let {
                            val distanceIncrement = it.distanceTo(newLocation) / 1000.0
                            distance += distanceIncrement
                        }
                        lastLocation = newLocation
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lastLocation = location
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }

    DisposableEffect(isRunning) {
        if (isRunning) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            time++
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 10f)
    }

    // filter words list
    val filterWords = listOf(
        stringResource(R.string.asphalt),
        stringResource(R.string.gravel),
        stringResource(R.string.parking)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 75.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = String.format(
                                "%02d:%02d:%02d",
                                time / 3600,
                                (time % 3600) / 60,
                                time % 60
                            )
                        )
                        Text(text = stringResource(R.string.time))
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (isRunning) {
                                isRunning = false
                                showDialog = true
                            } else {
                                isRunning = !isRunning
                            }
                        },
                        shape = MaterialTheme.shapes.small.copy(all = CornerSize(50)),
                        modifier = Modifier.size(85.dp)
                    ) {
                        Text(
                            text = if (isRunning) stringResource(R.string.stop) else stringResource(
                                R.string.start
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = String.format("%.2f km", distance))
                        Text(text = stringResource(R.string.distance))
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Column {
                    TextField(
                        value = tripName,
                        onValueChange = { tripName = it },
                        label = { Text(text = stringResource(R.string.trip_name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            TextField(
                                value = tripNotes,
                                onValueChange = { tripNotes = it },
                                label = { Text(text = stringResource(R.string.description)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            IconButton(
                                onClick = { /* Handle click */ },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    // Horizontally Scrollable Selected Words Box with Dropdown Icon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp) // Fixed height for word list
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween // Ensures dropdown icon stays on the right
                        ) {
                            // Scrollable Word List
                            Row(
                                modifier = Modifier
                                    .weight(1f) // Makes sure words take up available space
                                    .padding(end = 8.dp) // Adjust padding for good spacing
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                selectedWords.forEach { word ->
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primary)
                                            .height(36.dp)
                                            .width(72.dp)
                                            .padding(end = 8.dp)
                                            .clickable { selectedWords = selectedWords - word },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = word, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                            // Dropdown Menu Icon (Fixed on the Right)
                            IconButton(
                                onClick = { showDropdown = !showDropdown },
                                modifier = Modifier
                                    .size(36.dp) // Adjust size for good spacing
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Word",
                                )
                            }

                            // Dropdown Menu
                            DropdownMenu(
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                filterWords.forEach { word ->
                                    val isSelected = word in selectedWords

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = word,
                                                color = if (isSelected) Color.Gray else Color.Unspecified // Dim if selected
                                            )
                                        },
                                        onClick = {
                                            if (!isSelected) {
                                                selectedWords = selectedWords + word
                                                showDropdown = false
                                            }
                                        },
                                        enabled = !isSelected // Disable if already selected
                                    )
                                }
                            }
                        }
                    }


                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    time = 0
                    distance = 0.0
                }) {
                    Text(text = stringResource(R.string.save))
                }
            }
        )
    }
}