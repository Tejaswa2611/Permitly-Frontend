package com.example.permitely.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.ui.auth.AuthViewModel
import com.example.permitely.ui.theme.*

/**
 * Welcome screen shown after successful login
 * Displays user information and logout option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    // Observe user info
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Welcome Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Icon
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary),
                    shape = RoundedCornerShape(40.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = OnPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Welcome Message
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User Information
                userInfo?.let { user ->
                    Text(
                        text = "You are logged in as",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (user.role) {
                                "admin" -> Error.copy(alpha = 0.1f)
                                "host" -> Primary.copy(alpha = 0.1f)
                                "guard" -> Success.copy(alpha = 0.1f)
                                else -> Secondary.copy(alpha = 0.1f)
                            }
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = user.role.uppercase(),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (user.role) {
                                "admin" -> Error
                                "host" -> Primary
                                "guard" -> Success
                                else -> Secondary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Details
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                } ?: run {
                    // Loading state
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Error
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Info
        Text(
            text = "Permitely - Visitor Management System",
            style = MaterialTheme.typography.bodySmall,
            color = TextDisabled,
            textAlign = TextAlign.Center
        )
    }
}
