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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// A search bar with a icon and a text field
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // A text field with a search icon
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
        singleLine = true,      // Only one line possibe
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
            .padding(8.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(45.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }

    }
}

// A header for the screen with a back button to navigate to previous page and a title
@Composable
fun ScreenHeader(navController: NavController, headerText: String) {
    // Box for the header with a border
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.primary)
            .border(10.dp, MaterialTheme.colorScheme.primary)
    ) {
        // Row for the back button and the title
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
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
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

// Standard for how a messaging board should look like
@Composable
fun MessageBox(
    messages: SnapshotStateList<Message>,
    newMessage: String,
    onMessageChange: (String) -> Unit,
    community: Community
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 4.dp) // Reduced padding
            ) {
                val groupedMessages = messages.groupBy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.timestamp)) }
                groupedMessages.forEach { (date, messagesForDate) ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp), // Reduced padding
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date,
                                fontSize = 12.sp
                            )
                        }
                    }
                    items(messagesForDate) { message ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = if (message.username == UserSession.alias) Alignment.End else Alignment.Start
                        ) {
                            // Timestamp and name above the message box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = if (message.username == UserSession.alias) Arrangement.End else Arrangement.Start
                            ) {
                                Text(
                                    text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(message.timestamp)),
                                    fontSize = 10.sp,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (message.username == UserSession.alias) stringResource(R.string.me) else message.username, // Change username to "me"
                                    fontSize = 10.sp,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            // Message box with rounded corners and different background color for user's messages
                            Card(
                                shape = RoundedCornerShape(8.dp), // Rounded corners
                                colors = CardDefaults.cardColors(
                                    containerColor = if (message.username == UserSession.alias)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.padding(8.dp) // Reduced padding
                            ) {
                                Text(
                                    text = message.content,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newMessage,
                    onValueChange = onMessageChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = stringResource(R.string.enter_you_message)) }
                )
                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            val newMsg = Message(
                                id = UUID.randomUUID().toString(),
                                content = newMessage,
                                timestamp = System.currentTimeMillis(),
                                username = UserSession.alias
                            )
                            messages.add(newMsg)
                            onMessageChange("")
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.publish))
                }
            }
        }
    }
}