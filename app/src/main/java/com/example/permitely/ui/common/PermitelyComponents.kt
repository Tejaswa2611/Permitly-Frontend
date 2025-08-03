package com.example.permitely.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.theme.*

// ============================================================================
// Minimalist UI Components for Permitely - Updated for Dark Theme
// ============================================================================

/**
 * Clean text field with dark theme styling
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
    var passwordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                isFocused = newValue.isNotEmpty()
            },
            label = {
                Text(
                    label,
                    color = if (isFocused) Primary else TextTertiary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = null,
                            tint = if (isFocused) Primary else TextTertiary
                        )
                    }
                }
            } else null,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderLight,
                errorBorderColor = Error,
                focusedLabelColor = Primary,
                unfocusedLabelColor = TextTertiary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextSecondary,
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                errorContainerColor = InputBackground,
                focusedTrailingIconColor = Primary,
                unfocusedTrailingIconColor = TextTertiary,
                cursorColor = Primary,
                errorCursorColor = Error
            ),
            shape = RoundedCornerShape(16.dp)
        )

        // Simple error message display
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Clean gradient button with dark theme styling
 */
@Composable
fun PermitelyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    // Gradient background for dark theme
    val gradientBrush = Brush.horizontalGradient(
        colors = if (enabled && !loading) {
            listOf(GradientStart, GradientMiddle, GradientEnd)
        } else {
            listOf(TextDisabled, TextTertiary)
        }
    )

    Button(
        onClick = {
            if (!loading) {
                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush),
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OnPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextDisabled
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (loading) {
            // Loading indicator with proper dark theme colors
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = OnPrimary,
                strokeWidth = 2.dp,
                trackColor = OnPrimary.copy(alpha = 0.3f)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = OnPrimary
            )
        }
    }
}

/**
 * Clean outlined button with dark theme styling
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
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Primary,
            disabledContentColor = TextDisabled,
            containerColor = Surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) Primary else BorderMedium
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Primary else TextDisabled
        )
    }
}
