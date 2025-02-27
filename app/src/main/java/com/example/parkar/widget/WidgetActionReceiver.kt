package com.turbodev.parkar.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.turbodev.parkar.R
import com.turbodev.parkar.location.ParkingPreferences // Import corrected package
import com.turbodev.parkar.service.LocationForegroundService

class WidgetActionReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val parkingPreferences = ParkingPreferences(context) // Use the corrected ParkingPreferences class
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(context.packageName, ParKarWidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        when (intent.action) {
            ACTION_SAVE_LOCATION -> {
                // Actualiza el botón a estado "processing"
                updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.PROCESSING)

                // Verifica si tenemos permisos de localización
                if (hasLocationPermission(context)) {
                    // Inicia el Foreground Service que se encargará de obtener y guardar la ubicación
                    val serviceIntent = Intent(context, LocationForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                    // Nota: El servicio se encargará de actualizar la UI (por ejemplo, cambiando a SUCCESS o ERROR)
                } else {
                    Toast.makeText(
                        context,
                        "Se requieren permisos de ubicación. Abre la app primero.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Actualiza el botón a estado "error"
                    updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.ERROR)
                    // Vuelve al estado normal después de 2 segundos
                    Handler(Looper.getMainLooper()).postDelayed({
                        updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.NORMAL)
                    }, 2000)

                    // Abre la aplicación principal para solicitar permisos
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    launchIntent?.let { context.startActivity(it) }
                }
            }
            ACTION_NAVIGATE -> {
                val parkingLocation = parkingPreferences.getParkingLocation()

                if (parkingLocation != null) {
                    val (latitude, longitude) = parkingLocation.latitude to parkingLocation.longitude // Adapt to LatLng return from getParkingLocation() if needed, but not needed as getParkingLocation() returns LatLng now. Just destructuring the LatLng object directly is fine.
                    // val latitude = parkingLocation.latitude // If getParkingLocation returns Pair<Double, Double>
                    // val longitude = parkingLocation.longitude // If getParkingLocation returns Pair<Double, Double>

                    // Intenta abrir con Google Maps primero
                    val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(mapIntent)
                    } else {
                        // Fallback a cualquier app de mapas
                        val genericMapIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
                        )
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

    private fun updateSaveButtonState(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        state: ButtonState
    ) {
        val views = RemoteViews(context.packageName, R.layout.parkar_widget)

        when (state) {
            ButtonState.NORMAL -> views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_normal)
            ButtonState.PROCESSING -> views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_processing)
            ButtonState.SUCCESS -> views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_success)
            ButtonState.ERROR -> views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_error)
        }

        // Actualiza todas las instancias del widget
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
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
        const val ACTION_SAVE_LOCATION = "com.turbodev.parkar.widget.ACTION_SAVE_LOCATION"
        const val ACTION_NAVIGATE = "com.turbodev.parkar.widget.ACTION_NAVIGATE"
    }
}