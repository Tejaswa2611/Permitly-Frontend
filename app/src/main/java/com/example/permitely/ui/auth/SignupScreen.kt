package com.example.permitely.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.ui.common.PermitelyButton
import com.example.permitely.ui.common.PermitelyTextField
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Clean signup screen with minimal animations for better performance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Form input states
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedUserType by remember { mutableStateOf("host") }
    var expanded by remember { mutableStateOf(false) }

    // Animation state for logo only
    var isLogoVisible by remember { mutableStateOf(false) }

    // User type options for dropdown
    val userTypes = listOf(
        "admin" to "Administrator",
        "host" to "Host",
        "guard" to "Security Guard"
    )

    // Password validation logic
    val passwordsMatch = password == confirmPassword && password.isNotBlank()

    // Simple gradient background (no animation)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Secondary.copy(alpha = 0.1f),
            PrimaryLight.copy(alpha = 0.05f),
            Background
        )
    )

    // Navigate to main app on successful signup
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignupSuccess()
            viewModel.resetState()
        }
    }

    // Trigger logo animation only
    LaunchedEffect(Unit) {
        delay(150)
        isLogoVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Floating logo animation (kept as requested)
            AnimatedVisibility(
                visible = isLogoVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn()
            ) {
                ModernSignupLogo()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Simple title (no animation)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Join Permitely to manage visits efficiently",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Form fields without animations for better performance
            PermitelyTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermitelyTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermitelyTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User type dropdown
            ModernDropdownField(
                selectedValue = selectedUserType,
                options = userTypes,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onSelectionChange = { selectedUserType = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password fields
            PermitelyTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermitelyTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                isPassword = true,
                keyboardType = KeyboardType.Password,
                isError = confirmPassword.isNotBlank() && !passwordsMatch,
                errorMessage = if (confirmPassword.isNotBlank() && !passwordsMatch) {
                    "Passwords do not match"
                } else uiState.errorMessage,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Create account button with good loader
            PermitelyButton(
                text = "Create Account",
                onClick = {
                    println("Create Account button clicked!")
                    println("Form validation: name=${name.isNotBlank()}, email=${email.isNotBlank()}, phone=${phoneNumber.isNotBlank()}, password=${password.length >= 6}, passwordsMatch=$passwordsMatch")
                    viewModel.clearError()
                    viewModel.signup(name, email, password, phoneNumber, selectedUserType)
                },
                loading = uiState.isLoading,
                enabled = name.isNotBlank() &&
                         email.isNotBlank() &&
                         phoneNumber.isNotBlank() &&
                         password.length >= 6 &&
                         passwordsMatch
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login link (simple)
            ModernLoginPrompt(onNavigateToLogin)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModernSignupLogo() {
    Box(
        modifier = Modifier.size(100.dp)
    ) {
        // Gradient background with glow
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Secondary, SecondaryVariant),
                        radius = 100f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Create Account",
                tint = OnSecondary,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernDropdownField(
    selectedValue: String,
    options: List<Pair<String, String>>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectionChange: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = options.find { it.first == selectedValue }?.second ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("User Type", color = if (expanded) Primary else TextSecondary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = BorderLight,
                focusedLabelColor = Primary,
                unfocusedLabelColor = TextSecondary
            ),
            shape = RoundedCornerShape(16.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelectionChange(value)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun ModernLoginPrompt(onNavigateToLogin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = onNavigateToLogin
            ) {
                Text(
                    text = "Sign In",
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
