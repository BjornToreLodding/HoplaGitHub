package com.example.hopla

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
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import androidx.compose.runtime.MutableState
import android.widget.Toast

@Composable
fun MapScreen() {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    val polyline: MutableState<Polyline?> = remember { mutableStateOf(null) }

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
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10f))
                }
            }

            // List of test coordinates in and around Gjøvik, Norway with individual names and trip coordinates
            val testLocations = listOf(
                TestLocation(
                    mainCoordinate = LatLng(60.7950, 10.6915),
                    name = "Gjøvikløypa",
                    tripCoordinates = listOf(
                        LatLng(60.7960, 10.6920),
                        LatLng(60.7970, 10.6930)
                    )
                ),
                TestLocation(
                    mainCoordinate = LatLng(60.8000, 10.7000),
                    name = "Vannstien",
                    tripCoordinates = listOf(
                        LatLng(60.8010, 10.7010),
                        LatLng(60.8020, 10.7020)
                    )
                ),
            )

            // Add markers for each test location and its trip coordinates
            testLocations.forEach { location ->
                val marker = googleMap.addMarker(MarkerOptions().position(location.mainCoordinate).title(location.name))
                marker?.tag = location

                googleMap.setOnMarkerClickListener { clickedMarker ->
                    val clickedLocation = clickedMarker.tag as? TestLocation
                    clickedLocation?.let {
                        // Show the name of the testLocation
                        Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()

                        if (polyline.value != null) {
                            // Remove existing polyline if any
                            polyline.value?.remove()
                            polyline.value = null
                        } else {
                            // Create a new polyline with the mainCoordinate and tripCoordinates
                            val polylineOptions = PolylineOptions().add(it.mainCoordinate).apply {
                                it.tripCoordinates.forEach { tripCoordinate ->
                                    add(tripCoordinate)
                                }
                            }
                            polyline.value = googleMap.addPolyline(polylineOptions)
                        }
                    }
                    true
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