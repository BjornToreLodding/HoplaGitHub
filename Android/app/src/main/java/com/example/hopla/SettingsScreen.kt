package com.example.hopla

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hopla.ui.theme.ThemeViewModel
import java.util.Locale


@Composable
fun SettingsScreen(languageViewModel: LanguageViewModel,
                   themeViewModel: ThemeViewModel,
                   userViewModel: UserViewModel) {
    Text(text = stringResource(R.string.settings))
    Column {
        LanguageSelection(languageViewModel)
        ModeSelection(languageViewModel, themeViewModel)
        Button(onClick = {
            userViewModel.logOut()
        }) {
            Text(text = stringResource(R.string.log_out))
        }
        Button(onClick = {
            userViewModel.deleteUser()
        }) {
            Text(text = stringResource(R.string.delete_user))
        }
    }
}

class LanguageViewModel(
    private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedLanguage = mutableStateOf(savedStateHandle.get<String>("language") ?: "Norwegian")
    val selectedLanguage: State<String> = _selectedLanguage

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        savedStateHandle["language"] = language
        setLocale(context, if (language == "Norwegian") "no" else "en")
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
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
                    .padding(horizontal = 8.dp)
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


