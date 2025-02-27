package com.turbodev.parkar.location

import android.content.Context
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng

class ParkingPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("parking_preferences", Context.MODE_PRIVATE)

    fun saveParkingLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit {
            putFloat("latitude", latitude.toFloat())
            putFloat("longitude", longitude.toFloat())
        }
    }

    fun getParkingLocation(): LatLng? {
        val latitude = sharedPreferences.getFloat("latitude", 0.0f)
        val longitude = sharedPreferences.getFloat("longitude", 0.0f)

        return if (latitude != 0.0f || longitude != 0.0f) {
            LatLng(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }
}