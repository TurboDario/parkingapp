package com.example.parkar.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.parkar.R
import com.example.parkar.data.ParkingPreferences
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class WidgetActionReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val parkingPreferences = ParkingPreferences(context)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(context.packageName, ParKarWidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        when (intent.action) {
            ACTION_SAVE_LOCATION -> {
                // Set button to "processing" state
                updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.PROCESSING)

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
                                updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.SUCCESS)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.NORMAL)
                                }, 2000)
                            } else {
                                Log.e("WidgetActionReceiver", "La ubicación es null")
                                Toast.makeText(
                                    context,
                                    "No se pudo obtener la ubicación actual (location null)",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.ERROR)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.NORMAL)
                                }, 2000)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("WidgetActionReceiver", "Error al obtener la ubicación", e)
                            Toast.makeText(
                                context,
                                "Error al guardar ubicación: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.ERROR)
                            Handler(Looper.getMainLooper()).postDelayed({
                                updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.NORMAL)
                            }, 2000)
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Se requieren permisos de ubicación. Abre la app primero.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Set button to "error" state
                    updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.ERROR)

                    // Reset button state after 2 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.NORMAL)
                    }, 2000)

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

    private fun updateSaveButtonState(context: Context, appWidgetManager: AppWidgetManager,
                                      appWidgetIds: IntArray, state: ButtonState) {
        val views = RemoteViews(context.packageName, R.layout.parkar_widget)

        when (state) {
            ButtonState.NORMAL -> {
                views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_normal)
            }
            ButtonState.PROCESSING -> {
                views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_processing)
            }
            ButtonState.SUCCESS -> {
                views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_success)
            }
            ButtonState.ERROR -> {
                views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_error)
            }
        }

        // Update all instances of the widget
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
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

    enum class ButtonState {
        NORMAL, PROCESSING, SUCCESS, ERROR
    }

    companion object {
        const val ACTION_SAVE_LOCATION = "com.example.parkar.widget.ACTION_SAVE_LOCATION"
        const val ACTION_NAVIGATE = "com.example.parkar.widget.ACTION_NAVIGATE"
    }
}