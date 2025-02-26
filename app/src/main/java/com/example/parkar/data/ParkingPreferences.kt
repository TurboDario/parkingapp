package com.turbodev.parkar.data

import android.content.Context
import android.content.SharedPreferences

class ParkingPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    /**
     * Guarda la ubicación del aparcamiento en SharedPreferences
     */
    fun saveParkingLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit()
            .putFloat(KEY_LATITUDE, latitude.toFloat())
            .putFloat(KEY_LONGITUDE, longitude.toFloat())
            .apply()
    }

    /**
     * Obtiene la ubicación del aparcamiento guardada
     * @return Un par de valores (latitud, longitud) o null si no hay ubicación guardada
     */
    fun getParkingLocation(): Pair<Double, Double>? {
        val latitude = sharedPreferences.getFloat(KEY_LATITUDE, NO_LOCATION)
        val longitude = sharedPreferences.getFloat(KEY_LONGITUDE, NO_LOCATION)

        return if (latitude != NO_LOCATION && longitude != NO_LOCATION) {
            Pair(latitude.toDouble(), longitude.toDouble())
        } else {
            null
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "parking_preferences"
        private const val KEY_LATITUDE = "parking_latitude"
        private const val KEY_LONGITUDE = "parking_longitude"
        private const val NO_LOCATION = Float.MIN_VALUE
    }
}