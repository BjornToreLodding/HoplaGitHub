package com.example.hopla

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Preview
@Composable
fun NewTripScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf(0) }

    // Launch a coroutine to update the time
    LaunchedEffect(isRunning) {
        while (isRunning) {         // Loop while the timer is running
            delay(1000L)   // Delay of 1 second
            time++                  // Increment the time by 1 second
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Bottom Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60))
                        Text(text = stringResource(R.string.time))
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        shape = MaterialTheme.shapes.small.copy(all = CornerSize(100)),
                    ) {
                        Text(text = if (isRunning) stringResource(R.string.stop) else stringResource(R.string.start))
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.distance))
                }
            }
        }
    }
}