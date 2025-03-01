@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.turbomonguerdev.parkar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.maps.model.LatLng
import com.turbomonguerdev.parkar.location.LocationManager
import com.turbomonguerdev.parkar.screens.ManualLocationScreen
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
        // Read user preference (or system default) for dark theme
        val sharedPrefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val isSystemDarkTheme = (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES)
        val savedDarkTheme = sharedPrefs.getBoolean("dark_theme", isSystemDarkTheme)

        // Choose the splash theme (light/dark) before super.onCreate
        if (savedDarkTheme) {
            // Modo oscuro -> usa el splash definido en Theme.ParKar.Splash.Dark
            setTheme(R.style.Theme_ParKar_Splash_Dark)
        } else {
            // Modo claro -> usa el splash definido en Theme.ParKar.Splash.Light
            setTheme(R.style.Theme_ParKar_Splash_Light)
        }

        // Install the new splash screen API
        val splashScreen: androidx.core.splashscreen.SplashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Initialize location manager, check permissions, etc.
        locationManager = LocationManager(this)
        checkLocationPermissions()
        enableEdgeToEdge()

        // We'll use this state to toggle the theme in Composables
        val mainThemeState = mutableStateOf(savedDarkTheme)

        setContent {
            ParKarTheme(darkTheme = mainThemeState.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    NavigationHandler(
                        currentScreen = currentScreen,
                        locationManager = locationManager,
                        themeState = mainThemeState,
                        onThemeChange = { newValue ->
                            mainThemeState.value = newValue
                            sharedPrefs.edit().putBoolean("dark_theme", newValue).apply()
                        },
                        onSaveLocation = { saveLocation(it) },
                        onScreenChange = { currentScreen = it }
                    )
                }
            }
        }
    }

    @Composable
    fun NavigationHandler(
        currentScreen: Screen,
        locationManager: LocationManager,
        themeState: MutableState<Boolean>,
        onThemeChange: (Boolean) -> Unit,
        onSaveLocation: (LatLng) -> Unit,
        onScreenChange: (Screen) -> Unit
    ) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                onSaveParkingLocation = { locationManager.saveParkingLocation() },
                onNavigateToCar = { locationManager.navigateToParkingLocation() },
                onManualLocationClick = { onScreenChange(Screen.MANUAL_LOCATION) },
                onShareLocation = { locationManager.shareCurrentLocation() },
                themeState = themeState,
                onThemeChange = onThemeChange,
                onAboutClick = { onScreenChange(Screen.ABOUT) }
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
            hasCoarseLocation != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
