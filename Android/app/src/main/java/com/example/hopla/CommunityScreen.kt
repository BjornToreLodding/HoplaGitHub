package com.example.hopla

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hopla.ui.theme.HoplaTheme
import androidx.navigation.NavController

data class CommunityGroup(
    val image: Painter,
    val name: String,
    val description: String
)

@Composable
fun CommunityScreen(navController: NavController) {
    val groups = listOf(
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "Horse community",
            "A modern stable with excellent facilities."
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg2),
            "Ponny community",
            "SANDNES OG JÆREN RIDEKLUBB"
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "Donkey community",
            "BÆRUM RIDEKLUBB"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopTextCommunity()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            items(groups) { group ->
                CommunityCard(group, navController)
            }
        }
    }
}

@Composable
fun CommunityCard(group: CommunityGroup, navController: NavController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("communityDetail/${group.name}") }
    ) {
        Column {
            Image(
                painter = group.image,
                contentDescription = group.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Text(
                text = group.name,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun TopTextCommunity() {
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { /* Handle click action */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.position),
                    fontSize = 10.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clickable { /* Handle click action */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.liked),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun CommunityDetailScreen(navController: NavController, communityGroup: CommunityGroup) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Text(
                    text = communityGroup.name,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = communityGroup.description,
            fontSize = 16.sp
        )
    }
}

@Composable
// Function to retrieve the CommunityGroup object based on the communityName
fun getCommunityGroupByName(name: String): CommunityGroup? {
    val groups = listOf(
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "This is the first group.",
            "A modern stable with excellent facilities."
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg2),
            "This is the second group.",
            "SANDNES OG JÆREN RIDEKLUBB"
        ),
        CommunityGroup(
            painterResource(R.drawable.stockimg1),
            "This is the third group.",
            "BÆRUM RIDEKLUBB"
        )
    )
    return groups.find { it.name == name }
}

