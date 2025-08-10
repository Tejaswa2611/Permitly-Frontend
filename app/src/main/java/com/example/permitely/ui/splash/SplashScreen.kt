package com.example.permitely.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Beautiful animated splash screen for Permitely
 * Features app logo, name, tagline, and loading animations
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var logoVisible by remember { mutableStateOf(false) }
    var nameVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var iconsVisible by remember { mutableStateOf(false) }
    var loadingVisible by remember { mutableStateOf(false) }

    // Animation values
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val logoRotation by animateFloatAsState(
        targetValue = if (logoVisible) 0f else -180f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    // Gradient background
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Primary.copy(alpha = 0.8f),
            PrimaryLight.copy(alpha = 0.6f),
            Background,
            Secondary.copy(alpha = 0.3f)
        )
    )

    // Animation sequence
    LaunchedEffect(Unit) {
        delay(300)
        logoVisible = true

        delay(800)
        nameVisible = true

        delay(500)
        taglineVisible = true

        delay(400)
        iconsVisible = true

        delay(600)
        loadingVisible = true

        delay(2000) // Show loading for 2 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .rotate(logoRotation),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Card(
                    modifier = Modifier.size(140.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Primary.copy(alpha = 0.1f)
                    ),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner circle with main icon
                        Card(
                            modifier = Modifier.size(100.dp),
                            colors = CardDefaults.cardColors(containerColor = Primary),
                            shape = CircleShape
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Permitely Logo",
                                    tint = OnPrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with slide animation
            AnimatedVisibility(
                visible = nameVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Permitely",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    // Decorative underline
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(3.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Primary,
                                        Secondary,
                                        Primary
                                    )
                                ),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline with fade animation
            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(animationSpec = tween(800)) +
                       slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Text(
                    text = "Secure Visitor Management",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Floating icons animation
            AnimatedVisibility(
                visible = iconsVisible,
                enter = fadeIn(animationSpec = tween(600))
            ) {
                FloatingIcons()
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading section
            AnimatedVisibility(
                visible = loadingVisible,
                enter = fadeIn(animationSpec = tween(400))
            ) {
                LoadingSection()
            }
        }

        // Version info at bottom
        AnimatedVisibility(
            visible = loadingVisible,
            enter = fadeIn(animationSpec = tween(600)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Powered by Innovation",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun FloatingIcons() {
    val icons = listOf(
        Icons.Default.Person to Offset(-60f, -30f),
        Icons.Default.QrCode to Offset(60f, -30f),
        Icons.Default.Security to Offset(-80f, 20f),
        Icons.Default.CheckCircle to Offset(80f, 20f)
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        icons.forEachIndexed { index, (icon, offset) ->
            FloatingIcon(
                icon = icon,
                offset = offset,
                delay = index * 200L
            )
        }
    }
}

@Composable
private fun FloatingIcon(
    icon: ImageVector,
    offset: Offset,
    delay: Long
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition()
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .offset(x = offset.x.dp, y = (offset.y + floatOffset).dp)
            .scale(scale)
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.8f)
            ),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Custom loading animation
        LoadingDots()

        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 200,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .background(
                        color = Primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

// Helper data class for icon positioning
private data class Offset(val x: Float, val y: Float)
