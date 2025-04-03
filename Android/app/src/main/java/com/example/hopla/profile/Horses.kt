package com.example.hopla.profile

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.deleteHorse
import com.example.hopla.apiService.fetchHorseDetails
import com.example.hopla.apiService.fetchHorses
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.ui.theme.generalTextStyleRed
import com.example.hopla.ui.theme.headerTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.AddButton
import com.example.hopla.universalData.Horse
import com.example.hopla.universalData.HorseDetail
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.launch

@Composable
fun UserHorsesScreen(navController: NavController, userId: String) {
    var horses by remember { mutableStateOf<List<Horse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                horses = fetchHorses(userId, token)
            } catch (e: Exception) {
                Log.e("UserHorsesScreen", "Error fetching horses", e)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(8.dp)) {
            ScreenHeader(navController, stringResource(R.string.horses))
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(horses) { horse ->
                HorseItem(horse, navController)
            }
        }
    }
}

@Composable
fun MyHorsesScreen(navController: NavController) {
    var horses by remember { mutableStateOf<List<Horse>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            horses = fetchHorses(UserSession.userId, UserSession.token)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(8.dp)) {
            ScreenHeader(navController, stringResource(R.string.my_horses))
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(horses) { horse ->
                HorseItem(horse, navController)
            }
        }
    }
    AddButton(onClick = { navController.navigate("addHorseScreen") })
}

@Composable
fun HorseDetailScreen(navController: NavController, horseId: String) {
    var horseDetail by remember { mutableStateOf<HorseDetail?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(horseId) {
        coroutineScope.launch {
            try {
                horseDetail = fetchHorseDetails(horseId, UserSession.token)
            } catch (e: Exception) {
                Log.e("HorseDetailScreen", "Error fetching horse details", e)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        horseDetail?.let { horse ->
            val formattedDob = try {
                val dob = horse.dob
                "${dob.day}.${dob.month}.${dob.year}"
            } catch (e: Exception) {
                Log.e("HorseDetailScreen", "Error formatting date of birth", e)
                "Unknown"
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                item {
                    // Name
                    Text(
                        text = horse.name,
                        style = headerTextStyle,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Image with Border & Shadow
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                            .shadow(8.dp, CircleShape)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = horse.horsePictureUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card with Details
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground), // Set the desired color
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DetailRow(label = stringResource(R.string.breed) + ":", value = horse.breed)
                            DetailRow(
                                label = stringResource(R.string.age) + ":",
                                value = horse.age.toString()
                            )
                            DetailRow(
                                label = stringResource(R.string.date_of_birth) + ":",
                                value = formattedDob
                            )
                        }
                    }

                    // Delete Horse Text
                    Text(
                        text = stringResource(R.string.delete_horse),
                        style = generalTextStyleRed,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable {
                                showDialog = true
                            }
                    )
                }
            }
        }

        // Close Button (Top-Right)
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.delete_horse)) },
            text = { Text(text = stringResource(R.string.delete_horse_dialogue)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                deleteHorse(UserSession.token, horseId)
                                navController.popBackStack() // Navigate back after deletion
                            } catch (e: Exception) {
                                Log.e("HorseDetailScreen", "Error deleting horse", e)
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }
}

@Composable
fun HorseItem(horse: Horse, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
            .clickable {
                navController.navigate("horse_detail/${horse.id}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = horse.horsePictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = horse.name, style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = generalTextStyleBold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(text = value, style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
    }
}