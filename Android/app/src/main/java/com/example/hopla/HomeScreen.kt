package com.example.hopla

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.colorResource
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.Image
import com.example.hopla.ui.theme.PrimaryBlack
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.hopla.ui.theme.PrimaryWhite
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.universalData.ReportDialog
import com.example.hopla.universalData.UserSession

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopTextColumn()
        PostList()
    }
}

@Composable
fun TopTextColumn() {
    val items = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.Person,
        Icons.Outlined.FavoriteBorder,
        Icons.Outlined.LocationOn,
        Icons.Outlined.ThumbUp
    )
    var selectedItem by remember { mutableStateOf(items[0]) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(
                            if (selectedItem == item) colorResource(id = R.color.transparentWhite)
                            else MaterialTheme.colorScheme.primary
                        )
                        .clickable { selectedItem = item },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun PostList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        items(3) { index ->
            PostItem(
                imageRes = R.drawable.stockimg1,
                text = "Example text $index"
            )
        }
    }
}

@Composable
fun PostItem(imageRes: Int, text: String) {
    var isLogoClicked by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .height(40.dp),
            ) {
                Text(
                    text = text,
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = if (isLogoClicked) R.drawable.logo_filled_white else R.drawable.logo_white),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { isLogoClicked = !isLogoClicked }
            )
            Box {
                IconButton(onClick = { isDropdownExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { stringResource(R.string.report) },
                        onClick = {
                            isDropdownExpanded = false
                            showReportDialog = true
                        }
                    )
                }
            }
        }
    }
    // !!!!!! Change to correct entity id and entity name
    if (showReportDialog) {
        ReportDialog(
            entityId = UserSession.userId,
            entityName = "Home",
            token = UserSession.token,
            onDismiss = { showReportDialog = false }
        )
    }
}