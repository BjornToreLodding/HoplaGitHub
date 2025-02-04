package com.example.hopla

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hopla.ui.theme.ThemeViewModel
import java.util.Locale
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.background
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.sp


@Composable
fun ProfileScreen(
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { navController.navigate("settings") },
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture()
            ProfileButtons(navController)
            UserChanges(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SettingsScreen(
    languageViewModel: LanguageViewModel,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.settings),
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = stringResource(R.string.language))
                Text(text = stringResource(R.string.mode))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                LanguageSelection(languageViewModel)
                ModeSelection(languageViewModel, themeViewModel)

            }
        }

        Text(
            text = stringResource(R.string.log_out),
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp)
                .clickable { userViewModel.logOut() },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )

        Text(
            text = stringResource(R.string.delete_user),
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { userViewModel.deleteUser() },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }
}


class LanguageViewModel(
    private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedLanguage = mutableStateOf(savedStateHandle.get<String>("language") ?: "Norwegian")
    val selectedLanguage: State<String> = _selectedLanguage

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        savedStateHandle["language"] = language
        setLocale(context, if (language == "Norwegian") "no" else "en")
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

@Composable
fun LanguageSelection(languageViewModel: LanguageViewModel) {
    Column() {
        Row() {
            Text(
                text = "Norsk",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { languageViewModel.setLanguage("Norwegian") }
            )
            Text(
                text = "English",
                modifier = Modifier
                    .clickable { languageViewModel.setLanguage("English") }
            )
        }
    }
}

@Composable
fun ModeSelection(languageViewModel: LanguageViewModel, themeViewModel: ThemeViewModel = viewModel()) {
    Column() {
        Row() {
            Text(
                text = stringResource(R.string.light),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { themeViewModel.setDarkTheme(false) }
            )
            Text(
                text = stringResource(R.string.dark),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { themeViewModel.setDarkTheme(true) }
            )
        }
    }
}

@Composable
fun ProfilePicture(imageResource: Int = R.drawable.logo2) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        imageBitmap = bitmap
    }

    // Content picker launcher
    val contentPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            imageBitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permissionGranted = isGranted
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
                permissionGranted = true
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Display the current profile picture (either the captured or default image)
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!.asImageBitmap(),
            contentDescription = "Captured Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(10.dp, Color.Black, CircleShape)
        )
    } else {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(10.dp, Color.Black, CircleShape)
        )
    }

    // Text to trigger camera action
    Text(
        text = stringResource(R.string.change_profile_picture),
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable { showDialog = true },
        style = TextStyle(textDecoration = TextDecoration.Underline)
    )

    // Dialog to confirm taking a picture
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.change_profile_picture)) },
            text = { Text(text = stringResource(R.string.profile_pic_description)) },
            confirmButton = {
                Column {
                    Button(onClick = {
                        if (permissionGranted) {
                            cameraLauncher.launch(null)
                        }
                        showDialog = false
                    }) {
                        Text(text = stringResource(R.string.take_a_picture))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        contentPickerLauncher.launch("image/*")
                        showDialog = false
                    }) {
                        Text(text = stringResource(R.string.choose_from_library))
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun ProfileButtons(navController: NavController) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { navController.navigate("my_trips") },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = stringResource(R.string.my_trips))
        }
        Button(
            onClick = { navController.navigate("friends") },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = stringResource(R.string.friends))
        }
        Button(
            onClick = { navController.navigate("following") },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = stringResource(R.string.following))
        }
    }
}

@Composable
fun UserChanges(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("test@gmail.com") }
    var username by remember { mutableStateOf("test") }
    var showDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Gray)
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Black, thickness = 1.dp)
            Text(text = stringResource(R.string.username))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = {  },
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Color.Black, thickness = 1.dp)
            Text(text = stringResource(R.string.email))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { },
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Color.Black, thickness = 1.dp)
            Text(
                text = stringResource(R.string.change_password),
                modifier = Modifier.clickable { showDialog = true },
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.change_password)) },
            text = {
                Column {
                    TextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(text = stringResource(R.string.current_password)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(text = stringResource(R.string.new_password)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(text = stringResource(R.string.confirm_password)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Handle password change logic here
                    showDialog = false
                }) {
                    Text(text = stringResource(R.string.stables))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun MyTripsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.my_trips),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun FriendsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.friends),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun FollowingScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.following),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}