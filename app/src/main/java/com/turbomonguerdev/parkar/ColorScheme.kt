package com.turbomonguerdev.parkar

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ParKarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
// Define un ColorScheme para el modo claro
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3), // Azul
    onPrimary = Color(0xFFFFFFFF), // Blanco
    secondary = Color(0xFFB2EBF2), // Cyan claro
    onSecondary = Color(0xFF000000), // Negro
    background = Color(0xFFFFFFFF), // Blanco
    surface = Color(0xFFF0F0F0), // Gris claro
    onBackground = Color(0xFF333333), // Gris oscuro
    onSurface = Color(0xFF333333) // Gris oscuro
)

// Define un ColorScheme para el modo oscuro
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3F51B5), // Azul oscuro
    onPrimary = Color(0xFFFFFFFF), // Blanco
    secondary = Color(0xFF009688), // Verde azulado
    onSecondary = Color(0xFFFFFFFF), // Blanco
    background = Color(0xFF121212), // Negro casi puro
    surface = Color(0xFF1E1E1E), // Gris muy oscuro
    onBackground = Color(0xFFEEEEEE), // Gris claro
    onSurface = Color(0xFFEEEEEE) // Gris claro
)