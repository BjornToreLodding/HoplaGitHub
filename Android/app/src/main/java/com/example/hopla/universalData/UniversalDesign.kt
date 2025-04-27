package com.example.hopla.universalData

//noinspection UsingMaterialAndMaterial3Libraries
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.hopla.R
import com.example.hopla.apiService.changeEmail
import com.example.hopla.apiService.createUserReport
import com.example.hopla.profile.PasswordConfirmationDialog
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleDialog
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.textFieldLabelTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.ui.theme.underlinedTextStyleSmall
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

private val IMAGE_PICKER_PADDING_TOP = 8.dp
private val SPACER_HEIGHT = 8.dp
private val DROPDOWN_MENU_WIDTH = 90.dp

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
            .padding(IMAGE_PICKER_PADDING_TOP)
    )
}

// A button (used for add) that takes navigation in as a parameter
@Composable
fun AddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(47.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(37.dp)
            )
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
                    tint = MaterialTheme.colorScheme.onPrimary,
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
                style = headerTextStyleSmall,
                color = MaterialTheme.colorScheme.onPrimary
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
            .padding(top = IMAGE_PICKER_PADDING_TOP)
            .clickable { showDialog.value = true },
        style = underlinedTextStyleSmall,
        color = MaterialTheme.colorScheme.secondary
    )

    // Dialog to confirm taking a picture
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = stringResource(R.string.change_profile_picture),
                    style = underheaderTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                ) },
            text = {
                Text(
                    text = stringResource(R.string.profile_pic_description),
                    style = generalTextStyleDialog,
                    color = MaterialTheme.colorScheme.secondary
                ) },
            confirmButton = {
                Column {
                    Button(onClick = {
                        if (permissionGranted.value) {
                            cameraLauncher.launch(null)
                        }
                        showDialog.value = false
                    }, shape = RectangleShape ) {
                        Text(
                            text = stringResource(R.string.take_a_picture),
                            style = buttonTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(SPACER_HEIGHT))
                    Button(onClick = {
                        contentPickerLauncher.launch("image/*")
                        showDialog.value = false
                    }, shape = RectangleShape) {
                        Text(
                            text = stringResource(R.string.choose_from_library),
                            style = buttonTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }, shape = RectangleShape) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = buttonTextStyle,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
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
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.send_a_report),
                style = underheaderTextStyle,
                color = MaterialTheme.colorScheme.secondary
            ) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Column {
                            androidx.compose.material.TextField(
                                value = reportTitle,
                                onValueChange = { reportTitle = it },
                                label = {
                                    Text(
                                        text = stringResource(R.string.title),
                                        style = textFieldLabelTextStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    ) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Spacer(modifier = Modifier.height(SPACER_HEIGHT))
                            androidx.compose.material.TextField(
                                value = reportText,
                                onValueChange = { reportText = it },
                                label = {
                                    Text(
                                        text = stringResource(R.string.report),
                                        style = textFieldLabelTextStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    ) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        loading = true
                        try {
                            val reportRequest = UserReportRequest(
                                entityId = entityId,
                                entityName = entityName,
                                category = reportTitle,
                                message = reportText
                            )
                            createUserReport(token, reportRequest)
                            onDismiss()
                        } catch (e: Exception) {
                            Log.e("ReportDialog", "Error sending report: ${e.message}")
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading, // Disable button while loading
                shape = RectangleShape
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.send),
                        style = buttonTextStyle,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, shape = RectangleShape) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = buttonTextStyle,
                    color = MaterialTheme.colorScheme.onPrimary
                )
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
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = text, style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

// A composable for picking a date of birth
@Composable
fun DateOfBirthPicker(
    selectedDay: Int,
    selectedMonth: Int,
    selectedYear: Int,
    onDateSelected: (Int, Int, Int) -> Unit,
    onSave: suspend () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.secondary
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
    val years = (1925..currentYear).toList().reversed()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(IMAGE_PICKER_PADDING_TOP)
    ) {
        // Day Dropdown
        DropdownMenuBox(
            items = days,
            selectedItem = selectedDay,
            onItemSelected = { day -> onDateSelected(day, selectedMonth, selectedYear) },
            textColor = textColor
        )

        Spacer(modifier = Modifier.width(SPACER_HEIGHT))

        // Month Dropdown
        DropdownMenuBox(
            items = months,
            selectedItem = months[selectedMonth - 1],  // Display month name
            displayText = months[selectedMonth - 1].take(3),  // Display first 3 letters
            onItemSelected = { month ->
                val index = months.indexOf(month) + 1
                onDateSelected(selectedDay, index, selectedYear)
            },
            textColor = textColor
        )

        Spacer(modifier = Modifier.width(IMAGE_PICKER_PADDING_TOP))

        // Year Dropdown
        DropdownMenuBox(
            items = years,
            selectedItem = selectedYear,
            onItemSelected = { year -> onDateSelected(selectedDay, selectedMonth, year) },
            textColor = textColor
        )

        Spacer(modifier = Modifier.width(SPACER_HEIGHT))

        // Save Icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = stringResource(R.string.save),
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable {
                coroutineScope.launch {
                    onSave()
                }
            }
        )
    }
}

// A dropdown menu box for selecting an item from a list
@Composable
fun <T> DropdownMenuBox(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    displayText: String = selectedItem.toString(),
    textColor: Color = MaterialTheme.colorScheme.onBackground
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(DROPDOWN_MENU_WIDTH),
        ) {
            Text(text = displayText, color = textColor)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    onItemSelected(item)
                    expanded = false
                }) {
                    Text(text = item.toString(), color = textColor)
                }
            }
        }
    }
}

// Editable text field for user input
@Composable
fun EditableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: suspend () -> Unit,
    isPhone: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    textColor: Color = MaterialTheme.colorScheme.secondary
) {
    val coroutineScope = rememberCoroutineScope()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }
    var showResponseDialog by remember { mutableStateOf(false) }

    Column {
        Text(text = label, color = MaterialTheme.colorScheme.secondary)
        androidx.compose.material.TextField(
            value = value,
            onValueChange = { newValue ->
                if (!isPhone || newValue.all { it.isDigit() } && newValue.length <= 8) {
                    onValueChange(newValue)
                }
            },
            singleLine = singleLine,
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.save),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            onSave()
                        }
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(SPACER_HEIGHT))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.tertiary)
        Spacer(modifier = Modifier.height(SPACER_HEIGHT))
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

// Function to format date and time strings from backend to a more readable format
fun formatDateTime(dateTimeString: String): Pair<String, String> {
    val zonedDateTime = ZonedDateTime.parse(dateTimeString)
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH.mm")

    val formattedDate = zonedDateTime.format(dateFormatter)
    val formattedTime = zonedDateTime.format(timeFormatter)

    return Pair(formattedDate, formattedTime)
}

// Dialog to display the server error message
@Composable
fun ServerErrorDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.error),
                    style = underheaderTextStyle,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = stringResource(R.string.server_error_message),
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
                Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = stringResource(R.string.ok),
                        style = buttonTextStyle
                    )
                }
            }
        }
    }
}
