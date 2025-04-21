package com.example.hopla.newTrip

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.hopla.BottomBarViewModel
import com.example.hopla.R
import com.example.hopla.apiService.createNewHike
import com.example.hopla.apiService.fetchHorses
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.universalData.Coordinate
import com.example.hopla.universalData.Horse
import com.example.hopla.universalData.NewHike
import com.example.hopla.universalData.UserSession
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun NewTripScreen(bottomBarViewModel: BottomBarViewModel) {
    var isRunning by remember { mutableStateOf(false) }
    var time by remember { mutableDoubleStateOf(0.0) }
    var distance by remember { mutableDoubleStateOf(0.0) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var tripName by remember { mutableStateOf("") }
    var tripNotes by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var horses by remember { mutableStateOf(listOf<String>()) }
    var selectedHorse by remember { mutableStateOf("") }
    var selectedHorseId by remember { mutableStateOf<String?>(null) }
    var horseMap by remember { mutableStateOf(mapOf<String, Horse>()) }
    var newHike by remember { mutableStateOf<NewHike?>(null) }
    var coordinates by remember { mutableStateOf(listOf<Coordinate>()) }
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(Unit) {
        try {
            val fetchedHorses = fetchHorses("", UserSession.token)
            horses = fetchedHorses.map { it.name }
            horseMap = fetchedHorses.associateBy { it.name }
        } catch (e: Exception) {
            Log.e("NewTripScreen", "Error fetching horses: ${e.message}", e)
            horses = emptyList()
            horseMap = emptyMap()
        }
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            bottomBarViewModel.hideBottomBar()
        } else {
            bottomBarViewModel.showBottomBar()
        }
    }

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
                        coordinates = coordinates + Coordinate(
                            timestamp = time.toLong(),
                            lat = newLocation.latitude,
                            long = newLocation.longitude
                        )
                    }
                }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lastLocation = location
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(location.latitude, location.longitude), 15f
                    )
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
                .background(MaterialTheme.colorScheme.tertiary)
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
                        val minutes = (time / 60).toInt()
                        val seconds = (time % 60).toInt()
                        Text(
                            text = stringResource(R.string.time),
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
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
                                val currentTime = SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    Locale.getDefault()
                                ).format(
                                    Date()
                                )
                                val distanceStr =
                                    String.format(Locale.GERMANY, "%.2f", distance) // e.g., "30,30"
                                val durationStr = String.format(
                                    Locale.GERMANY,
                                    "%02d,%02d",
                                    (time / 60).toInt(),
                                    (time % 60).toInt()
                                ) // e.g., "5,45"

                                newHike = NewHike(
                                    StartetAt = currentTime,
                                    Distance = distanceStr,
                                    Duration = durationStr,
                                    Coordinates = coordinates
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.small.copy(all = CornerSize(50)),
                        modifier = Modifier.size(90.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Background color
                            contentColor = MaterialTheme.colorScheme.onPrimary // Text color
                        )
                    ) {
                        Text(
                            text = if (isRunning) stringResource(R.string.stop) else stringResource(
                                R.string.start
                            ), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary
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
                        Text(
                            text = stringResource(R.string.distance),
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = String.format("%.2f km", distance),
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                AlertDialogContent(
                    tripName = tripName,
                    onTripNameChange = { tripName = it },
                    tripNotes = tripNotes,
                    onTripNotesChange = { tripNotes = it },
                    horses = horses,
                    selectedHorse = selectedHorse,
                    onHorseSelected = { selectedHorse = it; selectedHorseId = horseMap[it]?.id },
                    selectedImage = selectedImage,
                    onImageSelected = { selectedImage = it }
                )
            },
            confirmButton = {
                Button(onClick = {
                    // Save the trip / set to 0
                    showDialog = false
                    val minutes = (time / 60).toInt()
                    val seconds = (time % 60).toInt()
                    val distanceStr =
                        String.format(Locale.GERMANY, "%.2f", distance) // e.g., "30,30"
                    val durationStr =
                        String.format(Locale.GERMANY, "%02d,%02d", minutes, seconds) // e.g., "5,45"
                    newHike = newHike?.copy(
                        Distance = distanceStr,
                        Duration = durationStr,
                        Coordinates = coordinates,
                        Title = tripName.ifEmpty { null },
                        Description = tripNotes.ifEmpty { null },
                        HorseId = selectedHorseId
                    )
                    time = 0.0
                    distance = 0.0
                    tripName = ""
                    tripNotes = ""
                    coordinates = emptyList()
                    newHike?.let {
                        Log.d("NewTripScreen", "NewHike: $it")
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = createNewHike(UserSession.token, it, selectedImage)
                            Log.d("NewTripScreen", "Create Hike Response: $response")
                        }
                    }
                }, shape = RectangleShape ) {
                    Text(text = stringResource(R.string.save), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }, shape = RectangleShape) {
                    Text(stringResource(R.string.cancel), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }
}