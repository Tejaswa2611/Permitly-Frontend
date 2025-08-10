package com.example.permitely.ui.guard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.ui.theme.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Scan Pass Screen - Full-screen camera with QR code scanning
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPassScreen(
    onNavigateBack: () -> Unit,
    onEnterManually: () -> Unit,
    onPassScanned: (String) -> Unit,
    viewModel: ScanPassViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isFlashlightOn by remember { mutableStateOf(false) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var camera: Camera? by remember { mutableStateOf(null) }

    // Camera executor
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val provider = cameraProviderFuture.get()
                    cameraProvider = provider

                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    // TODO: Add QR code analysis here
                    // imageAnalyzer.setAnalyzer(cameraExecutor, QRCodeAnalyzer { qrCode ->
                    //     onPassScanned(qrCode)
                    // })

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        provider.unbindAll()
                        camera = provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Scan Frame Overlay
        ScanFrameOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Flashlight Toggle
            IconButton(
                onClick = {
                    isFlashlightOn = !isFlashlightOn
                    camera?.cameraControl?.enableTorch(isFlashlightOn)
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    contentDescription = if (isFlashlightOn) "Turn off flashlight" else "Turn on flashlight",
                    tint = if (isFlashlightOn) Color.Yellow else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Scan Instructions
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 120.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Position QR code within the frame",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Bottom Section with Manual Entry
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Manual Entry Button
            TextButton(
                onClick = onEnterManually,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Enter Pass ID manually",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Verifying pass...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }

        // Error State
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show error for 3 seconds
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Scan Frame Overlay - Creates the centered scan frame with corners
 */
@Composable
private fun ScanFrameOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Frame dimensions
        val frameSize = minOf(canvasWidth, canvasHeight) * 0.7f
        val frameLeft = (canvasWidth - frameSize) / 2
        val frameTop = (canvasHeight - frameSize) / 2

        // Draw semi-transparent overlay with cutout
        drawRect(
            color = Color.Black.copy(alpha = 0.6f),
            topLeft = Offset.Zero,
            size = Size(canvasWidth, canvasHeight)
        )

        // Cut out the scanning area
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameSize, frameSize),
            blendMode = BlendMode.Clear
        )

        // Draw frame corners
        val cornerLength = 40f
        val cornerThickness = 6f
        val cornerColor = Primary

        // Top-left corner
        drawCorner(
            color = cornerColor,
            startX = frameLeft,
            startY = frameTop,
            length = cornerLength,
            thickness = cornerThickness,
            isTopLeft = true
        )

        // Top-right corner
        drawCorner(
            color = cornerColor,
            startX = frameLeft + frameSize,
            startY = frameTop,
            length = cornerLength,
            thickness = cornerThickness,
            isTopRight = true
        )

        // Bottom-left corner
        drawCorner(
            color = cornerColor,
            startX = frameLeft,
            startY = frameTop + frameSize,
            length = cornerLength,
            thickness = cornerThickness,
            isBottomLeft = true
        )

        // Bottom-right corner
        drawCorner(
            color = cornerColor,
            startX = frameLeft + frameSize,
            startY = frameTop + frameSize,
            length = cornerLength,
            thickness = cornerThickness,
            isBottomRight = true
        )
    }
}

/**
 * Helper function to draw frame corners
 */
private fun DrawScope.drawCorner(
    color: androidx.compose.ui.graphics.Color,
    startX: Float,
    startY: Float,
    length: Float,
    thickness: Float,
    isTopLeft: Boolean = false,
    isTopRight: Boolean = false,
    isBottomLeft: Boolean = false,
    isBottomRight: Boolean = false
) {
    when {
        isTopLeft -> {
            // Horizontal line
            drawRect(
                color = color,
                topLeft = Offset(startX, startY),
                size = Size(length, thickness)
            )
            // Vertical line
            drawRect(
                color = color,
                topLeft = Offset(startX, startY),
                size = Size(thickness, length)
            )
        }
        isTopRight -> {
            // Horizontal line
            drawRect(
                color = color,
                topLeft = Offset(startX - length, startY),
                size = Size(length, thickness)
            )
            // Vertical line
            drawRect(
                color = color,
                topLeft = Offset(startX - thickness, startY),
                size = Size(thickness, length)
            )
        }
        isBottomLeft -> {
            // Horizontal line
            drawRect(
                color = color,
                topLeft = Offset(startX, startY - thickness),
                size = Size(length, thickness)
            )
            // Vertical line
            drawRect(
                color = color,
                topLeft = Offset(startX, startY - length),
                size = Size(thickness, length)
            )
        }
        isBottomRight -> {
            // Horizontal line
            drawRect(
                color = color,
                topLeft = Offset(startX - length, startY - thickness),
                size = Size(length, thickness)
            )
            // Vertical line
            drawRect(
                color = color,
                topLeft = Offset(startX - thickness, startY - length),
                size = Size(thickness, length)
            )
        }
    }
}
