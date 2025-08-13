package com.example.permitely.ui.guard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
// ML Kit imports for QR code scanning
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
// Permission handling imports
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker

/**
 * QR Code Analyzer for ML Kit barcode scanning
 */
@androidx.camera.core.ExperimentalGetImage
class QRCodeAnalyzer(
    private val onQRCodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        when (barcode.valueType) {
                            Barcode.TYPE_URL,
                            Barcode.TYPE_TEXT -> {
                                barcode.rawValue?.let { value ->
                                    onQRCodeDetected(value)
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle scanning failure silently
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

/**
 * Scan Pass Screen - Full-screen camera with QR code scanning
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.camera.core.ExperimentalGetImage::class)
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

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PermissionChecker.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    // Request permission if not granted
    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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
        // Camera Preview - Only show if permission granted
        if (hasCameraPermission) {
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

                        @Suppress("ExperimentalGetImage")
                        imageAnalyzer.setAnalyzer(cameraExecutor, QRCodeAnalyzer { qrCode ->
                            // Route through ViewModel instead of calling onPassScanned directly
                            viewModel.onQRCodeScanned(qrCode)
                        })

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
        } else {
            // Permission not granted - show message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Camera Permission Required",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "This app needs camera permission to scan QR codes. Please grant camera permission to continue.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Grant Camera Permission")
                    }
                }
            }
        }

        // Show scan overlay only if camera permission granted
        if (hasCameraPermission) {
            ScanFrameOverlay()
        }

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

        // Scan Result Display
        uiState.scanResult?.let { scanResult ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                ScanResultCard(
                    scanResult = scanResult,
                    onScanAgain = { viewModel.startScanningAgain() },
                    onNavigateBack = onNavigateBack
                )
            }
        }

        // Error State
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
                viewModel.startScanningAgain()
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, top = 100.dp, end = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f))
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Scan Result Card - Shows pass verification results
 */
@Composable
private fun ScanResultCard(
    scanResult: com.example.permitely.data.models.PassScanResult,
    onScanAgain: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (scanResult.isSuccess) Success.copy(alpha = 0.95f) else Color.Red.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Icon and Title
            Icon(
                imageVector = if (scanResult.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (scanResult.isSuccess) "Pass Verified Successfully!" else "Verification Failed",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (scanResult.isSuccess && scanResult.visitor != null && scanResult.pass != null) {
                VisitorDetailsSection(
                    visitor = scanResult.visitor,
                    pass = scanResult.pass
                )
            } else {
                Text(
                    text = scanResult.errorMessage ?: "Unknown error occurred",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                ) {
                    Text("Done")
                }

                Button(
                    onClick = onScanAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = if (scanResult.isSuccess) Success else Color.Red
                    )
                ) {
                    Text("Scan Again")
                }
            }
        }
    }
}

/**
 * Visitor Details Section for successful scan
 */
@Composable
private fun VisitorDetailsSection(
    visitor: com.example.permitely.data.models.VisitorScanData,
    pass: com.example.permitely.data.models.PassScanData
) {
    Column {
        Text(
            text = "Visitor Information",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailRow("Name", visitor.name)
        DetailRow("Email", visitor.email)
        DetailRow("Phone", visitor.phoneNumber)
        DetailRow("Purpose", visitor.purposeOfVisit)
        DetailRow("Status", visitor.status)

        if (pass.expiryTime.isNotEmpty()) {
            DetailRow("Pass Expires", formatDateTime(pass.expiryTime))
        }
    }
}

/**
 * Detail Row for visitor information
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Format ISO datetime string for display
 */
private fun formatDateTime(isoString: String): String {
    return try {
        isoString.substring(0, 16).replace("T", " ")
    } catch (e: Exception) {
        isoString
    }
}

/**
 * Scan Frame Overlay - Custom composable to draw the scanning frame overlay
 */
@Composable
fun ScanFrameOverlay(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameSize = size.width * 0.7f
            val frameLeft = (size.width - frameSize) / 2
            val frameTop = (size.height - frameSize) / 2
            val strokeWidth = with(density) { 4.dp.toPx() }

            // Draw semi-transparent overlay
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = Size(size.width, frameTop)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(0f, frameTop + frameSize),
                size = Size(size.width, size.height - frameTop - frameSize)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(0f, frameTop),
                size = Size(frameLeft, frameSize)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(frameLeft + frameSize, frameTop),
                size = Size(size.width - frameLeft - frameSize, frameSize)
            )

            // Draw frame corners
            val cornerLength = with(density) { 24.dp.toPx() }
            val frameColor = Primary

            // Top-left corner
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(cornerLength, strokeWidth)
            )
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(strokeWidth, cornerLength)
            )

            // Top-right corner
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft + frameSize - cornerLength, frameTop),
                size = Size(cornerLength, strokeWidth)
            )
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft + frameSize - strokeWidth, frameTop),
                size = Size(strokeWidth, cornerLength)
            )

            // Bottom-left corner
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft, frameTop + frameSize - strokeWidth),
                size = Size(cornerLength, strokeWidth)
            )
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft, frameTop + frameSize - cornerLength),
                size = Size(strokeWidth, cornerLength)
            )

            // Bottom-right corner
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft + frameSize - cornerLength, frameTop + frameSize - strokeWidth),
                size = Size(cornerLength, strokeWidth)
            )
            drawRect(
                color = frameColor,
                topLeft = Offset(frameLeft + frameSize - strokeWidth, frameTop + frameSize - cornerLength),
                size = Size(strokeWidth, cornerLength)
            )
        }
    }
}
