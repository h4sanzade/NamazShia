package com.hasanzade.namazshia.location


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val city: String = "Unknown"
)

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getCurrentLocation(): LocationData? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val locationData = LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            city = getCityFromCoordinates(location.latitude, location.longitude)
                        )
                        continuation.resume(locationData)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }

    private fun getCityFromCoordinates(lat: Double, lng: Double): String {
        return when {
            // Baku coordinates
            lat in 40.3..40.5 && lng in 49.8..50.0 -> "baku"
            lat in 40.9..41.1 && lng in 28.8..29.0 -> "istanbul"
            lat in 39.8..40.0 && lng in 32.7..32.9 -> "ankara"
            else -> "istanbul"
        }
    }
}
