package com.example.hopla.newTrip

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hopla.R
import com.example.hopla.ui.theme.generalTextStyle
import com.example.hopla.ui.theme.headerTextStyleSmall
import com.example.hopla.universalData.ImagePicker

// This function creates the content of the AlertDialog for saving a trip.
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogContent(
    tripName: String,
    onTripNameChange: (String) -> Unit,
    tripNotes: String,
    onTripNotesChange: (String) -> Unit,
    horses: List<String>,
    selectedHorse: String,
    onHorseSelected: (String) -> Unit,
    selectedImage: Bitmap?,
    onImageSelected: (Bitmap?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.save_trip),
            style = headerTextStyleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.save_now_or_later),
            style = generalTextStyle,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = tripName,
            onValueChange = onTripNameChange,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(R.string.trip_name),
                    style = generalTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                TextField(
                    value = tripNotes,
                    onValueChange = onTripNotesChange,
                    label = {
                        Text(
                            text = stringResource(R.string.description),
                            style = generalTextStyle,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(bottom = 16.dp)
                )
            }
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedHorse.ifEmpty { stringResource(R.string.no_horse_chosen) },
                onValueChange = {},
                label = { Text(stringResource(R.string.choose_horse)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onHorseSelected("")
                        expanded = false
                    }
                ) {
                    Text(
                        stringResource(R.string.no_horse_chosen),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                horses.forEach { horse ->
                    DropdownMenuItem(
                        onClick = {
                            onHorseSelected(horse)
                            expanded = false
                        }
                    ) {
                        Text(horse, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
        ImagePicker(
            onImageSelected = onImageSelected,
            text = stringResource(R.string.add_image)
        )
        selectedImage?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 16.dp)
            )
        }
    }
}
