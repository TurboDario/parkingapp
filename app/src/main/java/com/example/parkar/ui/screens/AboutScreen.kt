package com.turbodev.parkar.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turbodev.parkar.BuildConfig
import com.turbodev.parkar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    val eulaUrl = "https://example.com/eula"
    val termsUrl = "https://example.com/terms"
    val privacyUrl = "https://example.com/privacy"
    val supportEmail = "mailto:turbomonguer.dev@gmail.com"
    val playStoreUrl = "https://play.google.com/store/apps/details?id=com.turbodev.parkar"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Text(text = stringResource(R.string.version, BuildConfig.VERSION_NAME))
            Text(text = stringResource(R.string.developer_name))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            listOf(
                stringResource(R.string.eula) to eulaUrl,
                stringResource(R.string.terms_and_conditions) to termsUrl,
                stringResource(R.string.privacy_policy) to privacyUrl,
                stringResource(R.string.contact_support) to supportEmail,
                stringResource(R.string.rate_us) to playStoreUrl
            ).forEach { (label, url) ->
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { openWebLink(context, url) }
                )
            }
        }
    }
}

fun openWebLink(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
