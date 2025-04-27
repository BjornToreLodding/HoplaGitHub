package com.example.hopla.trails

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.addFavoriteTrail
import com.example.hopla.apiService.fetchTrailUpdates
import com.example.hopla.apiService.postTrailReview
import com.example.hopla.apiService.rateTrail
import com.example.hopla.apiService.removeFavoriteTrail
import com.example.hopla.ui.theme.HeartColor
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.ui.theme.StarColor
import com.example.hopla.ui.theme.buttonTextStyle
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleSmall
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.universalData.ContentBoxInfo
import com.example.hopla.universalData.ImagePicker
import com.example.hopla.universalData.TrailRatingRequest
import com.example.hopla.universalData.TrailUpdate
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: (Bitmap?, String) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.post_review),
                    style = headerTextStyleSmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.no_image_selected),
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ImagePicker(
                    onImageSelected = { bitmap ->
                        imageBitmap = bitmap
                    },
                    text = stringResource(R.string.select_image)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .verticalScroll(scrollState)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss, shape = RectangleShape) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(onClick = {
                        onConfirm(imageBitmap, message) // Pass null if no image is selected
                    }, shape = RectangleShape) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

// Function to display the trail that have been clicked
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RouteClicked(navController: NavController, contentBoxInfo: ContentBoxInfo, onBackClick: () -> Unit) {
    val currentImageIndex by remember { mutableIntStateOf(0) }
    var userRating by remember { mutableIntStateOf(0) }
    var showMessageBox by remember { mutableStateOf(false) }
    var trailUpdates by remember { mutableStateOf<List<TrailUpdate>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token
    var showGiveReview by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(contentBoxInfo.isFavorite) }

    val images = contentBoxInfo.imageResource.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Inner box header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Row in header to display items next to each other
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back button that takes the user back to the main page of trails
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    // Text in the header
                    Text(
                        text = contentBoxInfo.title,
                        style = headerTextStyleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }

        // Scrollable content starts here
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            item {
                // Pictures + description box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                ) {
                    // Column for the picture
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
                                .height(190.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                        ) {
                            // Display the images
                            val painter = when (val imageResource = images[currentImageIndex]) {
                                is String -> rememberAsyncImagePainter(model = imageResource)
                                is Int -> painterResource(id = imageResource)
                                else -> painterResource(id = R.drawable.stockimg1)
                            }

                            Image(
                                painter = painter,
                                contentDescription = "Route Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Add like/dislike button in the top-right corner
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            if (isFavorite) {
                                                removeFavoriteTrail(token, contentBoxInfo.id)
                                            } else {
                                                addFavoriteTrail(token, contentBoxInfo.id)
                                            }
                                            isFavorite = !isFavorite
                                        } catch (e: Exception) {
                                            Log.e("RouteClicked", "Error updating favorite status", e)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFavorite) HeartColor else PrimaryWhite
                                )
                            }
                        }
                        // Description below pictures
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                contentBoxInfo.filters.forEach { filter ->
                                    Text(
                                        text = filter,
                                        style = generalTextStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Row for start trip and new updates boxes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Start trip clickable box
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.3f)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { navController.navigate("start_trip_map/${contentBoxInfo.id}") }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ride_trail),
                            style = buttonTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // New updates clickable box
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.2f)
                            .fillMaxWidth(0.7f)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { showGiveReview = true }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.new_updates),
                            style = buttonTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            }

            item {
                // Description Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = contentBoxInfo.description,
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    // Assessment box with star rating set
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.assessment),
                                style = generalTextStyle,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < contentBoxInfo.starRating) Icons.Filled.Star else Icons.TwoTone.Star,
                                        contentDescription = null,
                                        tint = StarColor
                                    )
                                }
                            }
                        }
                    }
                    // My assessment box wih star rating, changeable
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.give_assessment),
                                style = generalTextStyle,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            StarRating(
                                trailId = contentBoxInfo.id,
                                rating = userRating,
                                onRatingChanged = { userRating = it })
                        }
                    }
                    // Outer box of latest update
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner, clickable box for latest update
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    showMessageBox = true
                                    coroutineScope.launch {
                                        trailUpdates =
                                            fetchTrailUpdates(contentBoxInfo.id, 1, token)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.latest_update_about_the_route),
                                style = generalTextStyle,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMessageBox) {
        TrailUpdates(
            showMessageBox = showMessageBox,
            trailUpdates,
            onDismissRequest = { showMessageBox = false })
    }

    if (showGiveReview) {
        ReviewDialog(
            onDismiss = { showGiveReview = false },
            onConfirm = { image, message ->
                coroutineScope.launch {
                    try {
                        val response = postTrailReview(token, image, contentBoxInfo.id, message)
                        Log.d("postTrailReview", "Response: $response")
                        showGiveReview = false
                    } catch (e: Exception) {
                        Log.e("postTrailReview", "Error posting review", e)
                    }
                }
            }
        )
    }
}

@Composable
private fun TrailUpdates(
    showMessageBox: Boolean,
    trailUpdates: List<TrailUpdate>,
    onDismissRequest: () -> Unit
) {
    if (showMessageBox) {
        Dialog(onDismissRequest = onDismissRequest) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.updates2),
                        style = headerTextStyleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    // Group updates by date
                    val groupedUpdates = trailUpdates.groupBy { update ->
                        val updateDate = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).parse(update.createdAt)
                        val today = Calendar.getInstance()
                        val yesterday =
                            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

                        when {
                            updateDate != null && updateDate.toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate() == today.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() -> stringResource(R.string.today)

                            updateDate != null && updateDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() == yesterday.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() -> stringResource(R.string.yesterday)

                            else -> SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                                updateDate ?: ""
                            )
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        groupedUpdates.forEach { (date, updates) ->
                            item {
                                // Display the date header
                                Text(
                                    text = date,
                                    style = generalTextStyleSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                            items(updates) { update ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = update.comment,
                                            style = generalTextStyle,
                                            color = MaterialTheme.colorScheme.secondary,
                                        )

                                        if (update.pictureUrl != "https://hopla.imgix.net/main-review.jpg?w=400&h=300&fit=crop") {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Image(
                                                painter = rememberAsyncImagePainter(model = update.pictureUrl),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "User: ${update.alias}",
                                                style = generalTextStyleSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = SimpleDateFormat(
                                                    "HH:mm",
                                                    Locale.getDefault()
                                                ).format(
                                                    SimpleDateFormat(
                                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                                        Locale.getDefault()
                                                    ).parse(update.createdAt) ?: ""
                                                ),
                                                style = generalTextStyleSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Function to be able to change the rating
@Composable
fun StarRating(trailId: String, rating: Int, onRatingChanged: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.TwoTone.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        val newRating = index + 1
                        onRatingChanged(newRating)
                        coroutineScope.launch {
                            try {
                                val response = rateTrail(
                                    token,
                                    TrailRatingRequest(TrailId = trailId, Rating = newRating)
                                )
                                Log.d("rateTrail", "Response: ${response.message}")
                            } catch (e: Exception) {
                                Log.e("rateTrail", "Error rating trail", e)
                            }
                        }
                    }
            )
        }
    }
}

// Function to display the update screen where user can add their own update about the route
@Composable
fun UpdateScreen(navController: NavController) {
    val location by remember { mutableStateOf("Boredalstien") }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE6DD)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Box (Title + Back Button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB8A999))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = stringResource(R.string.new_updates),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Location Field (Read-only)
        TextField(
            value = location,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comment Box with Floating Add Button
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(150.dp)
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.comment), color = Color.Gray)
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            FloatingActionButton(
                onClick = { /* Handle Add Action */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(40.dp),
                containerColor = Color(0xFFD9CFC4)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Comment")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
            onClick = {
                comment = "" // Clear the comment box
                navController.popBackStack() // Navigate back to the previous screen
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9CFC4))
        ) {
            Text(text = stringResource(R.string.publish), color = Color.Gray)
        }
    }
}