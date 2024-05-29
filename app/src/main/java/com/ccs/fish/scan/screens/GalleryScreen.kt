package com.ccs.fish.scan.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ccs.fish.scan.data.capture.CaptureViewModel
import com.ccs.fish.scan.utils.Navigation
import com.ccs.fish.scan.utils.convertDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onSingleCaptureClicked: (String) -> Unit,
    mainMenuNavController: NavController,
    captureViewModel: CaptureViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Gallery")
                },
                navigationIcon = {
                    IconButton(onClick = { mainMenuNavController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO: Remove soon
                            captureViewModel.deleteAllCaptures()
                            Toast.makeText(
                                mainMenuNavController.context,
                                "Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Localized description"
                        )
                    }

                },
            )
        },

    ) { padding ->
        Box(modifier = Modifier.padding(padding))
        {
            Column(

            ) {
                GalleryScreenList(
                    onSingleCaptureClicked,
                    captureViewModel
                )
            }
        }
    }
}

@Composable
fun GalleryScreenList(
    onSingleCaptureClicked: (String) -> Unit,
    captureViewModel: CaptureViewModel
) {
    val allCaptureDates by captureViewModel.allCapturesByDate.collectAsState(initial = emptyList())
    Column {
        for (date in allCaptureDates) {
            GalleryScreenListItem(
                onSingleCaptureClicked,
                captureViewModel,
                date
            )
        }
    }
}

@Composable
fun GalleryScreenListItem(
    onSingleCaptureClicked: (String) -> Unit,
    captureViewModel: CaptureViewModel,
    date: String,
) {
    Column(
        modifier = Modifier
            .padding(
                bottom = 8.dp
            )
    ) {
        // Date and count
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.background(color = Color.hsv(211f, 0.24f, 0.99f, 1f))
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp,
                ),
        )
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = Arrangement.SpaceBetween
                //.padding(12.dp)
            ) {
                Text(
                    text = convertDateFormat(date),
                    style = MaterialTheme.typography.titleMedium
                )
//                Text(
//                    text = "$ladyFishCount total ladyfish",
//                    style = MaterialTheme.typography.titleSmall,
//                    color = MaterialTheme.colorScheme.secondary
//                )
            }
        }
        GalleryScreenImageGrid(
            onSingleCaptureClicked,
            captureViewModel,
            date
        )
    }
}

@Composable
fun GalleryScreenImageGrid(
    onSingleCaptureClicked: (String) -> Unit,
    captureViewModel: CaptureViewModel,
    date: String,
) {
    val captures by captureViewModel.getCapturesByDate(date).collectAsState(initial = emptyList())

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(captures) { capture ->
            AsyncImage(
                model = Uri.parse(capture.fileURI),
                contentDescription = "image",
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        onSingleCaptureClicked("${Navigation.SingleCapture.route}/${capture.id}")
                    },
                contentScale = ContentScale.Crop
            )
        }
    }

}