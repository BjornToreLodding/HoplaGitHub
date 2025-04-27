package com.example.hopla.universalData

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.hopla.newTrip.AlertDialogContent
import com.example.hopla.R
import com.example.hopla.apiService.createNewHike
import com.example.hopla.apiService.fetchHorses
import com.example.hopla.apiService.fetchTrailCoordinates
import com.example.hopla.apiService.fetchTrailsOnMap
import com.example.hopla.apiService.fetchUserHikeCoordinates
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// Map screen for displaying trails on a map
@Composable
fun MapScreen() {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val zoomLevel = remember { mutableIntStateOf(10) }
    val latitude = remember { mutableDoubleStateOf(0.0) }
    val longitude = remember { mutableDoubleStateOf(0.0) }
    val trails = remember { mutableStateOf(emptyList<MapTrail>()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    AndroidView({ mapView }) {
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            enableMyLocation(googleMap, context)

            // Get the phone's location and move the camera to that location
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationProvider = LocationManager.GPS_PROVIDER
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
                lastKnownLocation?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel.intValue.toFloat()))
                }
            }

            // Update zoom level and center coordinates when the camera changes
            googleMap.setOnCameraIdleListener {
                zoomLevel.intValue = googleMap.cameraPosition.zoom.toInt()
                latitude.doubleValue = googleMap.cameraPosition.target.latitude
                longitude.doubleValue = googleMap.cameraPosition.target.longitude
                Log.d("MapScreen", "Zoom level: ${zoomLevel.intValue}, Latitude: ${latitude.doubleValue}, Longitude: ${longitude.doubleValue}")

                // Fetch trails and update markers
                coroutineScope.launch {
                    try {
                        val response = fetchTrailsOnMap(token, latitude.doubleValue, longitude.doubleValue, zoomLevel.intValue)
                        trails.value = response
                        googleMap.clear()
                        trails.value.forEach { trail ->
                            googleMap.addMarker(MarkerOptions().position(LatLng(trail.latMean, trail.longMean)).title(trail.name))
                        }
                    } catch (e: Exception) {
                        Log.e("MapScreen", "Error fetching trails", e)
                    }
                }
            }
        }
    }
}

private fun enableMyLocation(googleMap: GoogleMap, context: Context) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        googleMap.isMyLocationEnabled = true
    } else {
        // Request location permissions from the user
        // This part should be handled in your activity or a higher-level composable
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

@Composable
fun SimpleMapScreen(onPositionSelected: (LatLng) -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val selectedPosition = remember { mutableStateOf<LatLng?>(null) }

    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            enableMyLocation(googleMap, context)

            googleMap.setOnMapClickListener { latLng ->
                selectedPosition.value = latLng
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Position"))
                onPositionSelected(latLng)
            }

            selectedPosition.value?.let {
                googleMap.addMarker(MarkerOptions().position(it).title("Selected Position"))
            }
        }
    }
}

@Composable
fun StartTripMapScreen(trailId: String, navController: NavController) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    val trailCoordinates = remember { mutableStateOf<List<TrailCoordinate>>(emptyList()) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var tripName by remember { mutableStateOf("") }
    var tripNotes by remember { mutableStateOf("") }
    var selectedHorse by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var horses by remember { mutableStateOf(listOf<String>()) }
    var horseMap by remember { mutableStateOf(mapOf<String, Horse>()) }
    var newHike by remember { mutableStateOf<NewHike?>(null) }
    val startTime = remember { System.currentTimeMillis() }
    var trailDistance by remember { mutableDoubleStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) } // Loading state

    val polylineColor = MaterialTheme.colorScheme.primary.toArgb()

    LaunchedEffect(Unit) {
        val fetchedHorses = fetchHorses("", UserSession.token)
        horses = fetchedHorses.map { it.name }
        horseMap = fetchedHorses.associateBy { it.name }
    }

    // Fetch trail coordinates
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val trailResponse = fetchTrailCoordinates(trailId, token)
            trailCoordinates.value = trailResponse?.allCoords ?: emptyList()
            trailDistance = trailResponse?.distance ?: 0.0
            isLoading = false // Update loading state
            Log.d("StartTripMapScreen", "Trail Coordinates: ${trailCoordinates.value}")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Show a loading indicator while fetching data
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading trail data...")
            }
        } else {
            AndroidView({ mapView }, modifier = Modifier.weight(1f)) {
                mapView.getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    enableMyLocation(googleMap, context)

                    if (trailCoordinates.value.isNotEmpty()) {
                        val polylineOptions = PolylineOptions().apply {
                            color(polylineColor)
                            width(5f)
                        }

                        trailCoordinates.value.forEachIndexed { index, coordinate ->
                            val latLng = LatLng(coordinate.lat, coordinate.lng)
                            polylineOptions.add(latLng)

                            if (index == 0) {
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title("Start Point")
                                )
                            }
                        }

                        googleMap.addPolyline(polylineOptions)

                        val firstCoordinate = trailCoordinates.value.first()
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(firstCoordinate.lat, firstCoordinate.lng),
                                15f
                            )
                        )
                    } else {
                        Log.d("StartTripMapScreen", "No coordinates available to display.")
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.popBackStack() }, shape = RectangleShape) {
                Text(stringResource(R.string.cancel), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
            }
            Button(onClick = { showSaveDialog = true }, shape = RectangleShape) {
                Text(stringResource(R.string.save), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text(stringResource(R.string.save_trip)) },
                text = {
                    AlertDialogContent(
                        tripName = tripName,
                        onTripNameChange = { tripName = it },
                        tripNotes = tripNotes,
                        onTripNotesChange = { tripNotes = it },
                        horses = horses,
                        selectedHorse = selectedHorse,
                        onHorseSelected = { selectedHorse = it },
                        selectedImage = selectedImage,
                        onImageSelected = { selectedImage = it }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showSaveDialog = false
                        val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(java.util.Date())
                        val durationMillis = System.currentTimeMillis() - startTime
                        val durationMinutes = (durationMillis / 60000).toInt()
                        val durationSeconds = ((durationMillis % 60000) / 1000).toInt()
                        val durationStr = String.format(Locale.GERMANY, "%02d,%02d", durationMinutes, durationSeconds)
                        val distanceStr = String.format(Locale.GERMANY, "%.2f", trailDistance)

                        val coordinates = trailCoordinates.value.map {
                            Coordinate(timestamp = 0, lat = it.lat, long = it.lng)
                        }

                        newHike = NewHike(
                            StartetAt = currentTime,
                            Distance = distanceStr,
                            Duration = durationStr,
                            Coordinates = coordinates,
                            Title = tripName.ifEmpty { null },
                            Description = tripNotes.ifEmpty { null },
                            HorseId = horseMap[selectedHorse]?.id,
                            TrailId = trailId
                        )

                        newHike?.let {
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = createNewHike(UserSession.token, it, selectedImage)
                                Log.d("StartTripMapScreen", "Create Hike Response: $response")
                            }
                        }
                        navController.popBackStack()
                    }, shape = RectangleShape) {
                        Text(stringResource(R.string.save), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                dismissButton = {
                    Button(onClick = { showSaveDialog = false }, shape = RectangleShape) {
                        Text(stringResource(R.string.cancel), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    }
}

//-------------Functions to open up a map that displays coordinates for userhikes ------------
@Composable
fun CoordinatesOnMap(userHikeId: String, token: String, onClose: () -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val coordinates = remember { mutableStateOf<List<HikeCoordinate>>(emptyList()) }

    LaunchedEffect(userHikeId) {
        coroutineScope.launch {
            val fetchedCoordinates = fetchUserHikeCoordinates(userHikeId, token)
            if (fetchedCoordinates != null) {
                coordinates.value = fetchedCoordinates
                Log.d("CoordinatesOnMap", "Fetched coordinates: $fetchedCoordinates")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView })

        LaunchedEffect(coordinates.value) {
            if (coordinates.value.isNotEmpty()) {
                mapView.getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    enableMyLocation(googleMap, context)

                    val boundsBuilder = LatLngBounds.Builder()
                    val polylineOptions = PolylineOptions().apply {
                        color(Color.Black.toArgb())
                        width(5f)
                    }

                    coordinates.value.forEach { coord ->
                        val latLng = LatLng(coord.lat, coord.lng)
                        boundsBuilder.include(latLng)
                        polylineOptions.add(latLng)
                        //googleMap.addMarker(MarkerOptions().position(latLng))
                    }

                    googleMap.addPolyline(polylineOptions)
                    val bounds = boundsBuilder.build()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    Log.d("CoordinatesOnMap", "Polyline added with coordinates: ${polylineOptions.points}")
                }
            } else {
                Log.d("CoordinatesOnMap", "No coordinates to display")
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
    }

}

@Composable
fun MapButton(userHikeId: String, token: String) {
    var showMap by remember { mutableStateOf(false) }
    Column {
        Text(
            text = stringResource(R.string.show_on_map),
            modifier = Modifier.clickable{ showMap = true }.padding(16.dp),
            style = generalTextStyle,
            color = MaterialTheme.colorScheme.primary
        )
        if (showMap) {
            Dialog(onDismissRequest = { showMap = false }) {
                CoordinatesOnMap(userHikeId = userHikeId, token = token, onClose = { showMap = false })
            }
        }
    }
}

//-------------Functions to open up a map that displays coordinates for trails ------------
@Composable
fun CoordinatesOnMapTrail(trailId: String, token: String, onClose: () -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val coordinates = remember { mutableStateOf<List<TrailCoordinate>>(emptyList()) }

    LaunchedEffect(trailId) {
        coroutineScope.launch {
            val trailResponse = fetchTrailCoordinates(trailId, token)
            coordinates.value = trailResponse?.allCoords ?: emptyList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ mapView })

        LaunchedEffect(coordinates.value) {
            if (coordinates.value.isNotEmpty()) {
                mapView.getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    enableMyLocation(googleMap, context)

                    val boundsBuilder = LatLngBounds.Builder()
                    val polylineOptions = PolylineOptions().apply {
                        color(Color.Black.toArgb())
                        width(5f)
                    }

                    coordinates.value.forEach { coord ->
                        val latLng = LatLng(coord.lat, coord.lng)
                        boundsBuilder.include(latLng)
                        polylineOptions.add(latLng)
                    }

                    googleMap.addPolyline(polylineOptions)
                    val bounds = boundsBuilder.build()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
    }
}

@Composable
fun MapButtonTrail(trailId: String, token: String) {
    var showMap by remember { mutableStateOf(false) }
    Column {
        Text(
            text = stringResource(R.string.show_on_map),
            modifier = Modifier.clickable { showMap = true }.padding(16.dp),
            style = generalTextStyle,
            color = MaterialTheme.colorScheme.primary
        )
        if (showMap) {
            Dialog(onDismissRequest = { showMap = false }) {
                CoordinatesOnMapTrail(trailId = trailId, token = token, onClose = { showMap = false })
            }
        }
    }
}