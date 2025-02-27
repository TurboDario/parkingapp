package com.turbodev.parkar.location

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng

class ParkingPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    fun saveParkingLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit {
            putFloat(KEY_LATITUDE, latitude.toFloat())
            putFloat(KEY_LONGITUDE, longitude.toFloat())
        }
    }

    fun getParkingLocation(): LatLng? {
        val latitude = sharedPreferences.getFloat(KEY_LATITUDE, DEFAULT_COORDINATE)
        val longitude = sharedPreferences.getFloat(KEY_LONGITUDE, DEFAULT_COORDINATE)

        return if (latitude != DEFAULT_COORDINATE || longitude != DEFAULT_COORDINATE) {
            LatLng(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "parking_preferences"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val DEFAULT_COORDINATE = 0.0f
    }
}