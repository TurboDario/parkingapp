package com.example.parkar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState // <-- Importante importar MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.parkar.R
import com.example.parkar.ui.components.AppDrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSaveParkingLocation: () -> Unit,
    onNavigateToCar: () -> Unit,
    onManualLocationClick: () -> Unit = {},
    onOption1Click: () -> Unit = {},
    onOption2Click: () -> Unit = {},
    // **NUEVOS PARÁMETROS PARA GESTIONAR EL TEMA**
    themeState: MutableState<Boolean>,
    onThemeChange: (Boolean) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                onOption1Click = onOption1Click,
                onOption2Click = onOption2Click,
                // **PASAMOS EL ESTADO Y LA FUNCIÓN DE CAMBIO DE TEMA AL APP DRAWER CONTENT**
                themeState = themeState,
                onThemeChange = onThemeChange
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "ParKar",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.shadow(elevation = 8.dp)
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Contenedor Box para Botón ParKar con funcionalidad Editar integrada
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                    ) {
                        // Botón 1: Guardar aparcamiento (ParKar)
                        Button(
                            onClick = onSaveParkingLocation,
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_car),
                                    contentDescription = "ParKar",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ParKar",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // IconButton:  Icono de Editar en la ESQUINA INFERIOR DERECHA - ACCIÓN SECUNDARIA
                        IconButton(
                            onClick = onManualLocationClick,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                                .align(Alignment.BottomEnd),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit_location),
                                contentDescription = "Editar ParKar",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Botón 3: Navegar al coche (cuadrado grande)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToCar,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_navigate),
                                contentDescription = "Llevarme al coche",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Llevarme al coche",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
    }
}