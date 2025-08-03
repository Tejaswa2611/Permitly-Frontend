package com.example.permitely.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// Modern Typography System for Permitely Dark Theme
// ============================================================================

// Custom font family (you can replace with your preferred fonts)
// For now using system default with proper weights
val PermitelyFontFamily = FontFamily.Default

// Modern typography scale optimized for dark theme readability
val Typography = Typography(
    // Display styles - For large headers and hero text
    displayLarge = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        color = TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),

    // Headline styles - For section headers
    headlineLarge = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),

    // Title styles - For card titles and important text
    titleLarge = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        color = TextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = TextSecondary
    ),

    // Body styles - For main content text
    bodyLarge = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = TextSecondary
    ),
    bodyMedium = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = TextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = TextTertiary
    ),

    // Label styles - For buttons, tabs, and labels
    labelLarge = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = TextPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = TextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = PermitelyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = TextTertiary
    )
)