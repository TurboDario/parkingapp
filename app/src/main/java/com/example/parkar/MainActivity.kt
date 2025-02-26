package com.example.parkar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.example.parkar.location.LocationManager
import com.example.parkar.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager

    // Registro para solicitar permisos de ubicación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (locationGranted) {
            locationManager.initLocationClient()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Inicializar el administrador de ubicación
        locationManager = LocationManager(this)

        // Verificar permisos de ubicación
        checkLocationPermissions()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onSaveParkingLocation = { locationManager.saveParkingLocation() },
                        onNavigateToCar = { locationManager.navigateToParkingLocation() }
                    )
                }
            }
        }
    }

    private fun checkLocationPermissions() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            locationManager.initLocationClient()
        }
    }
}