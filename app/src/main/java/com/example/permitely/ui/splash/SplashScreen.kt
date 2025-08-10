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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
 * Professional animated splash screen for Permitely
 * Features modern animations, particle effects, and smooth transitions
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var currentPhase by remember { mutableStateOf(SplashPhase.INITIAL) }

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite")

    // Pulsing animation for logo
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )

    // Rotation animation for outer ring
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    // Shimmer effect
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Main content animations
    val logoVisibility by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.LOGO_ENTER) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logo_visibility"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.CONTENT_ENTER) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "content_alpha"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.LOADING) 1f else 0f,
        animationSpec = tween(500),
        label = "loading_alpha"
    )

    // Animation sequence
    LaunchedEffect(Unit) {
        delay(200)
        currentPhase = SplashPhase.LOGO_ENTER

        delay(1000)
        currentPhase = SplashPhase.CONTENT_ENTER

        delay(1200)
        currentPhase = SplashPhase.LOADING

        delay(2000)
        currentPhase = SplashPhase.EXIT

        delay(500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.1f),
                        Background,
                        Color(0xFF0A0E27).copy(alpha = 0.8f)
                    ),
                    radius = 1200f
                )
            )
    ) {
        // Animated background particles
        AnimatedBackgroundParticles()

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo section with sophisticated animations
            Box(
                modifier = Modifier
                    .scale(logoScale * logoVisibility)
                    .alpha(logoVisibility),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating ring with gradient
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(ringRotation)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = 0.3f),
                                        Secondary.copy(alpha = 0.6f),
                                        Primary.copy(alpha = 0.9f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                )

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.1f),
                                    Primary.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Main logo container with glass morphism effect
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.9f),
                    shadowElevation = 20.dp,
                    tonalElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                // Shimmer effect
                                val shimmerBrush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    start = Offset(shimmerOffset - 200f, shimmerOffset - 200f),
                                    end = Offset(shimmerOffset, shimmerOffset)
                                )
                                drawCircle(brush = shimmerBrush)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permitely Logo",
                            tint = OnPrimary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                // Pulsing outer glow
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size((120 + index * 30).dp)
                            .alpha(0.1f - index * 0.03f)
                            .scale(logoScale + index * 0.1f)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // App name with sophisticated typography
            AnimatedVisibility(
                visible = currentPhase >= SplashPhase.CONTENT_ENTER,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permitely",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp,
                        modifier = Modifier.alpha(contentAlpha)
                    )

                    // Animated underline with gradient
                    Box(
                        modifier = Modifier
                            .animateContentSize()
                            .width(if (currentPhase >= SplashPhase.CONTENT_ENTER) 120.dp else 0.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Primary,
                                        Secondary,
                                        Primary,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline with elegant fade-in
            AnimatedVisibility(
                visible = currentPhase >= SplashPhase.CONTENT_ENTER,
                enter = fadeIn(
                    animationSpec = tween(1000, delayMillis = 400)
                ) + slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(800, delayMillis = 400)
                )
            ) {
                Text(
                    text = "Secure • Efficient • Reliable",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.alpha(contentAlpha)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Modern loading indicator
            AnimatedVisibility(
                visible = currentPhase >= SplashPhase.LOADING,
                enter = fadeIn(animationSpec = tween(500)) +
                       scaleIn(animationSpec = tween(500, easing = FastOutSlowInEasing))
            ) {
                ModernLoadingIndicator(
                    modifier = Modifier.alpha(loadingAlpha)
                )
            }
        }

        // Floating brand elements
        FloatingBrandElements(currentPhase)
    }
}

@Composable
private fun AnimatedBackgroundParticles() {
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particles = remember {
        List(8) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 20f + 10f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                alpha = Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }

    val animationTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.6f)
    ) {
        particles.forEach { particle ->
            val x = (particle.x + animationTime * particle.speed) % 1f
            val y = (particle.y + sin(animationTime * 2 * PI * particle.speed) * 0.1).toFloat()

            drawCircle(
                color = Primary.copy(alpha = particle.alpha),
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
private fun ModernLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(scale)
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 3.dp.toPx()
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.2f),
                            Primary,
                            Secondary,
                            Primary.copy(alpha = 0.2f)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading...",
            fontSize = 14.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun FloatingBrandElements(currentPhase: SplashPhase) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_elements")

    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val float2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val alpha by animateFloatAsState(
        targetValue = if (currentPhase >= SplashPhase.CONTENT_ENTER) 0.1f else 0f,
        animationSpec = tween(1000),
        label = "floating_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top left floating element
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = Primary.copy(alpha = alpha),
            modifier = Modifier
                .size(80.dp)
                .offset(x = 40.dp, y = 120.dp + float1.dp)
                .rotate(float1 * 2)
                .blur(2.dp)
        )

        // Bottom right floating element
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = Secondary.copy(alpha = alpha),
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-50).dp, y = (-150).dp + float2.dp)
                .rotate(-float2 * 1.5f)
                .blur(1.dp)
        )

        // Top right floating element
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Accent.copy(alpha = alpha),
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-80).dp, y = 200.dp + float1.dp * 0.5f)
                .rotate(float1 * -1.5f)
                .blur(1.5.dp)
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

private data class ParticleData(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)
