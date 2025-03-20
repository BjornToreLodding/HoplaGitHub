package com.example.hopla.profile

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
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
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.OtherUsers
import com.example.hopla.R
import com.example.hopla.apiService.changePassword
import com.example.hopla.universalData.UserSession
import com.example.hopla.apiService.fetchAllUsers
import com.example.hopla.apiService.uploadProfilePicture
import com.example.hopla.ui.theme.PrimaryGray
import com.example.hopla.ui.theme.textFieldLabelTextStyle
import org.json.JSONObject

// Main profile function
@Composable
fun ProfileScreen(navController: NavController) {
    Log.d("ProfilePicture", "Profile Screen entry: ${UserSession.profilePictureURL}")
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(4.dp),
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

// Function to update the profile picture of the user
@Composable
fun ProfilePicture(imageUrl: String = UserSession.profilePictureURL) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
        onImageSelected = { bitmap ->
            imageBitmap = bitmap
            coroutineScope.launch {
                try {
                    val response = uploadProfilePicture(UserSession.token, UserSession.userId, bitmap!!)
                    val filePath = JSONObject(response).getString("filePath").replace("/uploads", "")
                    val fullUrl = "https://files.hopla.no$filePath"
                    UserSession.profilePictureURL = fullUrl
                    Log.d("ProfilePicture", "New profile picture URL: ${UserSession.profilePictureURL}")
                } catch (e: Exception) {
                    Log.e("ProfilePicture", "Error uploading profile picture", e)
                }
            }
        },
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
    var username by remember { mutableStateOf(UserSession.alias) }
    var showDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }
    var showResponseDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.username),
                            modifier = Modifier.clickable {
                                // Handle username update logic here
                            }
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = PrimaryGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.email))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.email),
                            modifier = Modifier.clickable {
                                // Handle email update logic here
                            }
                        )
                    }
                )
            }
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
                    TextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(text = stringResource(R.string.current_password), style = textFieldLabelTextStyle) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(text = stringResource(R.string.new_password), style = textFieldLabelTextStyle) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(text = stringResource(R.string.confirm_password), style = textFieldLabelTextStyle) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (newPassword != confirmPassword) {
                        Text(
                            text = stringResource(R.string.passwords_do_not_match),
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword) {
                            coroutineScope.launch {
                                val trimmedCurrentPassword = currentPassword.trim()
                                val trimmedNewPassword = newPassword.trim()
                                val trimmedConfirmPassword = confirmPassword.trim()
                                val (statusCode, message) = changePassword(UserSession.token, trimmedCurrentPassword, trimmedNewPassword, trimmedConfirmPassword)
                                responseMessage = message
                                showResponseDialog = true
                                if (statusCode == 200) {
                                    showDialog = false
                                }
                            }
                        }
                    },
                    enabled = newPassword == confirmPassword
                ) {
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
    var users by remember { mutableStateOf<List<OtherUsers>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                users = fetchAllUsers(token)
            } catch (e: Exception) {
                Log.e("AddNewType", "Error fetching users", e)
            }
        }
    }

    val filteredUsers = users.filter {
        (it.name?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (it.alias?.contains(searchQuery, ignoreCase = true) ?: false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                if (imageBitmap != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(64.dp)
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
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    com.example.hopla.universalData.SearchBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredUsers) { user ->
                            UserItemComposable(user, navController)
                        }
                    }
                }
            }
        }
    }
}