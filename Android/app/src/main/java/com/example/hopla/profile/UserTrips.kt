package com.example.hopla.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.fetchUserHikes
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.Hike
import com.example.hopla.universalData.ScreenHeader
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.launch

@Composable
fun MyTripsScreen(navController: NavController) {
    var userHikes by remember { mutableStateOf<List<Hike>>(emptyList()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(pageNumber) {
        coroutineScope.launch {
            try {
                val newHikes = fetchUserHikes(token, pageNumber)
                userHikes = userHikes + newHikes
            } catch (e: Exception) {
                Log.e("UserHikesScreen", "Error fetching user hikes", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ScreenHeader(navController, stringResource(R.string.my_trips))

            if (userHikes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userHikes) { hike ->
                        HikeItem(hike)
                    }
                    item {
                        Button(
                            onClick = { pageNumber++ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = stringResource(R.string.load_more), style = buttonTextStyle, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HikeItem(hike: Hike) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
            .clickable {
                // Handle item click
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = hike.pictureUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = hike.trailName, style = underheaderTextStyle, color = MaterialTheme.colorScheme.secondary)
            Text(text = stringResource(R.string.length)+": ${hike.length} km", style = generalTextStyle, color = MaterialTheme.colorScheme.secondary)
            Text(
                text = stringResource(R.string.duration) +": ${hike.duration} min",
                style = generalTextStyle,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}