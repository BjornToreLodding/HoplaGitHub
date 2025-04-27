package com.example.hopla.profile

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hopla.R
import com.example.hopla.apiService.fetchUserRelationRequests
import com.example.hopla.apiService.relationRequestDelete
import com.example.hopla.apiService.sendUserRelationRequestPut
import com.example.hopla.ui.theme.AcceptColor
import com.example.hopla.ui.theme.HeartColor
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.generalTextStyleBold
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.ui.theme.underheaderTextStyle
import com.example.hopla.universalData.UserRelationChangeRequest
import com.example.hopla.universalData.UserRelationRequest
import com.example.hopla.universalData.UserSession
import kotlinx.coroutines.launch

// This screen displays the notifications for friend requests
@Composable
fun NotificationsScreen(navController: NavController) {
    var userRelationRequests by remember { mutableStateOf<List<UserRelationRequest>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val token = UserSession.token

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                userRelationRequests = fetchUserRelationRequests(token)
            } catch (e: Exception) {
                Log.e("NotificationsScreen", "Error fetching user relation requests", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.friend_requests),
            style = headerTextStyleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (userRelationRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_friend_requests), style = underheaderTextStyle)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(userRelationRequests) { request ->
                    NotificationItem(request, navController) {
                        coroutineScope.launch {
                            try {
                                userRelationRequests = fetchUserRelationRequests(token)
                            } catch (e: Exception) {
                                Log.e("NotificationsScreen", "Error fetching user requests", e)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Each of the notification items is displayed here
@Composable
fun NotificationItem(
    request: UserRelationRequest,
    navController: NavController,
    onReload: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .clickable {
                navController.navigate("friend_profile/${request.fromUserId}")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Avatar",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${request.fromUserName} (@${request.fromUserAlias})",
                    style = generalTextStyleBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.sent_a_friend_request),
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        val changeRequest = UserRelationChangeRequest(
                            targetUserId = request.fromUserId,
                            status = "FRIENDS"
                        )
                        coroutineScope.launch {
                            try {
                                val response = sendUserRelationRequestPut(UserSession.token, changeRequest)
                                Log.d("changeRelations", "Response: ${response.message}")
                                onReload()
                            } catch (e: Exception) {
                                Log.e("changeRelations", "Error accepting friend request", e)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = AcceptColor
                    )
                }
                IconButton(
                    onClick = {
                        val deleteRequest = UserRelationChangeRequest(
                            targetUserId = request.fromUserId
                        )
                        coroutineScope.launch {
                            try {
                                val response = relationRequestDelete(UserSession.token, deleteRequest)
                                Log.d("changeRelations", "Response: $response")
                                onReload()
                            } catch (e: Exception) {
                                Log.e("changeRelations", "Error declining request", e)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = HeartColor
                    )
                }
            }
        }
    }
}
