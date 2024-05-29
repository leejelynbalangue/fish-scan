package com.ccs.fish.scan.camera

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.ccs.fish.scan.R
import com.ccs.fish.scan.data.capture.Capture
import com.ccs.fish.scan.data.capture.CaptureViewModel
import com.ccs.fish.scan.screens.DetectionInfoComponent
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs


const val INPUT_SHAPE = 640

private val executor = Executors.newSingleThreadExecutor()

// Image analyzer buffer
private var imageRotationDegrees: Int = 0
private lateinit var bitmapBuffer: Bitmap
private lateinit var uprightImage: Bitmap
private lateinit var croppedImage: Bitmap

// ONNX
private var ortEnv: OrtEnvironment? = OrtEnvironment.getEnvironment()
private var ortSession: OrtSession? = null
private var inputName: String? = null

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewScreen(
    onViewGalleryClicked: () -> Unit,
    captureViewModel: CaptureViewModel,
    padding: PaddingValues,
) {
    var isLiveDetectionEnabled by remember { mutableStateOf(true) }
    var boundingBoxes by remember { mutableStateOf(emptyList<FloatArray>()) }
    var classIds by remember { mutableStateOf(intArrayOf()) }

    var ladyfishCount by remember { mutableIntStateOf(0) }
    var milkfishCount by remember { mutableIntStateOf(0) }

    var endTime by remember { mutableLongStateOf(0L) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current

    // CameraX
    val previewView = PreviewView(localContext)
    previewView.scaleType = PreviewView.ScaleType.FIT_CENTER
    val resolution = ResolutionSelector.Builder()
        .setAspectRatioStrategy(RATIO_4_3_FALLBACK_AUTO_STRATEGY)
        .build()
    val preview = Preview.Builder()
        .setResolutionSelector(resolution)
        .build()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        //.setResolutionSelector(resolution)
        .build()

    LaunchedEffect(Unit) {
        ortSession = createOrtSession(localContext)
        inputName = ortSession?.inputNames?.iterator()?.next()
    }

    imageAnalysis.setAnalyzer(executor) { image ->
        if (ortSession != null) {
            val startTime = System.currentTimeMillis()

            if (!::bitmapBuffer.isInitialized) {
                //Log.d("fish-scan", "Image analysis with ORT session: bitmapBuffer not initialized")
                imageRotationDegrees = image.imageInfo.rotationDegrees
                bitmapBuffer = Bitmap.createBitmap(
                    image.width, image.height, Bitmap.Config.ARGB_8888
                )
            }

            image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

            val matrix = Matrix().apply {
                postRotate(imageRotationDegrees.toFloat())
            }
            uprightImage = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
            )

            // Square crop in the middle
            val width = uprightImage.width
            val height = uprightImage.height
            val size = if (width < height) width else height
            val x = (width - size) / 2
            val y = (height - size) / 2
            croppedImage = Bitmap.createBitmap(uprightImage, x, y, size, size)

            if (isLiveDetectionEnabled) {
                val result = detectCroppedImage(startTime)
                endTime = result.endTime // updates the inference text
                boundingBoxes = result.boundingBoxes // draw the rect
                classIds = result.classIds // modify the rect colors
                ladyfishCount = result.ladyfishCount // draw text
                milkfishCount = result.milkfishCount // draw text
            }
            image.close()
        }
    }

    LaunchedEffect(cameraSelector) {
        val cameraProvider = localContext.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalysis,
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
        )

        DetectionInfoComponent(
            isShowValues = isLiveDetectionEnabled,
            ladyfishCount = ladyfishCount,
            milkfishCount = milkfishCount,
            endTime = endTime
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val top = (size.height - canvasWidth) / 2

            drawRect(
                color = Color.Magenta,
                topLeft = Offset(x = 0f, y = top),
                size = Size(width = canvasWidth, height = canvasWidth),
                style = Stroke(width = 10f)
            )

            if (isLiveDetectionEnabled) {
                if (boundingBoxes.isNotEmpty()) {
                    boundingBoxes.forEachIndexed { i, box ->
                        val newBox = xywh2xyxy(box)
                        // Rescale
                        val x1 = (newBox[0] / INPUT_SHAPE) * canvasWidth
                        val y1 = (newBox[1] / INPUT_SHAPE) * canvasWidth
                        val x2 = (newBox[2] / INPUT_SHAPE) * canvasWidth
                        val y2 = (newBox[3] / INPUT_SHAPE) * canvasWidth

                        val width = abs(x1 - x2)
                        val height = abs(y1 - y2)

                        drawRect(
                            color = if (classIds[i] == 0) {
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
        var fileURI by remember { mutableStateOf("") }
        val capture by captureViewModel.getLatestCapture().collectAsState(initial = null)
        LaunchedEffect(capture) {
            capture?.let {
                fileURI = capture?.fileURI.toString()
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Image Circle
                Image(
                    painter = rememberAsyncImagePainter(fileURI),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.DarkGray, CircleShape)
                        .clickable {
                            onViewGalleryClicked()
                        },
                    contentScale = ContentScale.Crop
                )

                // Capture Button
                Button(
                    onClick = {
                        val startTime = System.currentTimeMillis()
                        val result = detectCroppedImage(startTime)

                        //log sidebyside result.boundingBoxes vs result.boundingBoxes.toTypedArray()
                        for (i in result.boundingBoxes) {
                            Log.d("fish-scan","result.boundingBoxes: ${i.contentToString()}")
                        }
                        for (i in result.boundingBoxes.toTypedArray()) {
                            Log.d("fish-scan", "result.boundingBoxes.toTypedArray(): ${i.contentToString()}")
                        }

                        saveBitmapToGallery(
                            croppedImage,
                            context = localContext,
                            endTime = result.endTime,
                            boxes = result.boundingBoxes.toTypedArray(),
                            ladyfishCount = result.ladyfishCount,
                            milkfishCount = result.milkfishCount,
                            captureViewModel = captureViewModel
                        )

                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.size(72.dp)
                ) { }

                Button(
                    onClick = { isLiveDetectionEnabled = !isLiveDetectionEnabled },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isLiveDetectionEnabled) Icons.Outlined.Check else Icons.Outlined.Clear,
                        contentDescription = if (isLiveDetectionEnabled) "Detection Enabled" else "Detection Disabled",
                        tint = if (isLiveDetectionEnabled) Color.Yellow else Color.White,
                        modifier = Modifier.scale(5f)
                    )
                }
            }
        }
    }
}

data class Result(
    val endTime: Long,
    val boundingBoxes: List<FloatArray>,
    val classIds: IntArray,
    val ladyfishCount: Int,
    val milkfishCount: Int
)

fun detectCroppedImage(
    startTime: Long,
): Result {
    val resizedBitmap =
        Bitmap.createScaledBitmap(croppedImage, INPUT_SHAPE, INPUT_SHAPE, true)

    // Convert to bytearray
    val stream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    val shape = longArrayOf(byteArray.size.toLong())

    // Create tensor
    val inputTensor = OnnxTensor.createTensor(
        ortEnv,
        ByteBuffer.wrap(byteArray), // Convert to byte buffer
        shape,
        OnnxJavaType.UINT8
    )

    inputTensor.use {
        val inputName = ortSession?.inputNames?.iterator()?.next()

        val output = ortSession?.run(
            Collections.singletonMap(inputName, inputTensor),
            setOf("nms_output_with_scaled_boxes_and_keypoints")
        )
        val endTime = (System.currentTimeMillis() - startTime)

        output.use {
            val detections = (output?.get(0)?.value) as Array<FloatArray>
            val tempList = mutableListOf<Int>()

            for (detection in detections) {
                // 6th element is class id
                tempList.add(detection[5].toInt())
            }
            val classIds = tempList.toIntArray()
            val boundingBoxes = detections.toList()

            val ladyFishCount = classIds.count { it == 0 }
            val bayedbedCount = classIds.count { it == 1 }

            return Result(endTime, boundingBoxes, classIds, ladyFishCount, bayedbedCount)
        }
    }
}

// Read ONNX model into a ByteArray, run in background
private suspend fun readModel(context: Context): ByteArray = withContext(Dispatchers.IO) {
    val modelID = R.raw.best
    context.resources.openRawResource(modelID).readBytes()
}

// Create a new ORT session in background
suspend fun createOrtSession(context: Context): OrtSession? = withContext(Dispatchers.Default) {
    val sessionOptions: OrtSession.SessionOptions = OrtSession.SessionOptions()
    sessionOptions.registerCustomOpLibrary(OrtxPackage.getLibraryPath())
    ortEnv?.createSession(readModel(context), sessionOptions)
}

fun saveBitmapToGallery(
    bitmap: Bitmap,
    //filename: String,
    mimeType: String = "image/png",
    compressionQuality: Int = 95,
    context: Context,
    endTime: Long,
    boxes: Array<FloatArray>,
    ladyfishCount: Int,
    milkfishCount: Int,
    captureViewModel: CaptureViewModel,
): Uri? {
    val timestamp = System.currentTimeMillis()
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$timestamp.png")
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        //put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FishScan")
        }
    }

    val uri: Uri? = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    )
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream: OutputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, compressionQuality, outputStream)
            captureViewModel.insertCapture(
                Capture(
                    timestamp = timestamp,
                    dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        Date(timestamp)
                    ),
                    fileURI = it.toString(),
                    fileName = "$timestamp.png",
                    boundingBoxes = Gson().toJson(boxes),
                    ladyfishCount = ladyfishCount,
                    milkfishCount = milkfishCount,
                    captureMode = 1,
                    captureTime = endTime
                )
            )
            Toast.makeText(context, "Image is saved", Toast.LENGTH_SHORT).show()
        }
    } ?: run {
        // Handle the case where the image could not be saved
    }

    return uri
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

fun xywh2xyxy(x: FloatArray): FloatArray {
    val y = FloatArray(4)
    y[0] = x[0] - x[2] / 2
    y[1] = x[1] - x[3] / 2
    y[2] = x[0] + x[2] / 2
    y[3] = x[1] + x[3] / 2
    return y
}