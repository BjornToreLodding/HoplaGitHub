package com.example.hopla

import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hopla.ui.theme.ThemeViewModel


@Composable
fun SettingsScreen(languageViewModel: LanguageViewModel,
                   themeViewModel: ThemeViewModel,
                   userViewModel: UserViewModel) {
    val language = languageViewModel.selectedLanguage.value
    Text(text = if (language == "Norwegian") "Innstillinger" else "Settings")
    Column {
        LanguageSelection(languageViewModel)
        ModeSelection(languageViewModel, themeViewModel)
        Button(onClick = {
            userViewModel.logOut()
        }) {
            Text(text = if (language == "Norwegian") "Logg ut" else "Log out")
        }
        Button(onClick = {
            userViewModel.deleteUser()
        }) {
            Text(text = if (language == "Norwegian") "Slett bruker" else "Delete user")
        }
    }
}

class LanguageViewModel : ViewModel() {
    private val _selectedLanguage = mutableStateOf("Norwegian")
    val selectedLanguage: State<String> = _selectedLanguage

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }
}

@Composable
fun LanguageSelection(languageViewModel: LanguageViewModel) {
    val language = languageViewModel.selectedLanguage.value
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = if (language == "Norwegian") "Språk" else "Language")
            Text(
                text = "Norsk",
                modifier = Modifier
                    .padding(end = 8.dp, start = 8.dp)
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
    val language = languageViewModel.selectedLanguage.value
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = if (language == "Norwegian") "Modus" else "Mode")
            Button(onClick = { themeViewModel.setDarkTheme(false) }) {
                Text(text = if (language == "Norwegian") "Lys" else "Light")
            }
            Button(onClick = { themeViewModel.setDarkTheme(true) }) {
                Text(text = if (language == "Norwegian") "Mørk" else "Dark")
            }
        }
    }
}


