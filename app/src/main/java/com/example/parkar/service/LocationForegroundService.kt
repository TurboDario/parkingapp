package com.turbomonguerdev.parkar.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.turbomonguerdev.parkar.R
import com.turbomonguerdev.parkar.widget.ParKarWidgetProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.turbomonguerdev.parkar.location.ParkingPreferences

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        getLocationAndSave()
        return START_NOT_STICKY
    }

    private fun createNotification() = NotificationCompat.Builder(this, "location_channel").apply {
        setContentTitle(getString(R.string.notification_title))
        setContentText(getString(R.string.notification_text))
        setSmallIcon(R.drawable.ic_navigate)
        setPriority(NotificationCompat.PRIORITY_LOW)
        setSound(null)
        setVibrate(longArrayOf(0L))
    }.build().also { notification ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                getString(R.string.location_service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndSave() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                updateWidget(location != null)
                location?.let {
                    ParkingPreferences(this).saveParkingLocation(it.latitude, it.longitude)
                }
                resetWidgetAfterDelay()
            }
            .addOnFailureListener { e ->
                Log.e("LocationForegroundService", "Error getting location", e)
                updateWidget(false)
                resetWidgetAfterDelay()
            }
    }

    private fun updateWidget(success: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisAppWidget = ComponentName(this, ParKarWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
        val views = RemoteViews(packageName, R.layout.parkar_widget)

        views.setInt(
            R.id.widget_btn_save,
            "setBackgroundResource",
            if (success) R.drawable.save_button_success else R.drawable.save_button_error
        )
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
    }

    private fun resetWidgetAfterDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val thisAppWidget = ComponentName(this, ParKarWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            val views = RemoteViews(packageName, R.layout.parkar_widget)

            views.setInt(R.id.widget_btn_save, "setBackgroundResource", R.drawable.save_button_normal)
            appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
            stopSelf()
        }, 2000)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
