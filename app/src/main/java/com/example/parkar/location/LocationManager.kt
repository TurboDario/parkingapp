package com.turbomonguerdev.parkar.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.turbomonguerdev.parkar.R

class LocationManager(private val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val parkingPreferences = ParkingPreferences(context)

    fun getCurrentLocation(): LatLng? = parkingPreferences.getParkingLocation()

    fun initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun saveParkingLocation(latLng: LatLng) {
        parkingPreferences.saveParkingLocation(latLng.latitude, latLng.longitude)
        showToast(context.getString(R.string.parking_location_saved))
    }

    @SuppressLint("MissingPermission")
    fun saveParkingLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        saveParkingLocation(LatLng(location.latitude, location.longitude))
                    } else {
                        showToast(context.getString(R.string.unable_to_get_location))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("LocationManager", "Error getting location", exception)
                    showToast(context.getString(R.string.error_getting_location))
                }
        } catch (e: Exception) {
            Log.e("LocationManager", "Unexpected error", e)
            showToast(context.getString(R.string.error_unexpected, e.message))
        }
    }

    fun navigateToParkingLocation(latLng: LatLng) {
        openNavigationApp(latLng.latitude, latLng.longitude)
    }

    fun navigateToParkingLocation() {
        parkingPreferences.getParkingLocation()?.let {
            openNavigationApp(it.latitude, it.longitude)
        } ?: showToast(context.getString(R.string.no_saved_location))
    }

    private fun openNavigationApp(latitude: Double, longitude: Double) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        val packageManager = context.packageManager
        if (mapIntent.resolveActivity(packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val fallbackUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun shareCurrentLocation() {
        val currentLocation = parkingPreferences.getParkingLocation()
        if (currentLocation != null) {
            val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${currentLocation.latitude},${currentLocation.longitude}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, mapsUrl)
            }
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_location)))
        } else {
            showToast(context.getString(R.string.no_saved_location))
        }
    }
}