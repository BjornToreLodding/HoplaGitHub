package com.example.hopla.universalData

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

// Constants
private const val LOCATION_UPDATE_MIN_TIME_MS = 2000L
private const val LOCATION_UPDATE_MIN_DISTANCE_M = 0f
private const val LOCATION_UPDATE_TIMEOUT_MS = 10_000L

// Get the current location of the user
fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
        Log.d("CommunityScreen", "Location permissions not granted")
        return
    }

    val gpsProvider = LocationManager.GPS_PROVIDER
    val networkProvider = LocationManager.NETWORK_PROVIDER

    val gpsLocation: Location? = locationManager.getLastKnownLocation(gpsProvider)
    val networkLocation: Location? = locationManager.getLastKnownLocation(networkProvider)

    val bestLocation = when {
        gpsLocation != null -> gpsLocation
        networkLocation != null -> networkLocation
        else -> null
    }

    if (bestLocation != null) {
        Log.d(
            "CommunityScreen",
            "Using last known location: ${bestLocation.latitude}, ${bestLocation.longitude}"
        )
        onLocationReceived(LatLng(bestLocation.latitude, bestLocation.longitude))
    } else {
        Log.d("CommunityScreen", "No last known location, requesting updates")

        val locationListener = object : android.location.LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d(
                    "getLocation",
                    "Received new location: ${location.latitude}, ${location.longitude}"
                )
                onLocationReceived(LatLng(location.latitude, location.longitude))
                locationManager.removeUpdates(this) // Stop updates after first success
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Request updates from GPS and Network Providers
        locationManager.requestLocationUpdates(
            gpsProvider,
            LOCATION_UPDATE_TIMEOUT_MS, // Min time in milliseconds between updates
            LOCATION_UPDATE_MIN_DISTANCE_M,    // Min distance in meters
            locationListener
        )

        locationManager.requestLocationUpdates(
            networkProvider,
            LOCATION_UPDATE_MIN_TIME_MS,
            LOCATION_UPDATE_MIN_DISTANCE_M,
            locationListener
        )

        // Timeout if no location is found within 10 seconds
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Log.d("CommunityScreen", "Location update timeout")
            locationManager.removeUpdates(locationListener)
        }, LOCATION_UPDATE_TIMEOUT_MS) // 10 seconds timeout
    }
}
