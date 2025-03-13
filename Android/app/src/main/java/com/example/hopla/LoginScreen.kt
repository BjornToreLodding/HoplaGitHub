package com.example.hopla

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.hopla.ui.theme.PrimaryBlack
import com.example.hopla.ui.theme.PrimaryWhite
import androidx.compose.material3.CircularProgressIndicator
import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.hopla.apiService.handleLogin
import com.example.hopla.ui.theme.PrimaryGray

@Composable
fun LoginScreen(onLogin: () -> Unit, onCreateUser: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showCreateUserDialogue by remember { mutableStateOf(false) }
    var showForgottenPasswordDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // REMOVE BEFORE FINISHING -> auto login
        Button(
            onClick = {
                username = "test@test.no"
                password = "Hopla2025!"
                handleLogin(
                    username = username,
                    password = password,
                    context = context,
                    coroutineScope = coroutineScope,
                    onLogin = onLogin,
                    setErrorMessage = { errorMessage = it },
                    setShowErrorDialog = { showErrorDialog = it },
                    setIsLoading = { isLoading = it }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(text = "Auto-Fill and Login", color = PrimaryWhite)
        }

        Image(
            painter = painterResource(id = R.drawable.logo1),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxHeight(0.3f)
        )
        Text(
            text = "Hopla",
            style = TextStyle(
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = stringResource(R.string.email)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(R.string.password))},
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Text(
            text = stringResource(R.string.forgot_password),
            color = PrimaryBlack,
            textDecoration = TextDecoration.Underline,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { showForgottenPasswordDialog = true },
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Button(
                onClick = {
                    handleLogin(
                        username = username,
                        password = password,
                        context = context,
                        coroutineScope = coroutineScope,
                        onLogin = onLogin,
                        setErrorMessage = { errorMessage = it },
                        setShowErrorDialog = { showErrorDialog = it },
                        setIsLoading = { isLoading = it }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.log_in), color = PrimaryWhite)
            }
        }

        Text(
            text = stringResource(R.string.create_user),
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { showCreateUserDialogue = true }
        )
    }

    if (showCreateUserDialogue) {
        CreateUserDialog(
            onDismiss = { showCreateUserDialogue = false },
            onCreateUser = { email, password ->
                onCreateUser()
            },
            onLogin = onLogin
        )
    }

    if (showForgottenPasswordDialog) {
        ForgottenPasswordDialog(onDismiss = { showForgottenPasswordDialog = false })
    }

    if (showErrorDialog) {
        ErrorDialog(errorMessage = errorMessage, onDismiss = { showErrorDialog = false })
    }
}

// Dialog for showing error messages
@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
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
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = errorMessage)
                Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        }
    }
}

// Dialog for forgotten password
@Composable
fun ForgottenPasswordDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        InfoDialog(onDismiss = {
            showInfoDialog = false
            onDismiss()
        })
    } else {
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
                        text = stringResource(R.string.forgot_password),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(text = stringResource(R.string.forgotten_password_description))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = stringResource(R.string.email)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onDismiss) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        Button(onClick = {
                            showInfoDialog = true
                        }) {
                            Text(text = stringResource(R.string.send))
                        }
                    }
                }
            }
        }
    }
}

// Dialog for showing information about forgotten password
@Composable
fun InfoDialog(onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.verification_explanation),
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onDismiss) {
                    Text(text = "Close")
                }
            }
        }
    }
}

// Dialog for creating a new user
@Composable
fun CreateUserDialog(onDismiss: () -> Unit, onCreateUser: (String, String) -> Unit, onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showAdditionalUserInfoDialog by remember { mutableStateOf(false) }

    val allFieldsRequiredMessage = stringResource(R.string.all_fields_are_required)
    val passwordsDoNotMatchMessage = stringResource(R.string.passwords_do_not_match)

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
                    text = stringResource(R.string.create_user),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = stringResource(R.string.email)) },
                    singleLine = true,
                    isError = showError && email.isEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = showError && password.isEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = confirmedPassword,
                    onValueChange = { confirmedPassword = it },
                    label = { Text(text = stringResource(R.string.confirm_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = showError && (confirmedPassword.isEmpty() || password != confirmedPassword),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(onClick = {
                        if (email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
                            errorMessage = allFieldsRequiredMessage
                            showError = true
                        } else if (password != confirmedPassword) {
                            errorMessage = passwordsDoNotMatchMessage
                            showError = true
                        } else {
                            onCreateUser(email, password)
                            showAdditionalUserInfoDialog = true
                        }
                    }) {
                        Text(text = stringResource(R.string.create_user))
                    }
                }
            }
        }
    }

    if (showAdditionalUserInfoDialog) {
        AdditionalUserInfoDialog(onDismiss = { showAdditionalUserInfoDialog = false }, onConfirm = { alias, name, description, birthDate, phone, imageBitmap ->
            // Handle the additional user info and log in the user
            onLogin()
        })
    }
}

@Composable
fun AdditionalUserInfoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String?, Bitmap?) -> Unit
) {
    var alias by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val allFieldsRequiredMessage = stringResource(R.string.fill_inn_marked_fields)

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
                    text = stringResource(R.string.additional_user_info),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text(text = stringResource(R.string.alias)) },
                    singleLine = true,
                    isError = showError && alias.isEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(R.string.name)) },
                    singleLine = true,
                    isError = showError && name.isEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = stringResource(R.string.description)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text(text = stringResource(R.string.birth_date)) },
                    singleLine = true,
                    isError = showError && birthDate.isEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(text = stringResource(R.string.phone)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                // Image Picker Section
                ImagePicker(
                    onImageSelected = { bitmap -> imageBitmap = bitmap },
                    text = if (imageBitmap == null) stringResource(R.string.select_image) else stringResource(R.string.change_image)
                )

                imageBitmap?.let { bitmap ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.selected_image),
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(1.dp, PrimaryGray, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { imageBitmap = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.remove_image))
                        }
                    }
                }

                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(onClick = {
                        if (alias.isEmpty() || name.isEmpty() || birthDate.isEmpty()) {
                            errorMessage = allFieldsRequiredMessage
                            showError = true
                        } else {
                            onConfirm(alias, name, description, birthDate, phone, imageBitmap)
                            onDismiss()
                        }
                    }) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

