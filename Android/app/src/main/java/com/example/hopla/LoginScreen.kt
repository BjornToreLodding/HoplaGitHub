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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.hopla.apiService.handleLogin
import com.example.hopla.apiService.registerUser
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyle
import com.example.hopla.ui.theme.textFieldLabelTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.ui.theme.underlinedTextStyleBig
import com.example.hopla.ui.theme.underlinedTextStyleSmall
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, onLogin: () -> Unit, onCreateUser: () -> Unit) {
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
                    onLogin = {
                        onLogin()
                        navController.navigate("profile")
                    },
                    setErrorMessage = { errorMessage = it },
                    setShowErrorDialog = { showErrorDialog = it },
                    setIsLoading = { isLoading = it }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Auto-Fill and Login",
                style = buttonTextStyle
            )
        }

        Image(
            painter = painterResource(id = R.drawable.logo1),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxHeight(0.3f)
        )
        Text(
            text = "Hopla",
            style = headerTextStyle
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = stringResource(R.string.email), style = textFieldLabelTextStyle) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = stringResource(R.string.password), style = textFieldLabelTextStyle) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Text(
            text = stringResource(R.string.forgot_password),
            style = underlinedTextStyleSmall,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { showForgottenPasswordDialog = true }
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
                        onLogin = {
                            onLogin()
                            navController.navigate("profile")
                        },
                        setErrorMessage = { errorMessage = it },
                        setShowErrorDialog = { showErrorDialog = it },
                        setIsLoading = { isLoading = it }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.log_in),
                    style = buttonTextStyle
                )
            }
        }

        Text(
            text = stringResource(R.string.create_user),
            style = underlinedTextStyleBig,
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
            }
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
                    style = underheaderTextStyle,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = errorMessage)
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
                        style = underheaderTextStyle,
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
                            Text(
                                text = stringResource(R.string.cancel),
                                style = buttonTextStyle
                            )
                        }
                        Button(onClick = {
                            showInfoDialog = true
                        }) {
                            Text(
                                text = stringResource(R.string.send),
                                style = buttonTextStyle
                            )
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
                    style = generalTextStyle,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.close),
                        style = buttonTextStyle
                    )
                }
            }
        }
    }
}

// Dialog for creating a new user
@Composable
fun CreateUserDialog(onDismiss: () -> Unit, onCreateUser: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val allFieldsRequiredMessage = stringResource(R.string.all_fields_are_required)
    val passwordsDoNotMatchMessage = stringResource(R.string.passwords_do_not_match)
    val coroutineScope = rememberCoroutineScope()

    if (showSuccessDialog) {
        SuccessDialog(message = responseMessage, onDismiss = onDismiss)
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
                        text = stringResource(R.string.create_user),
                        style = underheaderTextStyle,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = stringResource(R.string.email), style = textFieldLabelTextStyle) },
                        singleLine = true,
                        isError = showError && email.isEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = stringResource(R.string.password), style = textFieldLabelTextStyle) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = showError && password.isEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    TextField(
                        value = confirmedPassword,
                        onValueChange = { confirmedPassword = it },
                        label = { Text(text = stringResource(R.string.confirm_password), style = textFieldLabelTextStyle) },
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
                    if (responseMessage.isNotEmpty()) {
                        Text(
                            text = responseMessage,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onDismiss) {
                            Text(
                                text = stringResource(R.string.cancel),
                                style = buttonTextStyle
                            )
                        }
                        Button(onClick = {
                            val trimmedEmail = email.trim()
                            val trimmedPassword = password.trim()
                            val trimmedConfirmedPassword = confirmedPassword.trim()

                            if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty() || trimmedConfirmedPassword.isEmpty()) {
                                errorMessage = allFieldsRequiredMessage
                                showError = true
                            } else if (trimmedPassword != trimmedConfirmedPassword) {
                                errorMessage = passwordsDoNotMatchMessage
                                showError = true
                            } else {
                                coroutineScope.launch {
                                    val (result, code) = registerUser(trimmedEmail, trimmedPassword)
                                    if (code == 200) {
                                        responseMessage = result
                                        showSuccessDialog = true
                                    } else {
                                        responseMessage = result
                                    }
                                }
                            }
                        }) {
                            Text(
                                text = stringResource(R.string.create_user),
                                style = buttonTextStyle
                            )
                        }
                    }
                }
            }
        }
    }
}

// Define the SuccessDialog composable
@Composable
fun SuccessDialog(message: String, onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.success),
                    style = underheaderTextStyle,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = message)
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