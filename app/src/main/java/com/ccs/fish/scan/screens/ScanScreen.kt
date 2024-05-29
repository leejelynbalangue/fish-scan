package com.ccs.fish.scan.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.ccs.fish.scan.MainActivity
import com.ccs.fish.scan.camera.CameraPreviewScreen
import com.ccs.fish.scan.data.capture.CaptureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onScanScreenBacked: () -> Unit,
    onViewGalleryClicked: () -> Unit,
    captureViewModel: CaptureViewModel,
    mainActivity: MainActivity,
) {
    Scaffold(
    topBar = {
        TopAppBar(
            title = {
                Text(text = "Scan")
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onScanScreenBacked()
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
        // Check if all permissions are granted
        if(!mainActivity.allPermissionsGranted()) {
            mainActivity.requestPermissions()
        } else {
            CameraPreviewScreen(
                onViewGalleryClicked,
                captureViewModel,
                padding,
            )
        }
    }
}

