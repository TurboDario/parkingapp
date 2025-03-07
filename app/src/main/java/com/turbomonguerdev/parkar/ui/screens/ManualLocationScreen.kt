@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.turbomonguerdev.parkar.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.turbomonguerdev.parkar.R

@Composable
fun ManualLocationScreen(
    initialLocation: LatLng?,
    onSaveManualLocation: (LatLng) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    val cameraPositionState = rememberCameraPositionState()
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true
        )
    }
    val properties = remember {
        MapProperties(
            isMyLocationEnabled = true
        )
    }

    LaunchedEffect(selectedLocation) {
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

    LaunchedEffect(cameraPositionState.isMoving) {
        // When the map stops moving, update the selected location
        if (!cameraPositionState.isMoving) {
            selectedLocation = cameraPositionState.position.target
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = context.getString(R.string.select_manual_location),
                        style = MaterialTheme.typography.headlineSmall,
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_pin),
                    contentDescription = context.getString(R.string.central_location),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 48.dp)
                        .size(69.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { selectedLocation?.let(onSaveManualLocation) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedLocation != null
                ) {
                    Text(context.getString(R.string.save_selected_location))
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(context.getString(R.string.cancel))
                }
            }
        }
    }
}

private fun fetchLastKnownLocation(context: Context, callback: (LatLng?) -> Unit) {
    val permissionGranted = (
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            )
    if (permissionGranted) {
        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { location ->
                callback(location?.let { LatLng(it.latitude, it.longitude) })
            }
            .addOnFailureListener { e ->
                Log.e("ManualLocation", "Error getting location: ${e.message}", e)
                callback(null)
            }
    } else {
        callback(null)
    }
}
