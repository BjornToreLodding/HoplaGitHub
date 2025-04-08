package com.example.hopla

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hopla.apiService.handleLogin
import com.example.hopla.apiService.registerUser
import com.example.hopla.apiService.resetPassword
import com.example.hopla.ui.theme.ThemeViewModel
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleDialog
import com.example.hopla.ui.theme.headerTextStyle
import com.example.hopla.ui.theme.textFieldLabelTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.ui.theme.underlinedTextStyleSmall
import com.example.hopla.universalData.ServerErrorDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, onLogin: () -> Unit, onCreateUser: () -> Unit, themeViewModel: ThemeViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showCreateUserDialogue by remember { mutableStateOf(false) }
    var showForgottenPasswordDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isCheckingLoginState by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000) // Add a delay to simulate loading time
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")
            if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                handleLogin(
                    username = savedUsername,
                    password = savedPassword,
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
            }
        }
        isCheckingLoginState = false
    }

    val isDarkTheme by themeViewModel.isDarkTheme.observeAsState(false)
    val logoResource = if (isDarkTheme) R.drawable.logo_white else R.drawable.logo1

    if (isCheckingLoginState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = logoResource),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxHeight(0.3f)
            )
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    } else {
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
                painter = painterResource(id = logoResource),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxHeight(0.3f)
            )
            Text(
                text = "Hopla",
                style = headerTextStyle,
                color = MaterialTheme.colorScheme.secondary
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
            var passwordVisible by remember { mutableStateOf(false) }

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(R.string.password), style = textFieldLabelTextStyle) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Lock else Icons.Default.Lock
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    passwordVisible = true
                                    tryAwaitRelease()
                                    passwordVisible = false
                                }
                            )
                        }
                    ) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )
            Text(
                text = stringResource(R.string.forgot_password),
                style = underlinedTextStyleSmall,
                color = MaterialTheme.colorScheme.secondary,
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
                                saveLoginState(context, username, password) // Save login state
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
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    shape = RectangleShape
                ) {
                    Text(
                        text = stringResource(R.string.log_in),
                        style = buttonTextStyle
                    )
                }
            }

            Text(
                text = stringResource(R.string.create_user),
                style = underlinedTextStyleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { showCreateUserDialogue = true }
            )
        }

        if (showCreateUserDialogue) {
            CreateUserDialog(
                onDismiss = { showCreateUserDialogue = false },
                onCreateUser = { _, password ->
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
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = errorMessage, style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
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
    var showResponseDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val errorOccurredMessage = stringResource(R.string.error_occurred)

    if (showInfoDialog) {
        InfoDialog(onDismiss = {
            showInfoDialog = false
            onDismiss()
        })
    } else if (showResponseDialog) {
        ResponseDialog(message = responseMessage, onDismiss = {
            showResponseDialog = false
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
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(text = stringResource(R.string.forgotten_password_description), style = generalTextStyleDialog, color = MaterialTheme.colorScheme.secondary)
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = stringResource(R.string.email), style = textFieldLabelTextStyle, color = MaterialTheme.colorScheme.secondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = onDismiss, shape = RectangleShape) {
                                Text(
                                    text = stringResource(R.string.cancel),
                                    style = buttonTextStyle,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Button(onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val response = resetPassword(email)
                                        responseMessage = response
                                        showResponseDialog = true
                                    } catch (e: Exception) {
                                        errorMessage = e.message ?: errorOccurredMessage
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }, shape = RectangleShape) {
                                Text(
                                    text = stringResource(R.string.send),
                                    style = buttonTextStyle,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResponseDialog(message: String, onDismiss: () -> Unit) {
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
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = message, style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
                Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = stringResource(R.string.close),
                        style = buttonTextStyle
                    )
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
                    color = MaterialTheme.colorScheme.secondary,
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
    var isChecked by remember { mutableStateOf(false) }
    var showInfoDialogCU by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmpasswordVisible by remember { mutableStateOf(false) }
    var showServerErrorDialog by remember { mutableStateOf(false) }

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
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = stringResource(R.string.email), style = textFieldLabelTextStyle, color = MaterialTheme.colorScheme.secondary )},
                        singleLine = true,
                        isError = showError && email.isEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = stringResource(R.string.password), style = textFieldLabelTextStyle, color = MaterialTheme.colorScheme.secondary) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Lock else Icons.Default.Lock
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        isError = showError && password.isEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    TextField(
                        value = confirmedPassword,
                        onValueChange = { confirmedPassword = it },
                        label = { Text(text = stringResource(R.string.confirm_password), style = textFieldLabelTextStyle, color = MaterialTheme.colorScheme.secondary )},
                        visualTransformation = if (confirmpasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            val image = if (confirmpasswordVisible) Icons.Default.Lock else Icons.Default.Lock
                            IconButton(onClick = { confirmpasswordVisible = !confirmpasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        isError = showError && password.isEmpty(),
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it }
                        )
                        Text(text = stringResource(R.string.allow_usage), style = generalTextStyleDialog, color = MaterialTheme.colorScheme.secondary)
                        IconButton(onClick = { showInfoDialogCU = true }) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onDismiss, shape = RectangleShape) {
                            Text(
                                text = stringResource(R.string.cancel),
                                style = buttonTextStyle,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Button(
                            onClick = {
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
                                            onCreateUser(trimmedEmail, trimmedPassword)
                                        } else if (code == 500) {
                                            showServerErrorDialog = true
                                        } else {
                                            responseMessage = result
                                        }
                                    }
                                }
                            },
                            shape = RectangleShape,
                            enabled = isChecked
                        ) {
                            Text(
                                text = stringResource(R.string.create_user),
                                style = buttonTextStyle,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showServerErrorDialog) {
        ServerErrorDialog(onDismiss = { showServerErrorDialog = false })
    }

    if (showInfoDialogCU) {
        InfoDialogCU(onDismiss = { showInfoDialogCU = false })
    }
}

@Composable
fun InfoDialogCU(onDismiss: () -> Unit) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
                Text(
                    text = stringResource(R.string.statistics_info),
                    style = generalTextStyleDialog,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = message, style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
                Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = stringResource(R.string.ok),
                        style = buttonTextStyle,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

fun saveLoginState(context: Context, username: String, password: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("username", username)
    editor.putString("password", password)
    editor.putBoolean("isLoggedIn", true)
    editor.apply()
}

@Composable
fun CheckLoginState(navController: NavController, onLogin: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Log.d("LoginState", "Checking login state")
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", true)
        Log.d("LoginState", "isLoggedIn: $isLoggedIn")
        if (isLoggedIn) {
            val username = sharedPreferences.getString("username", "")
            val password = sharedPreferences.getString("password", "")
            Log.d("LoginState", "username: $username, password: $password")
            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                handleLogin(
                    username = username,
                    password = password,
                    context = context,
                    coroutineScope = coroutineScope,
                    onLogin = {
                        onLogin()
                        navController.navigate("profile")
                    },
                    setErrorMessage = { errorMessage ->
                        Log.e("LoginState", "Login error: $errorMessage")
                    },
                    setShowErrorDialog = { showErrorDialog ->
                        Log.e("LoginState", "Show error dialog: $showErrorDialog")
                    },
                    setIsLoading = { isLoading ->
                        Log.d("LoginState", "Loading state: $isLoading")
                    }
                )
            }
        }
    }
}
