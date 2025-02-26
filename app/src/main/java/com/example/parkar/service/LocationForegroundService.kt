package com.example.parkar.service

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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.parkar.R
import com.example.parkar.data.ParkingPreferences
import com.example.parkar.widget.ParKarWidgetProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Inicia el servicio en primer plano con una notificación silenciosa
        startForeground(1, createNotification())
        getLocationAndSave()
        return START_NOT_STICKY
    }

    private fun createNotification() = NotificationCompat.Builder(this, "location_channel").apply {
        setContentTitle("Obteniendo ubicación")
        setContentText("Por favor, espera...")
        setSmallIcon(R.drawable.ic_navigate)  // Usa un ícono discreto
        setPriority(NotificationCompat.PRIORITY_LOW)
        setSound(null)
        setVibrate(longArrayOf(0L))
    }.build().also { notification ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Servicio de ubicación",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndSave() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                val appWidgetManager = AppWidgetManager.getInstance(this)
                // Usa 'this' en lugar de 'packageName'
                val thisAppWidget = ComponentName(this, ParKarWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
                val views = RemoteViews(packageName, R.layout.parkar_widget)

                if (location != null) {
                    val parkingPreferences = ParkingPreferences(this)
                    parkingPreferences.saveParkingLocation(location.latitude, location.longitude)
                    // Actualiza a estado SUCCESS (fondo verde)
                    views.setInt(
                        R.id.widget_btn_save,
                        "setBackgroundResource",
                        R.drawable.save_button_success
                    )
                } else {
                    // En caso de fallo, actualiza a estado ERROR
                    views.setInt(
                        R.id.widget_btn_save,
                        "setBackgroundResource",
                        R.drawable.save_button_error
                    )
                }
                // Actualiza el widget
                appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }

                // Reinicia a NORMAL después de 2 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    views.setInt(
                        R.id.widget_btn_save,
                        "setBackgroundResource",
                        R.drawable.save_button_normal
                    )
                    appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
                    stopSelf()
                }, 2000)
            }
            .addOnFailureListener { e ->
                val appWidgetManager = AppWidgetManager.getInstance(this)
                val thisAppWidget = ComponentName(this, ParKarWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
                val views = RemoteViews(packageName, R.layout.parkar_widget)
                views.setInt(
                    R.id.widget_btn_save,
                    "setBackgroundResource",
                    R.drawable.save_button_error
                )
                appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }

                Handler(Looper.getMainLooper()).postDelayed({
                    views.setInt(
                        R.id.widget_btn_save,
                        "setBackgroundResource",
                        R.drawable.save_button_normal
                    )
                    appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
                    stopSelf()
                }, 2000)
            }
    }



        override fun onBind(intent: Intent?): IBinder? = null
}
