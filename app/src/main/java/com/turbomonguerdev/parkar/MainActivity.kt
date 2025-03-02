@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.turbomonguerdev.parkar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
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
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var currentLanguage by mutableStateOf("system")
    private val supportedLanguages = listOf(
        "system" to "System default",
        "en" to "English",
        "es" to "Spanish",
        "fr" to "French",
        "de" to "German",
        "it" to "Italian",
        "ja" to "Japanese",
        "ru" to "Russian"
    )
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
        val langPrefs = getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)
        currentLanguage = langPrefs.getString("app_lang", "system") ?: "system"
        // Update the locale with the saved language
        updateLocale(currentLanguage)

        val sharedPrefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isSystemDarkTheme = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
        val savedDarkTheme = sharedPrefs.getBoolean("dark_theme", isSystemDarkTheme)

        if (savedDarkTheme) {
            setTheme(R.style.Theme_ParKar_Splash_Dark)
        } else {
            setTheme(R.style.Theme_ParKar_Splash_Light)
        }

        installSplashScreen()
        super.onCreate(savedInstanceState)

        locationManager = LocationManager(this)
        checkLocationPermissions()
        enableEdgeToEdge()

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
                        onScreenChange = { currentScreen = it },
                        currentLanguage = currentLanguage,
                        supportedLanguages = supportedLanguages,
                        onLanguageChange = { changeAppLanguage(it) }
                    )
                }
            }
        }
    }

    private fun changeAppLanguage(languageCode: String) {
        currentLanguage = languageCode
        getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("app_lang", languageCode)
            .apply()
        updateLocale(languageCode)
        recreate()
    }

    private fun updateLocale(languageCode: String) {
        val locale = if (languageCode == "system") {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                resources.configuration.locales.get(0)
            } else {
                resources.configuration.locale
            }
        } else {
            Locale(languageCode)
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            createConfigurationContext(config)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @Composable
    fun NavigationHandler(
        currentScreen: Screen,
        locationManager: LocationManager,
        themeState: MutableState<Boolean>,
        onThemeChange: (Boolean) -> Unit,
        onSaveLocation: (LatLng) -> Unit,
        onScreenChange: (Screen) -> Unit,
        currentLanguage: String,
        supportedLanguages: List<Pair<String, String>>,
        onLanguageChange: (String) -> Unit
    ) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                onSaveParkingLocation = { locationManager.saveParkingLocation() },
                onNavigateToCar = { locationManager.navigateToParkingLocation() },
                onManualLocationClick = { onScreenChange(Screen.MANUAL_LOCATION) },
                onShareLocation = { locationManager.shareCurrentLocation() },
                themeState = themeState,
                onThemeChange = onThemeChange,
                onAboutClick = { onScreenChange(Screen.ABOUT) },
                currentLanguage = currentLanguage,
                supportedLanguages = supportedLanguages,
                onLanguageChange = onLanguageChange
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
