package com.example.hopla.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hopla.R
import com.example.hopla.apiService.registerUser
import com.example.hopla.apiService.resetPassword
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleDialog
import com.example.hopla.ui.theme.textFieldLabelTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.ServerErrorDialog
import kotlinx.coroutines.launch

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
                Text(
                    text = errorMessage,
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
                Button(onClick = onDismiss,
                    modifier = Modifier
                        .padding(top = 16.dp),
                    shape = RectangleShape
                ) {
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
                    Text(
                        text = stringResource(R.string.forgotten_password_description),
                        style = generalTextStyleDialog,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                text = stringResource(R.string.email),
                                style = textFieldLabelTextStyle,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
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

// Dialog for showing response message for forgotten password
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
                Text(
                    text = message,
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
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
                        label = {
                            Text(
                                text = stringResource(R.string.email),
                                style = textFieldLabelTextStyle,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        singleLine = true,
                        isError = showError && email.isEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                text = stringResource(R.string.password),
                                style = textFieldLabelTextStyle,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Default.Lock else Icons.Default.Lock
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
                        label = {
                            Text(
                                text = stringResource(R.string.confirm_password),
                                style = textFieldLabelTextStyle,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        visualTransformation = if (confirmpasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            val image =
                                if (confirmpasswordVisible) Icons.Default.Lock else Icons.Default.Lock
                            IconButton(onClick = {
                                confirmpasswordVisible = !confirmpasswordVisible
                            }) {
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
                        Text(
                            text = stringResource(R.string.allow_usage),
                            style = generalTextStyleDialog,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        IconButton(onClick = { showInfoDialogCU = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.secondary
                            )
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
                                        val (result, code) = registerUser(
                                            trimmedEmail,
                                            trimmedPassword
                                        )
                                        when (code) {
                                            200 -> {
                                                responseMessage = result
                                                showSuccessDialog = true
                                                onCreateUser(trimmedEmail, trimmedPassword)
                                            }
                                            else -> {
                                                showServerErrorDialog = true
                                            }
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

// Dialog for showing information about statistics
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
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.secondary
                        )
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
                Text(
                    text = message,
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
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
