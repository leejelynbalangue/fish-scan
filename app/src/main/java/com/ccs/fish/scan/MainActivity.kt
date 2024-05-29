package com.ccs.fish.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ccs.fish.scan.data.capture.CaptureViewModel
import com.ccs.fish.scan.screens.GalleryScreen
import com.ccs.fish.scan.screens.HistoryScreen
import com.ccs.fish.scan.screens.InstructionScreen
import com.ccs.fish.scan.screens.MainMenuScreen
import com.ccs.fish.scan.screens.ScanScreen
import com.ccs.fish.scan.screens.SingleCaptureScreen
import com.ccs.fish.scan.ui.theme.FishScanTheme
import com.ccs.fish.scan.utils.Navigation


class MainActivity : ComponentActivity() {
    private val cameraPermissionRequest =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FishScanTheme {
                val captureViewModel: CaptureViewModel by viewModels()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    //color = Color.hsv(206f, 0.03f, 0.93f, 1f),
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Navigation.Main.route
                    ) {

                        // MAIN
                        composable(Navigation.Main.route) {
                            MainMenuScreen(navController)
                        }

                        // SCAN
                        composable(Navigation.Scan.route) {
                            ScanScreen(
                                onScanScreenBacked = {
                                    navController.popBackStack()
                                },
                                onViewGalleryClicked = {
                                    navController.navigate(Navigation.Gallery.route)
                                },
                                captureViewModel,
                                this@MainActivity,
                            )
                        }

                        // INSTRUCTION
                        composable(Navigation.Instruction.route) {
                            InstructionScreen(navController)
                        }

                        // GALLERY
                        composable(Navigation.Gallery.route) {
                            GalleryScreen(
                                onSingleCaptureClicked = { route ->
                                    navController.navigate(route)
                                },
                                navController,
                                captureViewModel,
                            )
                        }

                        // HISTORY
                        composable(Navigation.History.route) {
                            HistoryScreen(
                                onHistoryScreenBacked = {
                                    navController.popBackStack()
                                },
                                onSingleCaptureClicked = { route ->
                                    navController.navigate(route)
                                },
                                captureViewModel
                            )
                        }

                        composable(Navigation.SingleCapture.route + "/{captureId}") { backStackEntry ->
                            SingleCaptureScreen(
                                onSingleCaptureDeleted = { captureId ->
                                    captureViewModel.deleteCaptureById(captureId?.toInt() ?: -1)
                                    navController.popBackStack()
                                },
                                onSingleCaptureScreenBacked = {
                                    navController.popBackStack()
                                },
                                captureViewModel,
                                backStackEntry.arguments?.getString("captureId")
                            )
                        }
//                        composable(Navigation.SingleImage.route) {
//                            SingleImageScreen(captureViewModel)
//                        }
                    }

                }
            }
        }
    }

    // Popup request permission screen
    fun requestPermissions() {
        cameraPermissionRequest.launch(REQUIRED_PERMISSIONS)
    }

    // Check if all defined required permissions are granted
    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Define required permissions
    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
                .apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

//        fun getRequiredPermissions(): Array<String> {
//            return REQUIRED_PERMISSIONS
//        }
    }
}