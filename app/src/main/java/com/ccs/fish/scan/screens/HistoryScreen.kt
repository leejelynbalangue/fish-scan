package com.ccs.fish.scan.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ccs.fish.scan.data.capture.CaptureViewModel
import com.ccs.fish.scan.utils.Navigation
import com.ccs.fish.scan.utils.convertTimestamp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onHistoryScreenBacked: () -> Unit,
    onSingleCaptureClicked: (String) -> Unit,
    captureViewModel: CaptureViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "History")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onHistoryScreenBacked()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding))
        {
            HistoryScreenList(
                onSingleCaptureClicked,
                captureViewModel
            )
        }
    }
}

@Composable
fun HistoryScreenList(
    onSingleCaptureClicked: (String) -> Unit,
    captureViewModel: CaptureViewModel
) {
    val allCaptures by captureViewModel.allCaptures.collectAsState(initial = emptyList())

    LazyColumn {
        items(allCaptures) { item ->
            HistoryScreenListItem(
                onSingleCaptureClicked,
                item.id,
                item.timestamp,
                item.ladyfishCount,
                item.milkfishCount,
                item.fileName,
                item.fileURI,
            )
        }
    }
}

@Composable
fun HistoryScreenListItem(
    onSingleCaptureClicked: (String) -> Unit,
    captureId: Int,
    timestamp: Long,
    ladyfishCount: Int,
    milkfishCount: Int,
    fileName: String,
    fileURI: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp
            ).clickable {
                onSingleCaptureClicked("${Navigation.SingleCapture.route}/${captureId}")
            }
    )
    {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                AsyncImage(
                    model = Uri.parse(fileURI),
                    contentDescription = "image",
                    modifier = Modifier
                        .width(100.dp)
                        .padding(end = 8.dp)
                )
//                Image(
//                    painter = AsyncImage(fileURI),
//                    contentDescription = "Capture",
//                    modifier = Modifier.width(50.dp)
//                )
                Log.d("fish-scan", "fileURI(Historythumb): $fileURI")
                Column {
                    Text(
                        fileName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = convertTimestamp(timestamp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }

            }

            Column {
                Text(
                    text = "$ladyfishCount ladyfish",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "$milkfishCount milkfish",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}