package com.example.permitely.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

/**
 * Enhanced professional animated splash screen for Permitly
 * Features premium animations, particle effects, and glassmorphism design
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var currentPhase by remember { mutableStateOf(SplashPhase.INITIAL) }

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite")

    // Enhanced logo animations
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )

    // Multi-layer rotation system
    val outerRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring_rotation"
    )

    val middleRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "middle_ring_rotation"
    )

    // Enhanced shimmer with wave effect
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -2000f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_intensity"
    )

    // Main content animations with improved timing
    val logoVisibility by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.LOGO_ENTER) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "logo_visibility"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.CONTENT_ENTER) 1f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "content_alpha"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.LOADING) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "loading_alpha"
    )

    // Background gradient animation
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    // Animation sequence with refined timing
    LaunchedEffect(Unit) {
        delay(300)
        currentPhase = SplashPhase.LOGO_ENTER

        delay(1200)
        currentPhase = SplashPhase.CONTENT_ENTER

        delay(1500)
        currentPhase = SplashPhase.LOADING

        delay(2500)
        currentPhase = SplashPhase.EXIT

        delay(600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.15f + gradientOffset * 0.05f),
                        Background.copy(alpha = 0.95f),
                        Color(0xFF0A0E27).copy(alpha = 0.9f),
                        Color(0xFF000000).copy(alpha = 0.95f)
                    ),
                    radius = 1400f + gradientOffset * 200f,
                    center = Offset(0.3f + gradientOffset * 0.4f, 0.3f + gradientOffset * 0.4f)
                )
            )
    ) {
        // Enhanced animated background particles
        EnhancedBackgroundParticles(currentPhase)

        // Subtle grid pattern overlay
        SubtleGridPattern(currentPhase)

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Enhanced logo section with premium glassmorphism
            Box(
                modifier = Modifier
                    .scale(logoScale * logoVisibility)
                    .alpha(logoVisibility),
                contentAlignment = Alignment.Center
            ) {
                // Outermost glow ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .rotate(outerRingRotation)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = 0.0f),
                                        Primary.copy(alpha = 0.3f),
                                        Secondary.copy(alpha = 0.6f),
                                        Accent.copy(alpha = 0.4f),
                                        Primary.copy(alpha = 0.8f),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                )

                // Middle rotating ring with different direction
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .rotate(middleRingRotation)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Secondary.copy(alpha = 0.4f),
                                        Primary.copy(alpha = 0.7f),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension / 2,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                )

                // Glassmorphism background container
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.15f),
                                    Primary.copy(alpha = 0.08f),
                                    Color.White.copy(alpha = 0.02f),
                                    Color.Transparent
                                )
                            )
                        )
                        .drawBehind {
                            // Subtle border glow
                            drawCircle(
                                color = Primary.copy(alpha = 0.3f),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                )

                // Premium main logo container
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.95f),
                    shadowElevation = 24.dp,
                    tonalElevation = 12.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                // Enhanced shimmer with wave effect
                                val shimmerWidth = 100f
                                val waveOffset = sin(shimmerOffset / 200f) * 50f
                                val shimmerBrush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.1f),
                                        Color.White.copy(alpha = shimmerIntensity),
                                        Color.White.copy(alpha = 0.1f),
                                        Color.Transparent
                                    ),
                                    start = Offset(
                                        shimmerOffset - shimmerWidth + waveOffset,
                                        shimmerOffset - shimmerWidth
                                    ),
                                    end = Offset(
                                        shimmerOffset + waveOffset,
                                        shimmerOffset
                                    )
                                )
                                drawCircle(brush = shimmerBrush)

                                // Inner glow
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    ),
                                    radius = size.minDimension / 3
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Enhanced logo icon with visitor management theme
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permitly Logo",
                            tint = OnPrimary,
                            modifier = Modifier.size(55.dp)
                        )
                    }
                }

                // Multi-layer pulsing glow effect
                repeat(4) { index ->
                    val glowSize = 120 + index * 25
                    val glowAlpha = 0.12f - index * 0.025f
                    val glowScale = logoScale + index * 0.08f

                    Box(
                        modifier = Modifier
                            .size(glowSize.dp)
                            .alpha(glowAlpha)
                            .scale(glowScale)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Enhanced app name with premium typography
            AnimatedVisibility(
                visible = currentPhase >= SplashPhase.CONTENT_ENTER,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(1000))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Main brand name with enhanced styling
                    Text(
                        text = "Permitly",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.5.sp,
                        modifier = Modifier
                            .alpha(contentAlpha)
                            .drawBehind {
                                // Subtle text shadow for depth
                                drawContext.canvas.nativeCanvas.apply {
                                    val paint = android.graphics.Paint().apply {
                                        color = Primary.copy(alpha = 0.3f).toArgb()
                                        maskFilter = android.graphics.BlurMaskFilter(
                                            8f, android.graphics.BlurMaskFilter.Blur.NORMAL
                                        )
                                    }
                                }
                            }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Enhanced animated underline with gradient flow
                    Box(
                        modifier = Modifier
                            .animateContentSize()
                            .width(if (currentPhase >= SplashPhase.CONTENT_ENTER) 140.dp else 0.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Primary.copy(alpha = 0.6f),
                                        Primary,
                                        Secondary,
                                        Primary,
                                        Primary.copy(alpha = 0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Enhanced tagline with staggered animation
            AnimatedVisibility(
                visible = currentPhase >= SplashPhase.CONTENT_ENTER,
                enter = fadeIn(
                    animationSpec = tween(1200, delayMillis = 600)
                ) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(1000, delayMillis = 600)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaglineWord("Secure", 0)
                    TaglineDot()
                    TaglineWord("Efficient", 200)
                    TaglineDot()
                    TaglineWord("Reliable", 400)
                }
            }

            Spacer(modifier = Modifier.height(72.dp))

            // Premium loading indicator
            AnimatedVisibility(
                visible = currentPhase >= SplashPhase.LOADING,
                enter = fadeIn(animationSpec = tween(600)) +
                        scaleIn(
                            animationSpec = tween(700, easing = FastOutSlowInEasing),
                            initialScale = 0.8f
                        )
            ) {
                PremiumLoadingIndicator(
                    modifier = Modifier.alpha(loadingAlpha)
                )
            }
        }

        // Enhanced floating brand elements
        EnhancedFloatingBrandElements(currentPhase)
    }
}

@Composable
private fun TaglineWord(text: String, delay: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + scaleIn(tween(400))
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        )
    }
}

@Composable
private fun TaglineDot() {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(Primary.copy(alpha = 0.6f))
    )
}

@Composable
private fun EnhancedBackgroundParticles(currentPhase: SplashPhase) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particles = remember {
        List(12) {
            EnhancedParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 15f + 8f,
                speed = Random.nextFloat() * 0.3f + 0.15f,
                alpha = Random.nextFloat() * 0.25f + 0.08f,
                floatAmplitude = Random.nextFloat() * 0.15f + 0.05f,
                rotationSpeed = Random.nextFloat() * 2f + 0.5f
            )
        }
    }

    val animationTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    val particleAlpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.LOGO_ENTER) 0.8f else 0f,
        animationSpec = tween(2000),
        label = "particle_alpha"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(particleAlpha)
    ) {
        particles.forEach { particle ->
            val x = (particle.x + animationTime * particle.speed) % 1f
            val floatY = sin(animationTime * 2 * PI * particle.speed + particle.x * 10) * particle.floatAmplitude
            val y = (particle.y + floatY).toFloat()

            // Draw particle with subtle glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = particle.alpha),
                        Primary.copy(alpha = particle.alpha * 0.5f),
                        Color.Transparent
                    )
                ),
                radius = particle.size,
                center = Offset(
                    x * size.width,
                    (y % 1f) * size.height
                )
            )
        }
    }
}

@Composable
private fun SubtleGridPattern(currentPhase: SplashPhase) {
    val alpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.CONTENT_ENTER) 0.03f else 0f,
        animationSpec = tween(3000),
        label = "grid_alpha"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        val gridSize = 60.dp.toPx()

        for (x in 0..(size.width / gridSize).toInt()) {
            drawLine(
                color = Primary.copy(alpha = 0.1f),
                start = Offset(x * gridSize, 0f),
                end = Offset(x * gridSize, size.height),
                strokeWidth = 0.5.dp.toPx()
            )
        }

        for (y in 0..(size.height / gridSize).toInt()) {
            drawLine(
                color = Primary.copy(alpha = 0.1f),
                start = Offset(0f, y * gridSize),
                end = Offset(size.width, y * gridSize),
                strokeWidth = 0.5.dp.toPx()
            )
        }
    }
}

@Composable
private fun PremiumLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )

    val progressRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress_rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            // Outer ring
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation)
            ) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.1f),
                            Primary.copy(alpha = 0.8f),
                            Secondary,
                            Accent.copy(alpha = 0.9f),
                            Primary.copy(alpha = 0.1f)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 300f,
                    useCenter = false,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Inner progress ring
            Canvas(
                modifier = Modifier
                    .size(32.dp)
                    .rotate(-progressRotation)
            ) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Secondary.copy(alpha = 0.6f),
                            Primary.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Initializing Security...",
            fontSize = 14.sp,
            color = TextSecondary.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
private fun EnhancedFloatingBrandElements(currentPhase: SplashPhase) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_elements")

    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val float2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val float3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    val alpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.CONTENT_ENTER) 0.12f else 0f,
        animationSpec = tween(1500),
        label = "floating_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top left - Shield with security theme
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = Primary.copy(alpha = alpha),
            modifier = Modifier
                .size(90.dp)
                .offset(x = 50.dp, y = 140.dp + float1.dp)
                .rotate(float1 * 1.5f)
                .blur(2.5.dp)
        )

        // Bottom right - Verified badge
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = Secondary.copy(alpha = alpha),
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-60).dp, y = (-180).dp + float2.dp)
                .rotate(-float2 * 1.2f)
                .blur(1.5.dp)
        )

        // Top right - Lock for security
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Accent.copy(alpha = alpha),
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-90).dp, y = 220.dp + float3.dp)
                .rotate(float3 * -1.8f)
                .blur(2.dp)
        )

        // Bottom left - Security scan
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = Primary.copy(alpha = alpha * 0.8f),
            modifier = Modifier
                .size(55.dp)
                .align(Alignment.BottomStart)
                .offset(x = 70.dp, y = (-200).dp + float1.dp * 0.7f)
                .rotate(float1 * -2f)
                .blur(1.8.dp)
        )
    }
}

private enum class SplashPhase {
    INITIAL,
    LOGO_ENTER,
    CONTENT_ENTER,
    LOADING,
    EXIT
}

private data class EnhancedParticleData(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float,
    val floatAmplitude: Float,
    val rotationSpeed: Float
)