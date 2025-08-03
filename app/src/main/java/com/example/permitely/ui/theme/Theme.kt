package com.example.permitely.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================================================
// Modern Dark Color Scheme for Permitely
// ============================================================================

private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = OnPrimary,

    // Secondary colors
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = OnSecondary,

    // Tertiary colors
    tertiary = Accent,
    onTertiary = OnPrimary,
    tertiaryContainer = SecondaryVariant,
    onTertiaryContainer = OnSecondary,

    // Background colors
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Primary,

    // Inverse colors
    inverseSurface = TextPrimary,
    inverseOnSurface = Background,
    inversePrimary = PrimaryLight,

    // Status colors
    error = Error,
    onError = OnError,
    errorContainer = ErrorLight,
    onErrorContainer = OnError,

    // Outline colors
    outline = BorderMedium,
    outlineVariant = BorderLight,

    // Container colors
    surfaceContainer = SurfaceVariant,
    surfaceContainerHigh = Surface,
    surfaceContainerHighest = CardBackground,
    surfaceContainerLow = SurfaceDim,
    surfaceContainerLowest = Background,

    // Scrim
    scrim = OverlayDark
)

// Light color scheme for comparison (optional fallback)
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    tertiary = Accent,
    onTertiary = OnPrimary,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Error,
    onError = OnError,
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

/**
 * Main theme composable for Permitely app
 * Enforces dark theme for consistent modern look
 */
@Composable
fun PermitelyTheme(
    darkTheme: Boolean = true, // Force dark theme for modern look
    dynamicColor: Boolean = false, // Disable dynamic colors to maintain brand consistency
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false // Always use light content on dark status bar
                isAppearanceLightNavigationBars = false // Always use light content on dark nav bar
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}