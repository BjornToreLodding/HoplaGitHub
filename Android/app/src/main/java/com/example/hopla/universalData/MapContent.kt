package com.example.hopla.universalData

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import android.Manifest
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hopla.apiService.fetchTrailCoordinates
import com.example.hopla.apiService.fetchTrailsOnMap
import com.example.hopla.apiService.fetchUserHikeCoordinates
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import com.example.hopla.R

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
fun StartTripMapScreen(trailId: String) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val zoomLevel = remember { mutableIntStateOf(15) } // Adjusted zoom level for closer view
    val latitude = remember { mutableDoubleStateOf(0.0) }
    val longitude = remember { mutableDoubleStateOf(0.0) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    val trailCoordinates = remember { mutableStateOf<List<TrailCoordinate>>(emptyList()) }

    AndroidView({ mapView }) {
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            enableMyLocation(googleMap, context)

            // Fetch trail coordinates and draw polyline
            coroutineScope.launch {
                val trailResponse = fetchTrailCoordinates(trailId, token)
                trailResponse?.let {
                    trailCoordinates.value = it.allCoords
                    val polylineOptions = PolylineOptions().apply {
                        addAll(it.allCoords.map { coord -> LatLng(coord.lat, coord.long) })
                        color(Color.Black.toArgb())
                        width(5f)
                    }
                    googleMap.addPolyline(polylineOptions)

                    // Move the camera to the center of the trail coordinates
                    if (trailCoordinates.value.isNotEmpty()) {
                        val boundsBuilder = LatLngBounds.Builder()
                        trailCoordinates.value.forEach { coord ->
                            boundsBuilder.include(LatLng(coord.lat, coord.long))
                        }
                        val bounds = boundsBuilder.build()
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    }
                }
            }

            // Update zoom level and center coordinates when the camera changes
            googleMap.setOnCameraIdleListener {
                zoomLevel.intValue = googleMap.cameraPosition.zoom.toInt()
                latitude.doubleValue = googleMap.cameraPosition.target.latitude
                longitude.doubleValue = googleMap.cameraPosition.target.longitude
                Log.d("MapScreen", "Zoom level: ${zoomLevel.intValue}, Latitude: ${latitude.doubleValue}, Longitude: ${longitude.doubleValue}")
            }
        }
    }
}

//-------------Functions to open up a map that displays coordinates
@Composable
fun CoordinatesOnMap(userHikeId: String, token: String, onClose: () -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val coordinates = remember { mutableStateOf<List<HikeCoordinate>>(emptyList()) }
    val zoomLevel = remember { mutableIntStateOf(15) } // Adjusted zoom level for closer view

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
            color = MaterialTheme.colorScheme.primary
        )
        if (showMap) {
            Dialog(onDismissRequest = { showMap = false }) {
                CoordinatesOnMap(userHikeId = userHikeId, token = token, onClose = { showMap = false })
            }
        }
    }
}