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
import androidx.compose.material.icons.filled.Person
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
 * Clean login screen with minimal animations for better performance
 */
@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Animation state for logo only
    var isLogoVisible by remember { mutableStateOf(false) }

    // Simple gradient background (no animation)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Primary.copy(alpha = 0.1f),
            PrimaryLight.copy(alpha = 0.05f),
            Background
        )
    )

    // Navigate to main app on successful login
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // Trigger logo animation only
    LaunchedEffect(Unit) {
        delay(100)
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
            Spacer(modifier = Modifier.height(80.dp))

            // Floating logo animation (kept as requested)
            AnimatedVisibility(
                visible = isLogoVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn()
            ) {
                ModernLogoCard()
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Simple welcome text (no animation)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to continue your journey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Form fields without animations for better performance
            PermitelyTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                keyboardType = KeyboardType.Email,
                isError = uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            PermitelyTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                keyboardType = KeyboardType.Password,
                isError = uiState.errorMessage != null,
                errorMessage = uiState.errorMessage,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login button with good loader
            PermitelyButton(
                text = "Sign In",
                onClick = {
                    viewModel.clearError()
                    viewModel.login(email, password)
                },
                loading = uiState.isLoading,
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Simple forgot password link
            TextButton(
                onClick = { /* Handle forgot password */ }
            ) {
                Text(
                    text = "Forgot Password?",
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Simple signup prompt
            ModernSignupPrompt(onNavigateToSignup)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModernLogoCard() {
    Card(
        modifier = Modifier.size(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Primary, PrimaryLight),
                        radius = 120f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Login",
                tint = OnPrimary,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
private fun ModernSignupPrompt(onNavigateToSignup: () -> Unit) {
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
                text = "Don't have an account? ",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = onNavigateToSignup
            ) {
                Text(
                    text = "Sign Up",
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
