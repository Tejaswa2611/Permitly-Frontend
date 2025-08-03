package com.example.permitely.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Color Palette for Permitely - Visitor Management System
// ============================================================================
// This file defines the complete color system for the app using a modern,
// minimalistic approach with professional blue as the primary color.

// ============================================================================
// PRIMARY COLORS - Modern Blue Palette
// ============================================================================
// Main brand colors used for primary actions, buttons, and key UI elements
val Primary = Color(0xFF2563EB)        // Modern blue - primary brand color
val PrimaryVariant = Color(0xFF1D4ED8)  // Darker blue for hover states and emphasis
val OnPrimary = Color(0xFFFFFFFF)       // White text/icons on primary background

// ============================================================================
// SECONDARY COLORS - Soft Gray Palette
// ============================================================================
// Secondary colors for less prominent UI elements and supporting content
val Secondary = Color(0xFF64748B)       // Soft gray for secondary actions
val SecondaryVariant = Color(0xFF475569) // Darker gray for secondary hover states
val OnSecondary = Color(0xFFFFFFFF)     // White text on secondary background

// ============================================================================
// BACKGROUND COLORS
// ============================================================================
// Base colors for app backgrounds and surfaces
val Background = Color(0xFFFAFAFA)      // Light gray background for the app
val Surface = Color(0xFFFFFFFF)         // White surface color for cards and dialogs
val OnBackground = Color(0xFF1E293B)    // Dark text on light background
val OnSurface = Color(0xFF334155)       // Medium dark text on surface

// ============================================================================
// SEMANTIC COLORS
// ============================================================================
// Colors with specific meanings for user feedback and states
val Success = Color(0xFF10B981)         // Green for success states (approvals, confirmations)
val Warning = Color(0xFFF59E0B)         // Orange for warning states (pending, caution)
val Error = Color(0xFFEF4444)           // Red for error states (rejections, failures)
val Info = Color(0xFF06B6D4)            // Cyan for informational states

// ============================================================================
// TEXT COLORS
// ============================================================================
// Hierarchy of text colors for different content levels
val TextPrimary = Color(0xFF0F172A)     // Primary text - headings and important content
val TextSecondary = Color(0xFF64748B)   // Secondary text - descriptions and labels
val TextDisabled = Color(0xFF94A3B8)    // Disabled text and inactive elements

// ============================================================================
// BORDER COLORS
// ============================================================================
// Subtle border colors for input fields and dividers
val BorderLight = Color(0xFFE2E8F0)     // Light border for input fields and dividers
val BorderMedium = Color(0xFFCBD5E1)    // Medium border for more defined separations

// ============================================================================
// DARK THEME COLORS
// ============================================================================
// Colors optimized for dark mode experience
val DarkPrimary = Color(0xFF3B82F6)     // Lighter blue for dark theme primary
val DarkBackground = Color(0xFF0F172A)  // Dark background
val DarkSurface = Color(0xFF1E293B)     // Dark surface color
val DarkOnBackground = Color(0xFFF1F5F9) // Light text on dark background
val DarkOnSurface = Color(0xFFE2E8F0)   // Light text on dark surface
