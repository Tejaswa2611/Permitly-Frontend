package com.example.permitely.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.permitely.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Dark theme loading components for Permitely app
 */

/**
 * Simple circular progress indicator with dark theme colors
 */
@Composable
fun PermitelyLoader(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 4.dp,
    color: Color = Primary
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth,
        trackColor = color.copy(alpha = 0.2f)
    )
}

/**
 * Custom animated dots loader with dark theme
 */
@Composable
fun PermitelyDotsLoader(
    modifier: Modifier = Modifier,
    dotColor: Color = Primary,
    animationDelay: Int = 100
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_loader")

    @Composable
    fun animateAlphaWithDelay(delay: Int): Float {
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = delay),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_alpha_$delay"
        )
        return alpha
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = dotColor.copy(alpha = animateAlphaWithDelay(index * animationDelay)),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

/**
 * Pulsing loader with dark theme gradient effect
 */
@Composable
fun PermitelyPulseLoader(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 60.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_loader")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .size(size * scale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary.copy(alpha = alpha),
                        Secondary.copy(alpha = alpha * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.6f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GradientStart, GradientMiddle)
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
    }
}

/**
 * Full screen loading overlay with dark theme
 */
@Composable
fun PermitelyLoadingOverlay(
    isLoading: Boolean,
    message: String = "Loading...",
    onDismiss: (() -> Unit)? = null
) {
    if (isLoading) {
        Dialog(
            onDismissRequest = { onDismiss?.invoke() }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PermitelyPulseLoader()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Inline loading state for dark theme
 */
@Composable
fun PermitelyInlineLoader(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PermitelyLoader()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

/**
 * Skeleton loader for dark theme content placeholders
 */
@Composable
fun PermitelySkeleton(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = SurfaceVariant.copy(alpha = alpha)
            )
    )
}

/**
 * List item skeleton for dark theme loading states
 */
@Composable
fun PermitelyListItemSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PermitelySkeleton(
                    modifier = Modifier.size(40.dp),
                    height = 40.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    PermitelySkeleton(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        height = 16.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PermitelySkeleton(
                        modifier = Modifier.fillMaxWidth(0.4f),
                        height = 12.dp
                    )
                }
            }
        }
    }
}
