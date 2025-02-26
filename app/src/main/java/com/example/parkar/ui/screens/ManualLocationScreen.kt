package com.turbodev.parkar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ManualLocationScreen(
    onSaveManualLocation: () -> Unit // Recibimos la función para guardar la ubicación (placeholder por ahora)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Seleccionar Ubicación Manual",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box( // Placeholder para el mapa - un simple cuadro gris
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Ocupa la mayor parte de la pantalla
                    .background(Color.LightGray) // Color gris claro para simular el mapa
            ) {
                Text(
                    text = "Mapa Placeholder - Aquí irá el mapa real",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = onSaveManualLocation // Al hacer clic, llamamos a la función para guardar (placeholder)
            ) {
                Text("Guardar Ubicación Manual Seleccionada")
            }
        }
    }
}