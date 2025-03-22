package com.example.hopla.universalData

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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.hopla.R
import com.example.hopla.apiService.changeEmail
import com.example.hopla.apiService.createUserReport
import com.example.hopla.profile.PasswordConfirmationDialog
import com.example.hopla.ui.theme.PrimaryGray
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.textFieldLabelTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.ui.theme.underlinedTextStyleSmall
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
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
        label = { Text(text = stringResource(R.string.search), style = textFieldLabelTextStyle) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        singleLine = true,      // Only one line possible
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
                style = headerTextStyleSmall
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
        style = underlinedTextStyleSmall
    )

    // Dialog to confirm taking a picture
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.change_profile_picture), style = underheaderTextStyle) },
            text = { Text(text = stringResource(R.string.profile_pic_description), style = generalTextStyle) },
            confirmButton = {
                Column {
                    Button(onClick = {
                        if (permissionGranted.value) {
                            cameraLauncher.launch(null)
                        }
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.take_a_picture), style = buttonTextStyle)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        contentPickerLauncher.launch("image/*")
                        showDialog.value = false
                    }) {
                        Text(text = stringResource(R.string.choose_from_library), style = buttonTextStyle)
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = stringResource(R.string.cancel), style = buttonTextStyle)
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
                                style = generalTextStyle
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
                                    style = generalTextStyle,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (message.username == UserSession.alias) stringResource(
                                        R.string.me
                                    ) else message.username, // Change username to "me"
                                    style = generalTextStyle,
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
                                    style = generalTextStyle,
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
                    placeholder = { Text(text = stringResource(R.string.enter_you_message), style = textFieldLabelTextStyle) }
                )
                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            val newMsg = Message(
                                id = UUID.randomUUID().toString(),
                                content = newMessage,
                                timestamp = System.currentTimeMillis(),
                                username = UserSession.alias?: ""
                            )
                            messages.add(newMsg)
                            onMessageChange("")
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.publish), style = buttonTextStyle)
                }
            }
        }
    }
}

// Report dialog
@Composable
fun ReportDialog(
    entityId: String,
    entityName: String,
    token: String,
    onDismiss: () -> Unit
) {
    var reportTitle by remember { mutableStateOf("") }
    var reportText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.send_a_report), style = underheaderTextStyle) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Set a fixed height for the report box
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Column {
                            androidx.compose.material.TextField(
                                value = reportTitle,
                                onValueChange = { reportTitle = it },
                                label = { Text(text = stringResource(R.string.title), style = textFieldLabelTextStyle) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material.TextField(
                                value = reportText,
                                onValueChange = { reportText = it },
                                label = { Text(text = stringResource(R.string.report), style = textFieldLabelTextStyle) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Set a fixed height for the text field
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                coroutineScope.launch {
                    val reportRequest = UserReportRequest(
                        EntityId = entityId,
                        EntityName = entityName,
                        Category = reportTitle,
                        Message = reportText
                    )
                    createUserReport(token, reportRequest)
                    onDismiss()
                }
            }) {
                Text(text = stringResource(R.string.send), style = buttonTextStyle)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), style = buttonTextStyle)
            }
        }
    )
}

// FRIENDS, REQUEST button in users profile
@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .width(100.dp),
        shape = RectangleShape
    ) {
        Text(text = text, style = buttonTextStyle)
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

//--------------------- For dropdown menue for date --------------------------------------
@Composable
fun DateOfBirthPicker(
    selectedDay: Int,
    selectedMonth: Int,
    selectedYear: Int,
    onDateSelected: (Int, Int, Int) -> Unit,
    onSave: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val days = (1..31).toList()
    val months = listOf(
        stringResource(R.string.january),
        stringResource(R.string.february),
        stringResource(R.string.march),
        stringResource(R.string.april),
        stringResource(R.string.may),
        stringResource(R.string.june),
        stringResource(R.string.july),
        stringResource(R.string.august),
        stringResource(R.string.september),
        stringResource(R.string.october),
        stringResource(R.string.november),
        stringResource(R.string.december)
    )
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (1925..currentYear).toList()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        // Day Dropdown
        DropdownMenuBox(
            items = days,
            selectedItem = selectedDay,
            onItemSelected = { day -> onDateSelected(day, selectedMonth, selectedYear) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Month Dropdown
        DropdownMenuBox(
            items = months,
            selectedItem = months[selectedMonth - 1],  // Display month name
            displayText = months[selectedMonth - 1].take(3),  // Display first 3 letters
            onItemSelected = { month ->
                val index = months.indexOf(month) + 1
                onDateSelected(selectedDay, index, selectedYear)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Year Dropdown
        DropdownMenuBox(
            items = years,
            selectedItem = selectedYear,
            onItemSelected = { year -> onDateSelected(selectedDay, selectedMonth, year) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Save Icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = stringResource(R.string.save),
            modifier = Modifier.clickable {
                coroutineScope.launch {
                    onSave()
                }
            }
        )
    }
}

@Composable
fun <T> DropdownMenuBox(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayText: String = selectedItem.toString()
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(90.dp)
        ) {
            Text(text = displayText)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    onItemSelected(item)
                    expanded = false
                }) {
                    Text(text = item.toString())
                }
            }
        }
    }
}

//--------------- Standard text field with trailing icon ------------------------
@Composable
fun EditableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: suspend () -> Unit,
    isPhone: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    val coroutineScope = rememberCoroutineScope()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }
    var showResponseDialog by remember { mutableStateOf(false) }

    Column {
        Text(text = label)
        androidx.compose.material.TextField(
            value = value,
            onValueChange = { newValue ->
                if (!isPhone || newValue.all { it.isDigit() } && newValue.length <= 8) {
                    onValueChange(newValue)
                }
            },
            singleLine = singleLine,
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.save),
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            onSave()
                        }
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = PrimaryGray)
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showPasswordDialog) {
        PasswordConfirmationDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { password ->
                coroutineScope.launch {
                    val response = changeEmail(value, password)
                    responseMessage = response
                    showResponseDialog = true
                    showPasswordDialog = false
                }
            }
        )
    }

    if (showResponseDialog) {
        AlertDialog(
            onDismissRequest = { showResponseDialog = false },
            text = { Text(text = responseMessage) },
            confirmButton = {
                Button(onClick = { showResponseDialog = false }) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }
}