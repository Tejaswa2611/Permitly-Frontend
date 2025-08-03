package com.example.permitely.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.theme.*

// ============================================================================
// Common UI Components for Permitely - Visitor Management System
// ============================================================================
// This file contains reusable UI components that maintain design consistency
// across the entire app while following Material Design 3 principles.

/**
 * Custom text field component for the Permitely app.
 *
 * This component provides a consistent text input experience across the app with:
 * - Unified styling following the app's design system
 * - Built-in password visibility toggle for secure fields
 * - Error state handling with custom error messages
 * - Proper keyboard type configuration
 * - Accessibility support
 *
 * @param value Current text value in the field
 * @param onValueChange Callback triggered when text changes
 * @param label Label text displayed above/within the field
 * @param modifier Compose modifier for styling and layout
 * @param isPassword Whether this field should hide input (password field)
 * @param keyboardType Type of keyboard to show (email, phone, etc.)
 * @param isError Whether the field is in error state
 * @param errorMessage Error message to display below the field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitelyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    // State to manage password visibility toggle
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            // Show/hide password based on visibility state
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()  // Hide password with dots
            } else {
                VisualTransformation.None       // Show text normally
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            // Add password visibility toggle icon for password fields
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            } else null,
            isError = isError,
            // Apply custom colors from the app theme
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,       // Blue border when focused
                unfocusedBorderColor = BorderLight, // Light gray when not focused
                errorBorderColor = Error,           // Red border for errors
                focusedLabelColor = Primary,        // Blue label when focused
                unfocusedLabelColor = TextSecondary // Gray label when not focused
            ),
            shape = RoundedCornerShape(12.dp)       // Rounded corners for modern look
        )

        // Display error message below the field if there's an error
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Error,                      // Red color for error text
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Primary button component for the Permitely app.
 *
 * This component provides the main call-to-action button styling with:
 * - Consistent brand colors and styling
 * - Loading state with progress indicator
 * - Proper disabled state handling
 * - Accessibility support
 * - Rounded corners for modern appearance
 *
 * @param text Button text to display
 * @param onClick Callback triggered when button is clicked
 * @param modifier Compose modifier for styling and layout
 * @param enabled Whether the button is clickable
 * @param loading Whether to show loading spinner instead of text
 */
@Composable
fun PermitelyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),                     // Standard button height for touch targets
        enabled = enabled && !loading,          // Disable when loading or explicitly disabled
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,           // Blue background
            contentColor = OnPrimary,           // White text
            disabledContainerColor = TextDisabled // Gray when disabled
        ),
        shape = RoundedCornerShape(12.dp)       // Rounded corners
    ) {
        if (loading) {
            // Show loading spinner when operation is in progress
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = OnPrimary,              // White spinner
                strokeWidth = 2.dp
            )
        } else {
            // Show button text normally
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 * Secondary outlined button component for the Permitely app.
 *
 * This component provides an alternative button style for secondary actions with:
 * - Outlined style instead of filled background
 * - Consistent brand colors for borders and text
 * - Same size and accessibility as primary button
 * - Modern rounded corners
 *
 * @param text Button text to display
 * @param onClick Callback triggered when button is clicked
 * @param modifier Compose modifier for styling and layout
 * @param enabled Whether the button is clickable
 */
@Composable
fun PermitelyOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),                     // Same height as primary button
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Primary              // Blue text color
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Primary                     // Blue border
        ),
        shape = RoundedCornerShape(12.dp)       // Rounded corners
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
