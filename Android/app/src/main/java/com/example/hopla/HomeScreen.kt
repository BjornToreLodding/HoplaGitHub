package com.example.hopla

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopTextColumn()
    }
}

@Composable
fun TopTextColumn() {
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
                    text = stringResource(R.string.all),
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
                    text = stringResource(R.string.friends),
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
                    text = stringResource(R.string.area),
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
                    text = stringResource(R.string.popular),
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
                    text = stringResource(R.string.updates),
                    fontSize = 10.sp
                )
            }
        }
    }
}