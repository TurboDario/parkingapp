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

// 🎨 Esquema de colores para el modo claro
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),  // Azul profundo pero cálido
    onPrimary = Color.White,  // Texto en blanco sobre primary
    secondary = Color(0xFF00ACC1),  // Azul verdoso relajante
    onSecondary = Color.White,  // Texto en blanco sobre secondary
    background = Color(0xFFE3F2FD),  // Azul clarito, relajante y confiable
    surface = Color(0xFFBBDEFB),  // Azul pastel para destacar secciones
    onBackground = Color(0xFF0D47A1),  // Azul oscuro para textos contrastados
    onSurface = Color(0xFF102027)  // Azul grisáceo, elegante y profesional
)

// 🌙 Esquema de colores para el modo oscuro
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0D47A1),  // Azul marino profundo
    onPrimary = Color.White,  // Texto en blanco sobre primary
    secondary = Color(0xFF00838F),  // Verde azulado oscuro, transmite calma
    onSecondary = Color.White,  // Texto en blanco sobre secondary
    background = Color(0xFF0A192F),  // Azul oscuro grisáceo, relajante
    surface = Color(0xFF1B2A41),  // Azul grisáceo más suave para destacar secciones
    onBackground = Color(0xFFBBDEFB),  // Azul claro para contraste
    onSurface = Color(0xFF90CAF9)  // Azul pastel, elegante y profesional
)
