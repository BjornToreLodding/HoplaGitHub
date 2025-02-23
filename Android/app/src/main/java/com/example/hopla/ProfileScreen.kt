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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.sp
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryWhite
import android.app.Application
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.AccountBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.AndroidViewModel
import android.util.Log

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
    // State variables for sending a report
    var showReportDialog by remember { mutableStateOf(false) }
    var reportTitle by remember { mutableStateOf("") }
    var reportText by remember { mutableStateOf("") }
    // State variables for logging out
    var showLogOutDialog by remember { mutableStateOf(false) }
    // State variables for deleting user
    var showDeleteDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.language))
            LanguageSelection(languageViewModel)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = PrimaryBlack
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.mode))
            ModeSelection(themeViewModel)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = PrimaryBlack
        )

        Text(
            text = stringResource(R.string.send_a_report),
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                .clickable { showReportDialog = true },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )

        Text(
            text = stringResource(R.string.log_out),
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp)
                .clickable { showLogOutDialog = true },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )

        Text(
            text = stringResource(R.string.delete_user),
            modifier = Modifier
                .padding(start = 16.dp)
                .clickable { showDeleteDialog = true },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text(text = stringResource(R.string.send_a_report)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    TextField(
                        value = reportTitle,
                        onValueChange = { reportTitle = it },
                        label = { Text(text = stringResource(R.string.title)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = reportText,
                        onValueChange = { reportText = it },
                        label = { Text(text = stringResource(R.string.report)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Handle sending report logic here
                    showReportDialog = false
                }) {
                    Text(text = stringResource(R.string.send), color = PrimaryWhite)
                    reportTitle = ""
                    reportText = ""
                }
            },
            dismissButton = {
                Button(onClick = { showReportDialog = false }) {
                    Text(text = stringResource(R.string.cancel), color = PrimaryWhite)
                    reportTitle = ""
                    reportText = ""
                }
            }
        )
    }
    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog = false },
            title = { Text(text = stringResource(R.string.log_out)) },
            text = { Text(text = stringResource(R.string.confirm_logout)) },
            confirmButton = {
                Button(onClick = {
                    userViewModel.logOut()
                    showLogOutDialog = false
                }) {
                    Text(text = stringResource(R.string.confirm), color = PrimaryWhite)
                }
            },
            dismissButton = {
                Button(onClick = { showLogOutDialog = false }) {
                    Text(text = stringResource(R.string.cancel), color = PrimaryWhite)
                }
            }
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete_user)) },
            text = {
                Column {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = stringResource(R.string.confirm_password)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Handle delete user logic here
                    userViewModel.deleteUser()
                    showDeleteDialog = false
                }) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
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
fun ProfilePicture(imageResource: Int = R.drawable.logo2) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    ImagePicker(
        onImageSelected = { bitmap -> imageBitmap = bitmap },
        text = stringResource(R.string.change_profile_picture)
    )

    // Display the current profile picture (either the selected or default image)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .border(10.dp, MaterialTheme.colorScheme.secondary, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(10.dp, MaterialTheme.colorScheme.secondary, CircleShape)
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
                    painter = painterResource(id = imageResource),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
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
                shape = RectangleShape
            ) {
                Text(text = stringResource(R.string.my_trips), color = PrimaryWhite)
            }
            Button(
                onClick = { navController.navigate("my_horses") },
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text(text = stringResource(R.string.my_horses), color = PrimaryWhite)
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
                shape = RectangleShape
            ) {
                Text(text = stringResource(R.string.friends), color = PrimaryWhite)
            }
            Button(
                onClick = { navController.navigate("following") },
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text(text = stringResource(R.string.following), color = PrimaryWhite)
            }
        }
    }
}



@Composable
fun UserChanges(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Get username and email from the API
    LaunchedEffect(Unit) {
        try {
            val user = RetrofitInstance.api.getUser()
            email = user.email
            username = user.name
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("UserChanges", "Error loading user data", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.tertiary)
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp, color = PrimaryBlack)
                Text(text = stringResource(R.string.username))
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = {  },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(thickness = 1.dp, color = PrimaryBlack)
                Text(text = stringResource(R.string.email))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                HorizontalDivider(thickness = 1.dp, color = PrimaryBlack)
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
}

@Composable
fun MyTripsScreen(navController: NavController) {
    val trips = listOf(
        Trip("Trip to the mountains", "2023-10-01", "10", "2", R.drawable.stockimg1),
        Trip("City walk", "2023-09-15", "5", "1", R.drawable.stockimg2),
        Trip("Beach run", "2023-08-20", "8", "1.5", R.drawable.stockimg2),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
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
                    text = stringResource(R.string.my_trips),
                    fontSize = 24.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(trips) { trip ->
                TripItem(trip)
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
            .background(MaterialTheme.colorScheme.secondary)
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

@Composable
fun FriendsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val people = listOf(
        Person("Ole", R.drawable.friend1, PersonStatus.FRIEND),
        Person("Dole", R.drawable.friend2, PersonStatus.FRIEND),
        Person("Doffen", R.drawable.friend3, PersonStatus.FRIEND),
        Person("Dolly", R.drawable.friend1, PersonStatus.FRIEND),
        Person("Langbein", R.drawable.friend2, PersonStatus.FRIEND),
        Person("Donald", R.drawable.friend3, PersonStatus.FRIEND)
    )
    val filteredFriends = people.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, stringResource(R.string.friends))

            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredFriends) { friend ->
                    PersonItem(friend, navController)
                }
            }
        }
        AddButton(onClick = { navController.navigate("addFriendScreen") })
    }
}

@Composable
fun PersonDetailScreen(navController: NavController, friendName: String, friendImageResource: Int) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
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
                    painter = painterResource(id = friendImageResource),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = friendName, style = MaterialTheme.typography.headlineLarge)
                    Text(text = stringResource(R.string.friends) + " : 5", style = MaterialTheme.typography.bodySmall)
                    Text(text = stringResource(R.string.trips_added) + " : 3", style = MaterialTheme.typography.bodySmall)
                    Text( text = stringResource(R.string.remove_friend),
                        modifier = Modifier.clickable {
                            showConfirmationDialog = true
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Red,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                    if (showConfirmationDialog) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialog = false },
                            title = { Text(text = stringResource(R.string.remove_friend)) },
                            text = { Text(text = stringResource(R.string.delete_friend_dialogue)) },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle confirmation action here
                                    showConfirmationDialog = false
                                }) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showConfirmationDialog = false }) {
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
fun FollowingScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    val people = listOf(
        Person("Rachel", R.drawable.friend1, PersonStatus.FOLLOWING),
        Person("Monica", R.drawable.friend2, PersonStatus.FOLLOWING),
        Person("Phoebe", R.drawable.friend3, PersonStatus.FOLLOWING),
        Person("Chandler", R.drawable.friend1, PersonStatus.FOLLOWING),
        Person("Joey", R.drawable.friend2, PersonStatus.FOLLOWING),
        Person("Ross", R.drawable.friend3, PersonStatus.FOLLOWING)
    )

    val filteredFollowing = people.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, stringResource(R.string.following))

            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredFollowing) { following ->
                    PersonItem(following, navController)
                }
            }
        }
        AddButton(onClick = { navController.navigate("addFriendScreen") })
    }
}

@Composable
fun FollowingDetailScreen(navController: NavController, followingName: String, followingImageResource: Int) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = followingName, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = followingImageResource),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun MyHorsesScreen(navController: NavController) {
    val horses = listOf(
        Horse("Hest1", R.drawable.horse1, "Dølahest", 5),
        Horse("Hest2", R.drawable.horse2, "Fjording", 7),
        Horse("Hest3", R.drawable.horse3, "Araber", 3),
        Horse("Hest4", R.drawable.horse1, "Frieser", 6),
        Horse("Hest5", R.drawable.horse2, "Shetlandsponni", 10),
        Horse("Hest6", R.drawable.horse3, "Islandshest", 8)
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
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
}

@Composable
fun HorseDetailScreen(navController: NavController, horseName: String, horseImageResource: Int, horseBreed: String, horseAge: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = horseName, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = horseImageResource),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Breed: $horseBreed", style = MaterialTheme.typography.bodySmall)
            Text(text = "Age: $horseAge", style = MaterialTheme.typography.bodySmall)
        }
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
    }
}

@Composable
fun HorseItem(horse: Horse, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
            .clickable {
                navController.navigate("horse_detail/${horse.name}/${horse.imageResource}/${horse.breed}/${horse.age}")
            }
    ) {
        Image(
            painter = painterResource(id = horse.imageResource),
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
        // WHen it is a horse that is supposed to be added
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
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
            .clickable { navController.navigate("person_detail/${person.name}/${person.imageResource}") }
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