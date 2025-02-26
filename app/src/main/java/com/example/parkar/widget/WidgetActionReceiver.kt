package com.example.parkar.widget

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.parkar.data.ParkingPreferences
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class WidgetActionReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val parkingPreferences = ParkingPreferences(context)

        when (intent.action) {
            ACTION_SAVE_LOCATION -> {
                // Verificar si tenemos permisos
                if (hasLocationPermission(context)) {
                    // Obtener ubicación actual
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                // Guardar la ubicación
                                parkingPreferences.saveParkingLocation(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                                Toast.makeText(
                                    context,
                                    "Ubicación guardada correctamente",
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
                                "Error al guardar ubicación",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Se requieren permisos de ubicación. Abre la app primero.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Abrir la aplicación principal para solicitar permisos
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                    }
                }
            }
            ACTION_NAVIGATE -> {
                val parkingLocation = parkingPreferences.getParkingLocation()

                if (parkingLocation != null) {
                    val (latitude, longitude) = parkingLocation

                    // Intentar abrir con Google Maps primero
                    val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(mapIntent)
                    } else {
                        // Fallback a cualquier app de mapas
                        val genericMapIntent = Intent(Intent.ACTION_VIEW,
                            Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude"))
                        genericMapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(genericMapIntent)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "No hay ubicación guardada. Guarda la ubicación primero.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val ACTION_SAVE_LOCATION = "com.example.parkar.widget.ACTION_SAVE_LOCATION"
        const val ACTION_NAVIGATE = "com.example.parkar.widget.ACTION_NAVIGATE"
    }
}