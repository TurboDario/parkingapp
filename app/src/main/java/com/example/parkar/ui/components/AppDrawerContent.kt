package com.turbomonguerdev.parkar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turbomonguerdev.parkar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    themeState: MutableState<Boolean>,
    onThemeChange: (Boolean) -> Unit,
    onAboutClick: () -> Unit
) {
    val drawerWidth = LocalConfiguration.current.screenWidthDp.dp * 0.5f

    ModalDrawerSheet(
        modifier = Modifier.width(drawerWidth)
    ) {
        Text(
            text = stringResource(R.string.menu),
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        NavigationDrawerItem(
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.dark_mode))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = themeState.value,
                        onCheckedChange = { isChecked -> onThemeChange(isChecked) },
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
            },
            selected = false,
            onClick = {},
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.about)) },
            selected = false,
            onClick = onAboutClick,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
    }
}
