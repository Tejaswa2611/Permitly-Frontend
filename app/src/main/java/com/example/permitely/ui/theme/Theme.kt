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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================================================
// Theme Configuration for Permitely - Visitor Management System
// ============================================================================
// This file configures the complete Material Design 3 theme for the app,
// including light and dark color schemes and theme application logic.

/**
 * Dark color scheme configuration for the Permitely app.
 *
 * Uses carefully selected colors that maintain accessibility and readability
 * in dark environments while preserving brand identity.
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,              // Lighter blue for better contrast in dark mode
    onPrimary = OnPrimary,              // White text on primary surfaces
    secondary = Secondary,              // Gray remains consistent
    onSecondary = OnSecondary,          // White text on secondary surfaces
    background = DarkBackground,        // Dark navy background
    onBackground = DarkOnBackground,    // Light text on dark background
    surface = DarkSurface,              // Dark surface for cards and dialogs
    onSurface = DarkOnSurface,          // Light text on dark surfaces
    error = Error                       // Red error color (same for both themes)
)

/**
 * Light color scheme configuration for the Permitely app.
 *
 * The default theme using professional blue and clean grays,
 * optimized for daytime use and maximum readability.
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,                  // Professional blue for primary actions
    onPrimary = OnPrimary,              // White text on primary surfaces
    secondary = Secondary,              // Soft gray for secondary elements
    onSecondary = OnSecondary,          // White text on secondary surfaces
    background = Background,            // Light gray background
    onBackground = OnBackground,        // Dark text on light background
    surface = Surface,                  // White surface for cards and dialogs
    onSurface = OnSurface,              // Dark text on light surfaces
    error = Error                       // Red error color for error states
)

/**
 * Main theme composable for the Permitely app.
 *
 * This function applies the complete Material Design 3 theme including:
 * - Color scheme (light/dark mode support)
 * - Typography system
 * - Status bar styling
 * - System UI integration
 *
 * @param darkTheme Whether to use dark theme. Defaults to system preference.
 * @param dynamicColor Whether to use dynamic colors (Android 12+). Disabled for consistent branding.
 * @param content The app content to be themed.
 */
@Composable
fun PermitelyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),    // Respect system dark mode setting
    dynamicColor: Boolean = false,                 // Disabled to maintain brand consistency
    content: @Composable () -> Unit
) {
    // ============================================================================
    // Color Scheme Selection Logic
    // ============================================================================
    // Determines which color scheme to use based on user preferences and system capabilities

    val colorScheme = when {
        // Dynamic color support (Android 12+) - currently disabled for branding consistency
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Dark theme - use our custom dark color scheme
        darkTheme -> DarkColorScheme
        // Light theme (default) - use our custom light color scheme
        else -> LightColorScheme
    }

    // ============================================================================
    // Status Bar Styling
    // ============================================================================
    // Configure system UI elements to match the app theme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to match primary color for immersive experience
            window.statusBarColor = colorScheme.primary.toArgb()
            // Configure status bar icon colors based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // ============================================================================
    // Theme Application
    // ============================================================================
    // Apply the complete Material Design 3 theme to the app content

    MaterialTheme(
        colorScheme = colorScheme,          // Apply selected color scheme
        typography = Typography,            // Apply our custom typography system
        content = content                   // Render the app content with theme applied
    )
}