package com.example.hopla

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

// A search bar with a icon and a text field
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text(text = stringResource(R.string.search)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

// An button (used for add) that takes navigation in as a parameter
@Composable
fun AddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

// A header for the screen with a back button to navigate to previous page and a title
@Composable
fun ScreenHeader(navController: NavController, headerText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.tertiary)
            .border(10.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = headerText,
                fontSize = 24.sp
            )
        }
    }
}

// A composable that allows the user to pick an image from the camera or gallery through a dialog
@Composable
fun ImagePicker(
    onImageSelected: (Bitmap?) -> Unit,
    text: String
) {
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val permissionGranted = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Camera launcher
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            imageBitmap.value = bitmap
            onImageSelected(bitmap)
        }

    // Content picker launcher
    val contentPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                imageBitmap.value = BitmapFactory.decodeStream(inputStream)
                onImageSelected(imageBitmap.value)
            }
        }

    // Permission launcher
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionGranted.value = isGranted
            if (isGranted) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Check and request for camera permission
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted.value = true
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Text to trigger camera action
    Text(
        text = text,
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable { showDialog.value = true },
        style = TextStyle(textDecoration = TextDecoration.Underline)
    )

    // Dialog to confirm taking a picture
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.change_profile_picture)) },
            text = { Text(text = stringResource(R.string.profile_pic_description)) },
            confirmButton = {
                Column {
                    Button(onClick = {
                        if (permissionGranted.value) {
                            cameraLauncher.launch(null)
                        }
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.take_a_picture))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        contentPickerLauncher.launch("image/*")
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.choose_from_library))
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}