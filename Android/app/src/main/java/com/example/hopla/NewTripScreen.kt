package com.example.hopla

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.delay

@Preview
@Composable
fun NewTripScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(0) }
    var distance by remember { mutableStateOf(0.0) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (isRunning) {
                val newLocation = locationResult.lastLocation
                if (newLocation != null) {
                    lastLocation?.let {
                        val distanceIncrement = it.distanceTo(newLocation) / 1000.0
                        distance += distanceIncrement
                        Log.d("NewTripScreen", "Distance increment: $distanceIncrement km, Total distance: $distance km")
                    }
                    lastLocation = newLocation
                }
            }
        }
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
                return@LaunchedEffect
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            time++
        }
    }

    if (showDialog) {
        showDialog = TripDialogue(showDialog, time, distance)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        Text(text = String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60))
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
                                showDialog = true
                            }
                            isRunning = !isRunning
                        },
                        shape = MaterialTheme.shapes.small.copy(all = CornerSize(50)),
                        modifier = Modifier.size(85.dp)
                    ) {
                        Text(text = if (isRunning) stringResource(R.string.stop) else stringResource(R.string.start))
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
}

@Composable
private fun TripDialogue(showDialog: Boolean, time: Int, distance: Double): Boolean {
    var showDialog1 = showDialog
    AlertDialog(
        onDismissRequest = { showDialog1 = false },
        title = { Text(text = stringResource(R.string.trip_summary)) },
        text = {
            Column {
                TextField(
                    value = stringResource(R.string.time) + ": " + String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60) + " | " + stringResource(R.string.distance) + ": ${String.format("%.2f km", distance)}",
                    onValueChange = {},
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Comments") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { showDialog1 = false }) {
                Text("OK")
            }
        },
        shape = RectangleShape
    )
    return showDialog1
}