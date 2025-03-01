package com.turbomonguerdev.parkar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turbomonguerdev.parkar.R

@Composable
fun AppDrawerContent(
    themeState: MutableState<Boolean>,
    onThemeChange: (Boolean) -> Unit,
    onAboutClick: () -> Unit
) {
    // We calculate a width so the drawer is half the screen, for example
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
                    // The switch controls the dark theme
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
