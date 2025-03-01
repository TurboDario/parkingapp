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

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    secondary = Color(0xFF00ACC1),
    onSecondary = Color.White,
    background = Color(0xFFE3F2FD),
    surface = Color(0xFFBBDEFB),
    onBackground = Color(0xFF0D47A1),
    onSurface = Color(0xFF102027)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0D47A1),
    onPrimary = Color.White,
    secondary = Color(0xFF00838F),
    onSecondary = Color.White,
    background = Color(0xFF0A192F),
    surface = Color(0xFF1B2A41),
    onBackground = Color(0xFFBBDEFB),
    onSurface = Color(0xFF90CAF9)
)
