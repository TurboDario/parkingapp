package com.turbomonguerdev.parkar.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.turbomonguerdev.parkar.R
import com.turbomonguerdev.parkar.ui.components.AppDrawerContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSaveParkingLocation: () -> Unit,
    onNavigateToCar: () -> Unit,
    onShareLocation: () -> Unit,
    onManualLocationClick: () -> Unit,
    themeState: MutableState<Boolean>,
    onThemeChange: (Boolean) -> Unit,
    onAboutClick: () -> Unit,
    currentLanguage: String,
    supportedLanguages: List<Pair<String, String>>,
    onLanguageChange: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                themeState = themeState,
                onThemeChange = onThemeChange,
                onSelectLanguageClick = { showLanguageDialog = true },
                onAboutClick = onAboutClick
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.menu)
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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ParkingButton(
                            onClick = onSaveParkingLocation,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.6f)
                                .aspectRatio(1f)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SmallActionButton(
                                onClick = onShareLocation,
                                iconRes = R.drawable.ic_share_location,
                                contentDescription = stringResource(R.string.share_location)
                            )
                            SmallActionButton(
                                onClick = onManualLocationClick,
                                iconRes = R.drawable.ic_edit_location,
                                contentDescription = stringResource(R.string.edit_parking)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(end = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PhotoButton()
                            PlaceholderButton()
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    NavigateButton(onClick = onNavigateToCar)
                }
                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        containerColor = MaterialTheme.colorScheme.surface,
                        title = { Text(text = stringResource(R.string.select_language), style = MaterialTheme.typography.titleLarge) },
                        text = {
                            Column {
                                supportedLanguages.forEach { (code, name) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { selectedLanguage = code }
                                    ) {
                                        RadioButton(
                                            selected = code == selectedLanguage,
                                            onClick = { selectedLanguage = code }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = name, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                onLanguageChange(selectedLanguage)
                                showLanguageDialog = false
                            }) { Text(text = stringResource(R.string.ok), style = MaterialTheme.typography.bodyLarge) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text(text = stringResource(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun ParkingButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_car),
                contentDescription = stringResource(R.string.save_parking_location),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.save_parking),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SmallActionButton(onClick: () -> Unit, iconRes: Int, contentDescription: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(52.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun NavigateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .aspectRatio(1f)
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_navigate),
                contentDescription = stringResource(R.string.navigate_to_car),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.navigate_to_car),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PhotoButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val photoFileName = "parking_photo.jpg"
    val photoFile = File(context.filesDir, photoFileName)
    var photo by remember { mutableStateOf<ImageBitmap?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (photoFile.exists()) {
            val loadedBitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeFile(photoFile.absolutePath)
            }
            if (loadedBitmap != null) {
                photo = loadedBitmap.asImageBitmap()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    FileOutputStream(photoFile).use { fos ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                    }
                    withContext(Dispatchers.Main) {
                        photo = bitmap.asImageBitmap()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Button(
        onClick = {
            if (photo != null) showPhotoDialog = true else launcher.launch(null)
        },
        modifier = modifier
            .size(52.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = stringResource(R.string.take_photo),
            modifier = Modifier.size(28.dp)
        )
    }
    if (showPhotoDialog && photo != null) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(text = stringResource(R.string.photo), style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = photo!!,
                        contentDescription = stringResource(R.string.saved_photo),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(onClick = { launcher.launch(null) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = stringResource(R.string.retake_photo),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoDialog = false }) {
                    Text(text = stringResource(R.string.close), style = MaterialTheme.typography.bodyLarge)
                }
            }
        )
    }
}

@Composable
fun PlaceholderButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("parking_info", Context.MODE_PRIVATE) }
    var showPlaceholderDialog by remember { mutableStateOf(false) }
    var parkingLabelState by remember {
        mutableStateOf(sharedPrefs.getString("parking_label", "") ?: "")
    }
    var parkingDescriptionState by remember {
        mutableStateOf(sharedPrefs.getString("parking_description", "") ?: "")
    }

    Button(
        onClick = {
            parkingLabelState = sharedPrefs.getString("parking_label", "") ?: ""
            parkingDescriptionState = sharedPrefs.getString("parking_description", "") ?: ""
            showPlaceholderDialog = true
        },
        modifier = modifier
            .size(52.dp)
            .shadow(8.dp, shape = MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_parking_label),
            contentDescription = stringResource(R.string.parking_label),
            modifier = Modifier.size(28.dp)
        )
    }
    if (showPlaceholderDialog) {
        var currentLabel by remember { mutableStateOf(parkingLabelState) }
        var currentDescription by remember { mutableStateOf(parkingDescriptionState) }

        AlertDialog(
            onDismissRequest = { showPlaceholderDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(text = stringResource(R.string.enter_parking_info), style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentLabel,
                        onValueChange = { currentLabel = it },
                        label = { Text(text = stringResource(R.string.parking_label)) },
                        textStyle = MaterialTheme.typography.headlineMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentDescription,
                        onValueChange = { currentDescription = it },
                        label = { Text(text = stringResource(R.string.parking_description)) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    sharedPrefs.edit().apply {
                        putString("parking_label", currentLabel)
                        putString("parking_description", currentDescription)
                        apply()
                    }
                    parkingLabelState = currentLabel
                    parkingDescriptionState = currentDescription
                    showPlaceholderDialog = false
                }) {
                    Text(text = stringResource(R.string.save), style = MaterialTheme.typography.bodyLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPlaceholderDialog = false }) {
                    Text(text = stringResource(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
                }
            }
        )
    }
}