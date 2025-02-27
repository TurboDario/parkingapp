package com.turbodev.parkar.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

class LocationManager(private val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val parkingPreferences = ParkingPreferences(context)

    // Nueva función para obtener ubicación guardada
    fun getCurrentLocation(): LatLng? = parkingPreferences.getParkingLocation()

    fun initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    // Versión sobrecargada para guardado manual
    fun saveParkingLocation(latLng: LatLng) {
        parkingPreferences.saveParkingLocation(
            latitude = latLng.latitude,
            longitude = latLng.longitude
        )
        Toast.makeText(
            context,
            "Ubicación manual guardada",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Versión original para guardado automático
    @SuppressLint("MissingPermission")
    fun saveParkingLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        saveParkingLocation(LatLng(it.latitude, it.longitude))
                    } ?: run {
                        Toast.makeText(
                            context,
                            "No se pudo obtener la ubicación",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error al obtener ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Versión sobrecargada para navegación con parámetro
    fun navigateToParkingLocation(latLng: LatLng) {
        openNavigationApp(latLng.latitude, latLng.longitude)
    }

    // Versión original para navegación con ubicación guardada
    fun navigateToParkingLocation() {
        parkingPreferences.getParkingLocation()?.let {
            openNavigationApp(it.latitude, it.longitude)
        } ?: Toast.makeText(
            context,
            "No hay ubicación guardada",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun openNavigationApp(latitude: Double, longitude: Double) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
                )
            )
        }
    }
}