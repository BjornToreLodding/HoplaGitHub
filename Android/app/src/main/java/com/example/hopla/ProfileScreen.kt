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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hopla.ui.theme.ThemeViewModel
import java.util.Locale
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.background
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun ProfileScreen(
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { navController.navigate("settings") },
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture()
            ProfileButtons()
            UserChanges(modifier = Modifier.weight(1f))
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }

        Text(text = stringResource(R.string.settings))
        LanguageSelection(languageViewModel)
        ModeSelection(languageViewModel, themeViewModel)
        Button(onClick = { userViewModel.logOut() }) {
            Text(text = stringResource(R.string.log_out))
        }
        Button(onClick = { userViewModel.deleteUser() }) {
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
            Text(text = stringResource(R.string.mode))
            Button(onClick = { themeViewModel.setDarkTheme(false) }) {
                Text(text = if (language == "Norwegian") "Lys" else "Light")
            }
            Button(onClick = { themeViewModel.setDarkTheme(true) }) {
                Text(text = if (language == "Norwegian") "Mørk" else "Dark")
            }
        }
    }
}

@Composable
fun ProfilePicture(imageResource: Int = R.drawable.logo2) {
    Image(
        painter = painterResource(id = imageResource),
        contentDescription = null,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .border(10.dp, Color.Black, CircleShape)
    )
    Text(
        text = stringResource(R.string.change_profile_picture),
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable { /* TODO */ },
        style = TextStyle(textDecoration = TextDecoration.Underline)
    )
}

@Composable
fun ProfileButtons() {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = stringResource(R.string.my_trips))
        }
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = stringResource(R.string.friends))
        }
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .height(50.dp),
            shape = RectangleShape
        ) {
            Text(text = stringResource(R.string.following))
        }
    }
}

@Composable
fun UserChanges(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Gray)
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Black, thickness = 1.dp)
            Text(text = stringResource(R.string.username))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = stringResource(R.string.username)) },
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Color.Black, thickness = 1.dp)
            Text(text = stringResource(R.string.email))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Color.Black, thickness = 1.dp)
            Text(
                text = stringResource(R.string.change_password),
                modifier = Modifier.clickable { /* TODO */ },
                style = TextStyle(textDecoration = TextDecoration.Underline) )
        }
    }
}
