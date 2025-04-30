package com.example.hopla.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hopla.R
import com.example.hopla.apiService.fetchStableMessages
import com.example.hopla.apiService.joinStable
import com.example.hopla.apiService.leaveStable
import com.example.hopla.apiService.sendStableMessage
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.ui.theme.generalTextStyleSmall
import com.example.hopla.universalData.Message
import com.example.hopla.universalData.Stable
import com.example.hopla.universalData.StableActionRequest
import com.example.hopla.universalData.StableMessageRequest
import com.example.hopla.universalData.UserSession
import com.example.hopla.universalData.formatDateTime
import kotlinx.coroutines.launch

// Function to display a card for each stable
@Composable
fun StableCard(
    stable: Stable,
    navController: NavController,
    likedStables: MutableList<Stable>,
    token: String,
    contentDescriptionProvider: ((Boolean) -> String)? = null // Optional contentDescriptionProvider
) {
    var isLiked by remember { mutableStateOf(stable.member || likedStables.contains(stable)) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .clickable { navController.navigate("stableDetail/${stable.stableId}") }
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(stable.pictureUrl),
                contentDescription = stable.stableName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = stable.stableName,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isLiked) {
                                leaveStable(token, StableActionRequest(stable.stableId))
                                likedStables.remove(stable)
                            } else {
                                joinStable(token, StableActionRequest(stable.stableId))
                                likedStables.add(stable)
                            }
                            isLiked = !isLiked
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = contentDescriptionProvider?.invoke(isLiked)
                            ?: if (isLiked) stringResource(R.string.liked) else stringResource(R.string.not_liked),
                        tint = if (isLiked) colorResource(id = R.color.likedHeart) else Color.White,
                    )
                }
            }
        }
    }
}

// Top text for filtering the groups based on position and liked status
@Composable
fun TopTextCommunity(currentPage: String, onShowLikedOnlyChange: (String) -> Unit) {
    // Column for the top text
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(3.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Box for the position text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange("position") }
                    .background(if (currentPage == "position") colorResource(id = R.color.transparentWhite) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                // The position icon
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = stringResource(R.string.position),
                )
            }
            // Box for the liked text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { onShowLikedOnlyChange("liked") }
                    .background(if (currentPage == "liked") colorResource(id = R.color.transparentWhite) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                // The liked icon
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.liked),
                )
            }
        }
    }
}

// Function to display the message box
@Composable
fun MessageBox(stableId: String, token: String) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    var newMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Function to load messages
    fun loadMessages() {
        if (!loading) {
            loading = true
            coroutineScope.launch {
                val fetchedMessages = fetchStableMessages(token, stableId, pageNumber)
                if (fetchedMessages.isNotEmpty()) {
                    messages = messages + fetchedMessages
                    pageNumber += 1
                }
                loading = false
            }
        }
    }

    // Function to send a new message
    suspend fun sendMessage(content: String) {
        val stableMessageRequest = StableMessageRequest(
            stableId = stableId,
            content = content
        )
        sendStableMessage(token, stableMessageRequest)
        // Reload messages after sending
        messages = listOf()
        pageNumber = 1
        loadMessages()
    }

    // Initial load
    LaunchedEffect(Unit) {
        loadMessages()
    }

    // Group messages by date
    val groupedMessages = messages.groupBy { formatDateTime(it.timestamp).first }

    Column(modifier = Modifier.fillMaxSize()) {
        // LazyColumn to display messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true // To display the latest message at the top
        ) {
            // Loading indicator at the top
            item {
                if (loading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Display grouped messages
            groupedMessages.forEach { (date, messagesForDate) ->
                items(messagesForDate) { message ->
                    MessageCard(message)
                }
                item {
                    Text(
                        text = date,
                        style = generalTextStyleBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Load more messages when scrolled to the top
            item {
                LaunchedEffect(Unit) {
                    loadMessages()
                }
            }
        }

        // TextField and Button to send a new message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                singleLine = true,
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = stringResource(R.string.enter_you_message)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        coroutineScope.launch {
                            sendMessage(newMessage)
                            newMessage = ""
                        }
                    }
                },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp), // Remove default padding
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.publish),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// Function to display a message card
@Composable
fun MessageCard(message: Message) {
    val backgroundColor = if (message.senderId == UserSession.userId) {
        MaterialTheme.colorScheme.primary // Green color
    } else {
        MaterialTheme.colorScheme.onBackground // Default color
    }

    val displayAlias = if (message.senderId == UserSession.userId) {
        stringResource(R.string.me) // "Me"
    } else {
        message.senderAlias
    }

    val (_, formattedTime) = formatDateTime(message.timestamp)

    val alignment = if (message.senderId == UserSession.userId) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (message.senderId == UserSession.userId) Alignment.End else Alignment.Start
        ) {
            // Row for name and time
            Row(
                horizontalArrangement = if (message.senderId == UserSession.userId) Arrangement.End else Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formattedTime,
                    style = generalTextStyleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = displayAlias,
                    style = generalTextStyleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Message box
            Card(
                modifier = Modifier
                    .widthIn(
                        min = 100.dp,
                        max = (0.75f * LocalConfiguration.current.screenWidthDp).dp
                    )
                    .padding(top = 4.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Text(
                    text = message.content,
                    style = generalTextStyle.copy(
                        color = if (backgroundColor == MaterialTheme.colorScheme.onBackground) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            generalTextStyle.color
                        }
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
