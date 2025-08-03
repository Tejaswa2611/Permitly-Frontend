package com.example.permitely.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// Typography System for Permitely - Visitor Management System
// ============================================================================
// This file defines the complete typography hierarchy following Material Design 3
// guidelines. The typography is optimized for readability and accessibility
// across different screen sizes and use cases.

/**
 * Typography configuration for the Permitely app.
 *
 * This typography system provides a comprehensive set of text styles that ensure
 * consistent text appearance throughout the app. The hierarchy is designed to:
 *
 * - Maintain clear content hierarchy
 * - Ensure excellent readability on mobile devices
 * - Follow Material Design 3 typography guidelines
 * - Support accessibility requirements
 */
val Typography = Typography(
    // ============================================================================
    // DISPLAY STYLES - Large, attention-grabbing text
    // ============================================================================
    // Used for hero text, onboarding titles, and major section headers

    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,                    // Large size for maximum impact
        lineHeight = 40.sp,                  // Proper line spacing for readability
        letterSpacing = (-0.5).sp            // Tighter letter spacing for large text
    ),

    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,                    // Medium display size
        lineHeight = 36.sp,
        letterSpacing = 0.sp                 // Default letter spacing
    ),

    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,                    // Smaller display text
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // ============================================================================
    // HEADLINE STYLES - Page titles and section headers
    // ============================================================================
    // Used for screen titles, card headers, and important section divisions

    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,    // Slightly lighter than display
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    // ============================================================================
    // TITLE STYLES - Component titles and emphasis text
    // ============================================================================
    // Used for dialog titles, list headers, and emphasized content

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Medium weight for titles
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp               // Slight letter spacing for clarity
    ),

    // ============================================================================
    // BODY STYLES - Main content text
    // ============================================================================
    // Used for paragraphs, descriptions, and primary content

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,      // Regular weight for body text
        fontSize = 16.sp,                    // Comfortable reading size
        lineHeight = 24.sp,                  // 1.5x line height for readability
        letterSpacing = 0.5.sp               // Improved letter spacing for reading
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,                    // Standard body text size
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,                    // Small body text for captions
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // ============================================================================
    // LABEL STYLES - Button text and interactive elements
    // ============================================================================
    // Used for buttons, tabs, chips, and other interactive components

    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,      // Medium weight for emphasis
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,                    // Smallest label size
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)