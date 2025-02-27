package com.turbodev.parkar.location

import android.content.Context
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng
import android.content.SharedPreferences

class ParkingPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    /**
     * Guarda la ubicación del aparcamiento en SharedPreferences
     */
    fun saveParkingLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit {
            putFloat(KEY_LATITUDE, latitude.toFloat())
            putFloat(KEY_LONGITUDE, longitude.toFloat())
        }
    }

    /**
     * Obtiene la ubicación del aparcamiento guardada
     * @return Un objeto LatLng que representa la ubicación (latitud, longitud) o null si no hay ubicación guardada
     */
    fun getParkingLocation(): LatLng? {
        val latitude = sharedPreferences.getFloat(KEY_LATITUDE, 0.0f)
        val longitude = sharedPreferences.getFloat(KEY_LONGITUDE, 0.0f)

        return if (latitude != 0.0f || longitude != 0.0f) {
            LatLng(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "parking_preferences"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }
}