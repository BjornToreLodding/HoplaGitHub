package com.example.hopla.profile

//noinspection UsingMaterialAndMaterial3Libraries
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.fetchAllUsers
import com.example.hopla.apiService.uploadProfilePicture
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryGray
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.OtherUsers
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.launch
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
    var phone by remember { mutableStateOf(UserSession.telephone?.toString() ?: "") }
    var name by remember { mutableStateOf(UserSession.name) }
    var description by remember { mutableStateOf(UserSession.description) }
    var dob by remember { mutableStateOf(UserSession.dob) }
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

            // Reusable TextField with Save Icon
            @Composable
            fun EditableTextField(label: String, value: String, onValueChange: (String) -> Unit, onSave: () -> Unit) {
                Column {
                    Text(text = label)
                    TextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.save),
                                modifier = Modifier.clickable { onSave() }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 2.dp, color = PrimaryGray)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Username
            EditableTextField(
                label = stringResource(R.string.username),
                value = username,
                onValueChange = { username = it },
                onSave = { UserSession.alias = username }
            )

            // Email
            EditableTextField(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = { email = it },
                onSave = { UserSession.email = email }
            )

            // Phone (Handled as a nullable integer)
            EditableTextField(
                label = stringResource(R.string.phone),
                value = phone,
                onValueChange = { phone = it },
                onSave = { UserSession.telephone = phone.toIntOrNull() }
            )

            // Name
            EditableTextField(
                label = stringResource(R.string.name),
                value = name,
                onValueChange = { name = it },
                onSave = { UserSession.name = name }
            )

            // Description
            EditableTextField(
                label = stringResource(R.string.description),
                value = description,
                onValueChange = { description = it },
                onSave = { UserSession.description = description }
            )

            // Date of Birth
            EditableTextField(
                label = stringResource(R.string.date_of_birth),
                value = dob ?: "",
                onValueChange = { dob = it },
                onSave = { UserSession.dob = dob }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Response Dialog
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