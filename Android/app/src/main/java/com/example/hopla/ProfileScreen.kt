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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hopla.ui.theme.ThemeViewModel
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.background
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.graphics.Bitmap
import androidx.compose.material3.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryWhite
import android.app.Application
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.sharp.AccountBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.AndroidViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.example.hopla.ui.theme.PrimaryGray

// Main profile function
@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
            ProfilePicture()
            ProfileButtons(navController)
            UserChanges(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth())
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
    var showReportDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ScreenHeader(navController, stringResource(R.string.settings))
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                SettingsCategory(title = "General")
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(R.string.language),
                    trailingContent = { LanguageSelection(languageViewModel) }
                )
                HorizontalDivider()

                SettingsItem(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(R.string.mode),
                    trailingContent = { ModeSelection(themeViewModel) }
                )
                HorizontalDivider()
            }

            item {
                SettingsCategory(title = "Account")
                SettingsClickableItem(
                    icon = Icons.Default.Create,
                    title = stringResource(R.string.send_a_report),
                    onClick = { showReportDialog = true }
                )
                HorizontalDivider()

                SettingsClickableItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = stringResource(R.string.log_out),
                    onClick = { showLogOutDialog = true }
                )
                HorizontalDivider()

                SettingsClickableItem(
                    icon = Icons.Default.Delete,
                    title = stringResource(R.string.delete_user),
                    onClick = { showDeleteDialog = true }
                )
                HorizontalDivider()
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(onDismiss = { showReportDialog = false })
    }
    if (showLogOutDialog) {
        ConfirmDialog(
            title = stringResource(R.string.log_out),
            message = stringResource(R.string.confirm_logout),
            onConfirm = {
                userViewModel.logOut()
                showLogOutDialog = false
            },
            onDismiss = { showLogOutDialog = false }
        )
    }
    if (showDeleteDialog) {
        PasswordConfirmDialog(
            password = password,
            onPasswordChange = { password = it },
            onConfirm = {
                userViewModel.deleteUser()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

// Settings Category Header
@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

// Settings Item with an Icon and Trailing Content (e.g., switch or dropdown)
@Composable
fun SettingsItem(icon: ImageVector, title: String, trailingContent: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Text(text = title, modifier = Modifier.weight(1f).padding(start = 16.dp))
        trailingContent()
    }
}

// Clickable Settings Item
@Composable
fun SettingsClickableItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Text(text = title, modifier = Modifier.weight(1f).padding(start = 16.dp))
    }
}

// Confirm Action Dialog (Log Out, Delete, etc.)
@Composable
fun ConfirmDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

// Password Confirmation Dialog
@Composable
fun PasswordConfirmDialog(password: String, onPasswordChange: (String) -> Unit, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_user)) },
        text = {
            Column {
                TextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.confirm_password)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

class LanguageViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _selectedLanguage = mutableStateOf(savedStateHandle.get<String>("language") ?: "Norwegian")
    val selectedLanguage: State<String> = _selectedLanguage

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        savedStateHandle["language"] = language
        setLocale(getApplication(), if (language == "Norwegian") "no" else "en")
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
}

@Composable
fun LanguageSelection(languageViewModel: LanguageViewModel) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Norsk",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { languageViewModel.setLanguage("Norwegian") }
            )
            Text(
                text = "English",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { languageViewModel.setLanguage("English") }
            )
        }
    }
}

@Composable
fun ModeSelection(themeViewModel: ThemeViewModel = viewModel()) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column {
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

// Function to update the profile picture of the user
@Composable
fun ProfilePicture(imageUrl: String = UserSession.profilePictureURL) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .border(5.dp, PrimaryWhite, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(5.dp, PrimaryWhite, CircleShape)
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    ImagePicker(
        onImageSelected = { bitmap -> imageBitmap = bitmap },
        text = stringResource(R.string.change_profile_picture)
    )
}

@Composable
fun ProfileButtons(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("my_trips") },
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(50.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryWhite
                )
            ) {
                Text(text = stringResource(R.string.my_trips), color = PrimaryBlack)
            }
            Button(
                onClick = { navController.navigate("my_horses") },
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(50.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryWhite
                )
            ) {
                Text(text = stringResource(R.string.my_horses), color = PrimaryBlack)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("friends") },
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(50.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryWhite
                )
            ) {
                Text(text = stringResource(R.string.friends), color = PrimaryBlack)
            }
            Button(
                onClick = { navController.navigate("following") },
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(50.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryWhite
                )
            ) {
                Text(text = stringResource(R.string.following), color = PrimaryBlack)
            }
        }
    }
}

@Composable
fun UserChanges(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf(UserSession.email) }
    var username by remember { mutableStateOf(UserSession.name) }
    var showDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = PrimaryGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.username))
            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = { }
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = PrimaryGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.email))
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = { }
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = PrimaryGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.change_password),
                modifier = Modifier.clickable { showDialog = true },
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.change_password)) },
            text = {
                Column {
                    CustomTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(text = stringResource(R.string.current_password)) }
                    )
                    CustomTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(text = stringResource(R.string.new_password)) }
                    )
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(text = stringResource(R.string.confirm_password)) }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Handle password change logic here
                    showDialog = false
                }) {
                    Text(text = stringResource(R.string.confirm))
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
    var userHikes by remember { mutableStateOf<List<Hike>>(emptyList()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(pageNumber) {
        coroutineScope.launch {
            try {
                val newHikes = fetchUserHikes(token, pageNumber)
                userHikes = userHikes + newHikes
            } catch (e: Exception) {
                Log.e("UserHikesScreen", "Error fetching user hikes", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, stringResource(R.string.my_trips))

            if (userHikes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userHikes) { hike ->
                        HikeItem(hike)
                    }
                    item {
                        Button(
                            onClick = { pageNumber++ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = stringResource(R.string.load_more))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripItem(trip: Trip) {
    var showDialog by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .clickable { /* Handle click event */ }
            .padding(16.dp)
    ) {
        Column {
            Text(text = trip.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(R.string.dateString) + ": ${trip.date}", style = MaterialTheme.typography.bodySmall)
            Text(text = stringResource(R.string.length) + ": ${trip.length} km", style = MaterialTheme.typography.bodySmall)
            Text(text = stringResource(R.string.hourString) + ": ${trip.time} " + stringResource(R.string.hourString), style = MaterialTheme.typography.bodySmall)
        }
        IconButton(
            onClick = {
                showDialog = true
                showImage = true // Reset showImage to true when the icon is clicked
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Sharp.AccountBox,
                contentDescription = stringResource(R.string.liked)
            )
        }
    }

    if (showDialog && showImage) {
        Dialog(onDismissRequest = {
            showDialog = false
            showImage = true // Reset showImage to true when the dialog is dismissed so image can be clicked several times
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Image(
                        painter = painterResource(id = trip.imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    IconButton(
                        onClick = { showImage = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    }
                }
            }
        }
    }
}

// Details about a person
@Composable
fun UsersProfileScreen(navController: NavController, userId: String) {
    var friendProfile by remember { mutableStateOf<FriendProfile?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    var showFullDescription by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                friendProfile = fetchFriendProfile(userId, token)
            } catch (e: Exception) {
                Log.e("FriendProfileScreen", "Error fetching friend profile", e)
            }
        }
    }

    friendProfile?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = profile.alias, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile.pictureUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(5.dp, PrimaryWhite, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = profile.name, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = stringResource(R.string.friends) + ": ${profile.friendsCount}",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                        modifier = Modifier.clickable { /* Handle friends click */ }
                    )
                    Text(
                        text = stringResource(R.string.horses) + ": ${profile.horseCount}",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                        modifier = Modifier.clickable { /* Handle horses click */ }
                    )
                    Text(
                        text = stringResource(R.string.relation_status) + ": ${profile.relationStatus}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
                    .animateContentSize() // Animate size changes
            ) {
                Column {
                    Text(
                        text = if (showFullDescription) profile.description ?: "" else profile.description?.take(100)?.plus("...") ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (showFullDescription) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if ((profile.description?.length ?: 0) > 100) {
                        IconButton(onClick = { showFullDescription = !showFullDescription }) {
                            Icon(
                                imageVector = if (showFullDescription) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (showFullDescription) "Show less" else "Show more"
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.trips), style = MaterialTheme.typography.headlineSmall)
            profile.userHikes.forEach { hike ->
                HikeItem(hike)
            }
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(100.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { /* Handle load more click */ },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Load More", style = MaterialTheme.typography.bodyMedium, color = PrimaryBlack)
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val formattedDate = profile.created_at?.let { formatDate(it) } ?: "Unknown"
                Text(
                    text = "Created at: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                )
            }
        }
    } ?: run {
        // Show a loading indicator
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun HikeItem(hike: Hike) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .padding(16.dp)
            .clickable {
                // Handle item click
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = hike.pictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = hike.trailName, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Length: ${hike.length} km", style = MaterialTheme.typography.bodySmall)
            Text(text = "Duration: ${hike.duration} min", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PersonDetailScreen(navController: NavController, person: Person) {
    var showConfirmationDialogFriend by remember { mutableStateOf(false) }
    var showConfirmationDialogFollowing by remember { mutableStateOf(false) }
    var showConfirmationDialogPending by remember { mutableStateOf(false) }
    val trips = listOf(
        Trip("Trip to the mountains", "2023-10-01", "10", "2", R.drawable.stockimg1),
        Trip("City walk", "2023-09-15", "5", "1", R.drawable.stockimg2),
        Trip("Beach run", "2023-08-20", "8", "1.5", R.drawable.stockimg2)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.back)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = person.imageResource),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(5.dp, PrimaryWhite, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = person.name, style = MaterialTheme.typography.headlineLarge)
                    Text(text = stringResource(R.string.friends) + " : 5", style = MaterialTheme.typography.bodySmall)
                    Text(text = stringResource(R.string.trips_added) + " : 3", style = MaterialTheme.typography.bodySmall)
                    Text(text = person.status.toString(), style = MaterialTheme.typography.bodySmall)
                    // If the user is a friend
                    if (person.status == PersonStatus.FRIEND) {
                        Text(
                            text = stringResource(R.string.remove_friend),
                            modifier = Modifier.clickable {
                                showConfirmationDialogFriend = true
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    // If the user is following that person
                    if (person.status == PersonStatus.FOLLOWING) {
                        Text(
                            text = stringResource(R.string.unfollow),
                            modifier = Modifier.clickable{
                                showConfirmationDialogFollowing = true
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    // If the user has a pending friend request
                    if (person.status == PersonStatus.PENDING) {
                        Text(
                            text = stringResource(R.string.friendrequest_pending),
                            modifier = Modifier.clickable {
                                showConfirmationDialogPending = true
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    // If the user has no relation to that user
                    if (person.status == PersonStatus.NONE) {
                        Button(onClick = { /* Handle follow logic here */ }) {
                            Text(text = stringResource(R.string.follow))
                        }
                        Button(onClick = { /* Handle follow logic here */ }) {
                            Text(text = stringResource(R.string.add_friend))
                        }
                    }
                    if (showConfirmationDialogFriend) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialogFriend = false },
                            title = { Text(text = stringResource(R.string.remove_friend)) },
                            text = { Text(text = stringResource(R.string.delete_friend_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialogFriend = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialogFriend = false }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                    if (showConfirmationDialogFollowing) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialogFollowing = false },
                            title = { Text(text = stringResource(R.string.unfollow)) },
                            text = { Text(text = stringResource(R.string.unfollow_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialogFollowing = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialogFollowing = false }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                    if (showConfirmationDialogPending) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialogPending = false },
                            title = { Text(text = stringResource(R.string.friendrequest_pending)) },
                            text = { Text(text = stringResource(R.string.remove_request_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialogPending = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialogPending = false }) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(trips) { trip ->
                    TripItem(trip)
                }
            }
        }
    }
}

@Composable
fun UserListScreen(
    navController: NavController,
    title: String,
    fetchUsers: suspend (String) -> List<UserItem>
) {
    var users by remember { mutableStateOf<List<UserItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                users = fetchUsers(UserSession.token)
            } catch (e: Exception) {
                Log.e("UserListScreen", "Error fetching users", e)
            }
        }
    }

    val filteredUsers = users.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.alias.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, title)

            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            if (filteredUsers.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_matches),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredUsers) { user ->
                        UserItemComposable(user, navController)
                    }
                }
            }
        }
        AddButton(onClick = { navController.navigate("addFriendScreen") })
    }
}

@Composable
fun FriendsScreen(navController: NavController) {
    UserListScreen(
        navController = navController,
        title = stringResource(R.string.friends),
        fetchUsers = { token -> fetchFriends(token) }
    )
}

@Composable
fun FollowingScreen(navController: NavController) {
    UserListScreen(
        navController = navController,
        title = stringResource(R.string.following),
        fetchUsers = { token -> fetchFollowing(token) }
    )
}

@Composable
fun UserItemComposable(userItem: UserItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .padding(16.dp)
            .clickable {
                navController.navigate("friend_profile/${userItem.id}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = userItem.pictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = userItem.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = userItem.alias, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MyHorsesScreen(navController: NavController) {
    var horses by remember { mutableStateOf<List<Horse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            horses = fetchHorses(UserSession.token)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(8.dp)) {
            ScreenHeader(navController, stringResource(R.string.my_horses))
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(horses) { horse ->
                HorseItem(horse, navController)
            }
        }
    }
    AddButton(onClick = { navController.navigate("addHorseScreen") })
}

@Composable
fun HorseDetailScreen(navController: NavController, horseId: String) {
    var horseDetail by remember { mutableStateOf<HorseDetail?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(horseId) {
        coroutineScope.launch {
            try {
                horseDetail = fetchHorseDetails(horseId, UserSession.token)
            } catch (e: Exception) {
                Log.e("HorseDetailScreen", "Error fetching horse details", e)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        horseDetail?.let { horse ->
            val formattedDob = try {
                val dateTime = horse.dob.split("T")[0]
                val (year, month, day) = dateTime.split("-")
                "$day.$month.$year"
            } catch (e: Exception) {
                Log.e("HorseDetailScreen", "Error parsing date of birth", e)
                "Unknown"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                // Name
                Text(
                    text = horse.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Image with Border & Shadow
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .shadow(8.dp, CircleShape)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = horse.horsePictureUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Card with Details
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailRow(label = stringResource(R.string.breed) + ":", value = horse.breed)
                        DetailRow(label = stringResource(R.string.age) + ":", value = horse.age.toString())
                        DetailRow(label = stringResource(R.string.date_of_birth) + ":", value = formattedDob)
                    }
                }
            }
        }

        // Close Button (Top-Right)
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}


// Horsedetail commented out until method for connecting to database is implemented
@Composable
fun HorseItem(horse: Horse, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .padding(16.dp)
            .clickable {
                navController.navigate("horse_detail/${horse.id}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = horse.horsePictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = horse.name, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun AddNewType(
    navController: NavController,
    type: String,
    onAdd: (String, Bitmap?, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var breedOrFriendType by remember { mutableStateOf("") }
    var ageOrFriendAge by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val testNonFriends = listOf(
        Person("Penny", R.drawable.friend1, PersonStatus.NONE),
        Person("Sheldon", R.drawable.friend2, PersonStatus.NONE),
        Person("Amy", R.drawable.friend3, PersonStatus.PENDING),
        Person("Leonard", R.drawable.friend1, PersonStatus.NONE),
        Person("Howard", R.drawable.friend2, PersonStatus.NONE),
        Person("Bernadette", R.drawable.friend3, PersonStatus.PENDING)
    )
    // Search for adding new friends/followers
    val filteredPersons = testNonFriends.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // When it is a horse that is supposed to be added
        when (type) {
            "Horse" -> {
                Text(text = "Add New $type", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        TextField(
                            value = breedOrFriendType,
                            onValueChange = { breedOrFriendType = it },
                            label = { Text(text = stringResource(R.string.breed)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = ageOrFriendAge.toString(),
                            onValueChange = { ageOrFriendAge = it.toIntOrNull() ?: 0 },
                            label = { Text(text = stringResource(R.string.age)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                // Add option to remove image if it is added
                if (imageBitmap != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                        )
                        IconButton(onClick = { imageBitmap = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ImagePicker(
                    onImageSelected = { bitmap -> imageBitmap = bitmap },
                    text = stringResource(R.string.add_image)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onAdd(name, imageBitmap, breedOrFriendType, ageOrFriendAge)
                    navController.popBackStack()
                }) {
                    Text(text = "Add $type")
                }
            }
            // If it is a friend/follow that is supposed to be added
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredPersons) { persons ->
                            PersonItem(persons, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonItem(person: Person, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PrimaryWhite)
            .padding(16.dp)
            .clickable { navController.navigate("person_detail/${person.name}/${person.imageResource}/${person.status}") }
    ) {
        Image(
            painter = painterResource(id = person.imageResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = person.name, style = MaterialTheme.typography.bodyLarge)
    }
}