package com.turbodev.parkar.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.turbodev.parkar.data.ParkingPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationManager(private val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val parkingPreferences = ParkingPreferences(context)

    fun initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun saveParkingLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Guardar la ubicación del aparcamiento
                        parkingPreferences.saveParkingLocation(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                        Toast.makeText(
                            context,
                            "Ubicación de aparcamiento guardada",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "No se pudo obtener la ubicación actual",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Error al guardar la ubicación",
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

    fun navigateToParkingLocation() {
        val parkingLocation = parkingPreferences.getParkingLocation()

        if (parkingLocation != null) {
            val (latitude, longitude) = parkingLocation

            // Crear una URI para abrir la aplicación de mapas
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            // Verificar si hay una aplicación que pueda manejar este intent
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                // Si Google Maps no está instalado, abrir con cualquier app de mapas
                val genericMapIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude"))
                context.startActivity(genericMapIntent)
            }
        } else {
            Toast.makeText(
                context,
                "No hay ubicación de aparcamiento guardada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}