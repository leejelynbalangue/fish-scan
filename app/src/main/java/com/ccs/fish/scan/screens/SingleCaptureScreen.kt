package com.ccs.fish.scan.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import coil.compose.rememberAsyncImagePainter
import com.ccs.fish.scan.camera.xywh2xyxy
import com.ccs.fish.scan.data.capture.CaptureViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleCaptureScreen(
    onSingleCaptureDeleted: (String?) -> Unit,
    onSingleCaptureScreenBacked: () -> Unit,
    captureViewModel: CaptureViewModel,
    captureId: String? = null,
) {
    val capture by captureViewModel.getCaptureById(captureId?.toInt() ?: -1)
        .collectAsState(initial = null)
    var fileName by remember { mutableStateOf("") }
    var fileURI by remember { mutableStateOf("") }
    var boundingBoxes by remember { mutableStateOf(emptyArray<FloatArray>()) }
    var ladyfishCount by remember { mutableStateOf(0) }
    var milkfishCount by remember { mutableStateOf(0) }
    var captureTime by remember { mutableStateOf(0L) }

    LaunchedEffect(capture) {
        capture?.let {
            fileName = capture?.fileName.toString()
            fileURI = capture?.fileURI.toString()
            ladyfishCount = capture?.ladyfishCount ?: 0
            milkfishCount = capture?.milkfishCount ?: 0
            captureTime = capture?.captureTime ?: 0
            val type = object : TypeToken<Array<FloatArray>>() {}.type
            boundingBoxes = Gson().fromJson(capture?.boundingBoxes, type)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = fileName)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onSingleCaptureScreenBacked() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSingleCaptureDeleted(captureId)
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

        ) { innerPadding ->
        // apply black background color
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.Black)
        )
        {
            DetectionInfoComponent(
                ladyfishCount = ladyfishCount,
                milkfishCount = milkfishCount,
                endTime = captureTime
            )
            Image(
                painter = rememberAsyncImagePainter(fileURI),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Log.d("fish-scan", "fileURI(single): $fileURI")
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val top = (size.height - canvasWidth) / 2

                drawRect(
                    color = Color.Magenta,
                    topLeft = Offset(x = 0f, y = top),
                    size = Size(width = canvasWidth, height = canvasWidth),
                    style = Stroke(width = 10f)
                )

                boundingBoxes.forEachIndexed { _, box ->
                    Log.d("fish-scan", "bounding boxes(gallery): ${box.contentToString()}")
                    val newBox = xywh2xyxy(box)
                    // rescale
                    val x1 = (newBox[0] / 640) * canvasWidth
                    val y1 = (newBox[1] / 640) * canvasWidth
                    val x2 = (newBox[2] / 640) * canvasWidth
                    val y2 = (newBox[3] / 640) * canvasWidth

                    // width get ABSOLUTE difference of x1 and x2
                    val width = abs(x1 - x2)
                    val height = abs(y1 - y2)

                    drawRect(
                        color = if (box[5].toInt() == 0) {
                            Color.Red
                        } else {
                            Color.Green
                        },
                        topLeft = Offset(x1, y1 + top),
                        size = Size(width, height),
                        style = Stroke(width = 5f)
                    )
                }
            }
        }
    }


}