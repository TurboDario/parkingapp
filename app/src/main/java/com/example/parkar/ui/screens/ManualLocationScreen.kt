package com.turbodev.parkar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ManualLocationScreen(
    initialLocation: LatLng?,
    onSaveManualLocation: (LatLng) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf(initialLocation) }

    val cameraPositionState = rememberCameraPositionState()
    val uiSettings = remember { MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true) }
    val properties = remember { MapProperties(isMyLocationEnabled = true) }

    // Cargar ubicación inicial y centrar la cámara
    LaunchedEffect(Unit) {
        if (selectedLocation == null) {
            fetchLastKnownLocation(context) { location ->
                location?.let {
                    selectedLocation = it
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 17f)
                }
            }
        } else {
            selectedLocation?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 17f)
            }
        }
    }

    // Actualizar selectedLocation cuando la cámara se mueva y quede inactiva
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            selectedLocation = cameraPositionState.position.target
        }
    }

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                )
                // Icono central fijo
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_pin),
                    contentDescription = "Ubicación central",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 48.dp)
                        .size(69.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { selectedLocation?.let(onSaveManualLocation) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedLocation != null
                ) {
                    Text("Guardar Ubicación Seleccionada")
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

private fun fetchLastKnownLocation(context: Context, callback: (LatLng?) -> Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {

        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    callback(LatLng(it.latitude, it.longitude))
                } ?: run {
                    Log.d("ManualLocation", "No previous location available")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ManualLocation", "Error getting location: ${e.message}")
                callback(null)
            }
    } else {
        callback(null)
    }
}