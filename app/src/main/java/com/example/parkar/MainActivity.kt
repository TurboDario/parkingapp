package com.turbomonguerdev.parkar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.turbomonguerdev.parkar.location.LocationManager
import com.turbomonguerdev.parkar.ui.screens.AboutScreen
import com.turbomonguerdev.parkar.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private var currentLocation by mutableStateOf<LatLng?>(null)
    private var currentScreen by mutableStateOf(Screen.HOME)

    enum class Screen {
        HOME, MANUAL_LOCATION, ABOUT
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            locationManager.initLocationClient()
            updateCurrentLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = LocationManager(this)
        checkLocationPermissions()
        enableEdgeToEdge()

        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            val themeState = remember { mutableStateOf(systemDarkTheme) }

            ParKarTheme(darkTheme = themeState.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationHandler(
                        currentScreen = currentScreen,
                        locationManager = locationManager,
                        themeState = themeState,
                        onSaveLocation = { saveLocation(it) },
                        onScreenChange = { currentScreen = it }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NavigationHandler(
        currentScreen: Screen,
        locationManager: LocationManager,
        themeState: MutableState<Boolean>,
        onSaveLocation: (LatLng) -> Unit,
        onScreenChange: (Screen) -> Unit
    ) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                onSaveParkingLocation = { locationManager.saveParkingLocation() },
                onNavigateToCar = { locationManager.navigateToParkingLocation() },
                onManualLocationClick = { onScreenChange(Screen.MANUAL_LOCATION) },
                themeState = themeState,
                onThemeChange = { themeState.value = it },
                onAboutClick = { onScreenChange(Screen.ABOUT) },
                onShareLocation = {
                    locationManager.shareCurrentLocation()
                },
            )
            Screen.MANUAL_LOCATION -> ManualLocationScreen(
                initialLocation = currentLocation,
                onSaveManualLocation = { onSaveLocation(it) },
                onCancel = { onScreenChange(Screen.HOME) }
            )
            Screen.ABOUT -> AboutScreen(
                onBackClick = { onScreenChange(Screen.HOME) }
            )
        }
    }

    private fun checkLocationPermissions() {
        val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocation != PackageManager.PERMISSION_GRANTED ||
            hasCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            locationManager.initLocationClient()
            updateCurrentLocation()
        }
    }

    private fun updateCurrentLocation() {
        currentLocation = locationManager.getCurrentLocation()
    }

    private fun saveLocation(latLng: LatLng) {
        currentLocation = latLng
        locationManager.saveParkingLocation(latLng)
        currentScreen = Screen.HOME
    }
}
