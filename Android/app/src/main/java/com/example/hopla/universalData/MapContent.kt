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
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.hopla.apiService.fetchTrailsOnMap
import kotlinx.coroutines.launch

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