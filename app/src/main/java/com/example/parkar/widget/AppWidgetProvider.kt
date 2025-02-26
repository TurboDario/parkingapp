package com.turbodev.parkar.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.turbodev.parkar.R

class ParKarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Actualiza cada instancia del widget
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        @SuppressLint("RemoteViewLayout")
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Construye la vista del widget usando RemoteViews
            val views = RemoteViews(context.packageName, R.layout.parkar_widget)

            // Configura el PendingIntent para el botón de guardar ubicación
            val saveIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_SAVE_LOCATION
            }
            val savePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                saveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_save, savePendingIntent)

            // Configura el PendingIntent para el botón de navegar al coche
            val navigateIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_NAVIGATE
            }
            val navigatePendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                navigateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_navigate, navigatePendingIntent)

            // Actualiza el widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}