package com.example.permitely.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Modern Dark Theme Color Palette for Permitely
// ============================================================================

// Primary Colors - Modern Blue-Purple Gradient
val Primary = Color(0xFF6366F1)           // Indigo-500 - Main brand color
val PrimaryLight = Color(0xFF818CF8)      // Indigo-400 - Lighter variant
val PrimaryDark = Color(0xFF4F46E5)       // Indigo-600 - Darker variant
val OnPrimary = Color(0xFFFFFFFF)         // White text on primary

// Secondary Colors - Complementary Purple
val Secondary = Color(0xFF8B5CF6)         // Violet-500 - Secondary brand
val SecondaryLight = Color(0xFFA78BFA)    // Violet-400 - Lighter variant
val SecondaryDark = Color(0xFF7C3AED)     // Violet-600 - Darker variant
val SecondaryVariant = Color(0xFF9333EA)  // Purple-600 - Alternative
val OnSecondary = Color(0xFFFFFFFF)       // White text on secondary

// Background Colors - Dark Theme Base
val Background = Color(0xFF0F0F23)        // Very dark blue-black
val BackgroundSecondary = Color(0xFF1A1B3A) // Dark blue-gray
val Surface = Color(0xFF1E1E2E)           // Card/surface background
val SurfaceVariant = Color(0xFF2A2A40)    // Alternative surface
val SurfaceDim = Color(0xFF16172B)        // Dimmed surface
val OnBackground = Color(0xFFE2E8F0)      // Light text on background
val OnSurface = Color(0xFFE2E8F0)         // Light text on surface

// Text Colors - High Contrast Dark Theme
val TextPrimary = Color(0xFFFFFFFF)       // Pure white for headers
val TextSecondary = Color(0xFFCBD5E1)     // Slate-300 for body text
val TextTertiary = Color(0xFF94A3B8)      // Slate-400 for hints
val TextDisabled = Color(0xFF64748B)      // Slate-500 for disabled
val TextLink = Color(0xFF60A5FA)          // Blue-400 for links

// Border Colors
val BorderLight = Color(0xFF334155)       // Slate-700 for subtle borders
val BorderMedium = Color(0xFF475569)      // Slate-600 for normal borders
val BorderDark = Color(0xFF1E293B)        // Slate-800 for strong borders

// Status Colors - Modern and Accessible
val Success = Color(0xFF10B981)           // Emerald-500 - Success states
val SuccessLight = Color(0xFF34D399)      // Emerald-400 - Light success
val OnSuccess = Color(0xFFFFFFFF)         // White text on success

val Warning = Color(0xFFF59E0B)           // Amber-500 - Warning states
val WarningLight = Color(0xFFFBBF24)      // Amber-400 - Light warning
val OnWarning = Color(0xFF000000)         // Black text on warning

val Error = Color(0xFFEF4444)             // Red-500 - Error states
val ErrorLight = Color(0xFFF87171)        // Red-400 - Light error
val OnError = Color(0xFFFFFFFF)           // White text on error

val Info = Color(0xFF3B82F6)              // Blue-500 - Info states
val InfoLight = Color(0xFF60A5FA)         // Blue-400 - Light info
val OnInfo = Color(0xFFFFFFFF)            // White text on info

// Special UI Colors
val Accent = Color(0xFFEC4899)            // Pink-500 - Accent highlights
val AccentLight = Color(0xFFF472B6)       // Pink-400 - Light accent

// Gradient Colors for Special Effects
val GradientStart = Color(0xFF6366F1)     // Primary gradient start
val GradientMiddle = Color(0xFF8B5CF6)    // Purple middle
val GradientEnd = Color(0xFFEC4899)       // Pink gradient end

// Overlay Colors
val OverlayLight = Color(0x40000000)      // Light overlay (25% black)
val OverlayMedium = Color(0x80000000)     // Medium overlay (50% black)
val OverlayDark = Color(0xB3000000)       // Dark overlay (70% black)

// Card and Component Colors
val CardBackground = Color(0xFF1E1E2E)    // Same as Surface
val CardBorder = Color(0xFF334155)        // Same as BorderLight
val InputBackground = Color(0xFF2A2A40)   // Input field background
val InputBorder = Color(0xFF475569)       // Input field border

// Shadow Colors for Dark Theme
val ShadowLight = Color(0x1A000000)       // Light shadow (10% black)
val ShadowMedium = Color(0x33000000)      // Medium shadow (20% black)
val ShadowDark = Color(0x4D000000)        // Dark shadow (30% black)
