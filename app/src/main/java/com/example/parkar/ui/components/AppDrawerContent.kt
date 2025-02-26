package com.example.parkar.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch // <-- Importante importar Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState // <-- Importante importar MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    onOption1Click: () -> Unit,
    onOption2Click: () -> Unit,
    // **NUEVOS PARÁMETROS PARA GESTIONAR EL TEMA**
    themeState: MutableState<Boolean>,
    onThemeChange: (Boolean) -> Unit,
    onManualLocationClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.5f

    ModalDrawerSheet(
        modifier = Modifier.width(drawerWidth)
    ) {
        Text(
            text = "Menú",
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Opción 1") },
            selected = false,
            onClick = onOption1Click,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("Opción 2") },
            selected = false,
            onClick = onOption2Click,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        HorizontalDivider() // Añadimos un Divider más para separar opciones del tema
        NavigationDrawerItem( // **NAVIGATION DRAWER ITEM MODIFICADO - SIN trailingContent**
            label = {
                Row( // **Usamos un Row como LABEL**
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Modo Oscuro")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = themeState.value,
                        onCheckedChange = { isChecked -> onThemeChange(isChecked) },
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
            },
            selected = false,
            onClick = { },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
    }
}