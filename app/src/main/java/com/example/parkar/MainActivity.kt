package com.turbodev.parkar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.turbodev.parkar.location.LocationManager
import com.turbodev.parkar.ui.screens.HomeScreen
import com.turbodev.parkar.ui.screens.ManualLocationScreen

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
        // Loguea la API key en onCreate()
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
            Log.d("MAPS_API_KEY", "Key en runtime: $apiKey")
        } catch (e: Exception) {
            Log.e("MAPS_API_KEY", "Error al obtener la API key", e)
        }

        // Inicializar el administrador de ubicación
        locationManager = LocationManager(this)

        // Verificar permisos de ubicación
        checkLocationPermissions()

        enableEdgeToEdge() // <- Añade esta línea para habilitar edge-to-edge

        setContent {
            // **EVALUATE isSystemInDarkTheme OUTSIDE remember**
            val systemDarkTheme = isSystemInDarkTheme() // <-- EVALUATE HERE
            // **ESTADO PARA GESTIONAR EL TEMA (CLARO/OSCURO)**
            val themeState = remember { mutableStateOf(systemDarkTheme) } // <-- PASS THE RESULT TO mutableStateOf

            ParKarTheme(darkTheme = themeState.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onSaveParkingLocation = { locationManager.saveParkingLocation() },
                        onNavigateToCar = { locationManager.navigateToParkingLocation() },
                        // **IMPLEMENTACIÓN DE onManualLocationClick - ¡IMPORTANTE!**
                        onManualLocationClick = {
                            // **CUANDO SE HAGA CLIC EN "EDITAR", NAVEGAMOS A ManualLocationScreen**
                            setContent { // **¡¡¡USAMOS setContent DE NUEVO!!!  (Esto es una forma SIMPLE de navegar entre "pantallas" en Compose en este ejemplo básico)**
                                ManualLocationScreen(
                                    onSaveManualLocation = {
                                        locationManager.saveParkingLocation() // **PASAMOS onSaveParkingLocation A ManualLocationScreen**
                                        // **DESPUÉS DE "GUARDAR" EN ManualLocationScreen, VOLVEMOS A HomeScreen (de nuevo, simple para este ejemplo)**
                                        setContent { // **VOLVEMOS A setContent PARA REGRESAR A HomeScreen**
                                            HomeScreen(
                                                onSaveParkingLocation = { locationManager.saveParkingLocation() },
                                                onNavigateToCar = { locationManager.navigateToParkingLocation() },
                                                themeState = themeState,
                                                onThemeChange = { isDark -> themeState.value = isDark }
                                            )
                                        }
                                    }
                                )
                            }
                        },
                        themeState = themeState,
                        onThemeChange = { isDark -> themeState.value = isDark }
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