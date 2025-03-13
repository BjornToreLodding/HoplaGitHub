package com.example.hopla.profile

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hopla.R
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.UserViewModel
import com.example.hopla.ui.theme.ThemeViewModel
import java.util.Locale

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

    private val _selectedLanguage =
        mutableStateOf(savedStateHandle.get<String>("language") ?: "Norwegian")
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