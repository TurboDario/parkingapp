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
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.turbodev.parkar.R
import com.turbodev.parkar.location.ParkingPreferences
import com.turbodev.parkar.service.LocationForegroundService

class WidgetActionReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val parkingPreferences = ParkingPreferences(context)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(context.packageName, ParKarWidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        when (intent.action) {
            ACTION_SAVE_LOCATION -> {
                updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.PROCESSING)
                if (hasLocationPermission(context)) {
                    val serviceIntent = Intent(context, LocationForegroundService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    showToast(context, context.getString(R.string.location_permission_required))
                    updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.ERROR)
                    Handler(Looper.getMainLooper()).postDelayed({
                        updateSaveButtonState(context, appWidgetManager, appWidgetIds, ButtonState.NORMAL)
                    }, 2000)
                    context.packageManager.getLaunchIntentForPackage(context.packageName)?.let { context.startActivity(it) }
                }
            }
            ACTION_NAVIGATE -> {
                parkingPreferences.getParkingLocation()?.let { location ->
                    openNavigationApp(context, location.latitude, location.longitude)
                } ?: showToast(context, context.getString(R.string.no_saved_location))
            }
        }
    }

    private fun openNavigationApp(context: Context, latitude: Double, longitude: Double) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private fun updateSaveButtonState(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        state: ButtonState
    ) {
        val views = RemoteViews(context.packageName, R.layout.parkar_widget).apply {
            val resource = when (state) {
                ButtonState.NORMAL -> R.drawable.save_button_normal
                ButtonState.PROCESSING -> R.drawable.save_button_processing
                ButtonState.SUCCESS -> R.drawable.save_button_success
                ButtonState.ERROR -> R.drawable.save_button_error
            }
            setInt(R.id.widget_btn_save, "setBackgroundResource", resource)
        }
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    enum class ButtonState {
        NORMAL, PROCESSING, SUCCESS, ERROR
    }

    companion object {
        const val ACTION_SAVE_LOCATION = "com.turbodev.parkar.widget.ACTION_SAVE_LOCATION"
        const val ACTION_NAVIGATE = "com.turbodev.parkar.widget.ACTION_NAVIGATE"
    }
}