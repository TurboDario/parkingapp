package com.turbodev.parkar.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turbodev.parkar.BuildConfig
import com.turbodev.parkar.R
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit, onOpenDocument: (String, String) -> Unit) {
    val context = LocalContext.current

    val eulaTitle = stringResource(R.string.eula)
    val termsTitle = stringResource(R.string.terms_and_conditions)
    val privacyTitle = stringResource(R.string.privacy_policy)
    val contactTitle = stringResource(R.string.contact_support)
    val rateUsTitle = stringResource(R.string.rate_us)

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
            // Nombre de la App
            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)

            // Versión de la App
            Text(text = stringResource(R.string.version, BuildConfig.VERSION_NAME))

            // Desarrollador
            Text(text = stringResource(R.string.developer_name))

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Enlaces a documentos
            Text(
                text = eulaTitle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onOpenDocument(eulaTitle, readRawTextFile(context, R.raw.eulaparkar)) }
            )
            Text(
                text = termsTitle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onOpenDocument(termsTitle, readRawTextFile(context, R.raw.termsparkar)) }
            )
            Text(
                text = privacyTitle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onOpenDocument(privacyTitle, readRawTextFile(context, R.raw.privacyparkar)) }
            )

            Text(
                text = contactTitle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val emailIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                        data = android.net.Uri.parse("mailto:turbomonguer.dev@gmail.com")
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Support - ParKar")
                    }
                    context.startActivity(emailIntent)
                }
            )

            // Enlace a Google Play
            Text(
                text = rateUsTitle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val url = "https://play.google.com/store/apps/details?id=com.turbodev.parkar"
                    context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)))
                }
            )
        }
    }
}

fun readRawTextFile(context: Context, resId: Int): String {
    return context.resources.openRawResource(resId).bufferedReader().use(BufferedReader::readText)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(title: String, content: String, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}