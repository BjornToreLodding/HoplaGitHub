package com.example.hopla

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
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
                        Log.d(
                            "NewTripScreen",
                            "Received location: ${newLocation.latitude}, ${newLocation.longitude}"
                        )
                        lastLocation?.let {
                            val distanceIncrement = it.distanceTo(newLocation) / 1000.0
                            distance += distanceIncrement
                            Log.d(
                                "NewTripScreen",
                                "Distance increased by $distanceIncrement km, total: $distance km"
                            )
                        } ?: run {
                            Log.d("NewTripScreen", "Initializing lastLocation for first time")
                        }

                        lastLocation = newLocation
                    } else {
                        Log.e(
                            "NewTripScreen",
                            "Location result received but no new location available"
                        )
                    }
                } else {
                    Log.d("NewTripScreen", "Location callback triggered but tracking is stopped")
                }
            }
        }
    }

    // Ensure permissions are granted before proceeding
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(
                        "NewTripScreen",
                        "Last known location retrieved: ${location.latitude}, ${location.longitude}"
                    )
                    lastLocation = location
                } else {
                    Log.w("NewTripScreen", "No last known location available")
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
            Log.w("NewTripScreen", "Permissions not granted, requesting now")
        }
    }

    DisposableEffect(isRunning) {
        if (isRunning) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("NewTripScreen", "Starting location updates")
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                Log.e("NewTripScreen", "Location updates requested but permissions are missing")
            }
        } else {
            Log.d("NewTripScreen", "Stopping location updates")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        onDispose {
            Log.d("NewTripScreen", "Removing location updates on disposal")
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

    // UI Layout
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
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = tripNotes,
                        onValueChange = { tripNotes = it },
                        label = { Text(text = stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        // Add any additional content here
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Handle saving trip details here
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