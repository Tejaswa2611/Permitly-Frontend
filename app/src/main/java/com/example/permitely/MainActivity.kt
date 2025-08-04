package com.example.permitely

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.permitely.ui.auth.AuthNavigation
import com.example.permitely.ui.main.WelcomeScreen
import com.example.permitely.ui.theme.PermitelyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermitelyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermitelyApp()
                }
            }
        }
    }
}

@Composable
private fun PermitelyApp() {
    // Simple state management approach for authentication flow
    var isAuthenticated by remember { mutableStateOf(false) }
    var userType by remember { mutableStateOf("host") } // Default to host for demo
    var currentScreen by remember { mutableStateOf("dashboard") } // Track current screen
    var selectedVisitor by remember { mutableStateOf<com.example.permitely.ui.host.Visitor?>(null) } // Track selected visitor

    if (isAuthenticated) {
        // Show appropriate dashboard based on user type
        when (userType) {
            "host" -> {
                when (currentScreen) {
                    "dashboard" -> {
                        com.example.permitely.ui.host.HostDashboardScreen(
                            onCreateVisitor = { currentScreen = "create_visitor" },
                            onViewAllVisitors = { currentScreen = "visitors_list" },
                            onViewNotifications = { currentScreen = "notifications" },
                            onViewProfile = { currentScreen = "profile" },
                            onLogout = {
                                isAuthenticated = false
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    "create_visitor" -> {
                        com.example.permitely.ui.host.CreateVisitorScreen(
                            onNavigateBack = { currentScreen = "dashboard" },
                            onVisitorCreated = { currentScreen = "visitors_list" }
                        )
                    }
                    "visitors_list" -> {
                        com.example.permitely.ui.host.VisitorsListScreen(
                            onNavigateBack = { currentScreen = "dashboard" },
                            onVisitorClick = { visitor ->
                                selectedVisitor = visitor
                                currentScreen = "visitor_details"
                            }
                        )
                    }
                    "visitor_details" -> {
                        selectedVisitor?.let { visitor ->
                            com.example.permitely.ui.host.VisitorDetailsScreen(
                                visitor = visitor,
                                onNavigateBack = { currentScreen = "visitors_list" },
                                onEditVisitor = { visitorToEdit ->
                                    selectedVisitor = visitorToEdit
                                    // TODO: Navigate to edit screen when implemented
                                    // currentScreen = "edit_visitor"
                                },
                                onDeleteVisitor = { visitorId ->
                                    // TODO: Implement visitor deletion logic
                                    currentScreen = "visitors_list"
                                },
                                onGeneratePass = { visitorId ->
                                    // TODO: Implement pass generation logic
                                },
                                onShareQRCode = {
                                    // TODO: Implement QR code sharing logic
                                }
                            )
                        }
                    }
                    "notifications" -> {
                        com.example.permitely.ui.host.NotificationsScreen(
                            onNavigateBack = { currentScreen = "dashboard" }
                        )
                    }
                    "profile" -> {
                        com.example.permitely.ui.host.HostProfileScreen(
                            onNavigateBack = { currentScreen = "dashboard" },
                            onLogout = {
                                isAuthenticated = false
                                currentScreen = "dashboard"
                            }
                        )
                    }
                }
            }
            else -> {
                // Fallback to welcome screen for other user types
                WelcomeScreen(
                    onLogout = {
                        isAuthenticated = false
                        currentScreen = "dashboard"
                    }
                )
            }
        }
    } else {
        // Show authentication screens
        AuthNavigation(
            onAuthSuccess = {
                isAuthenticated = true
                // In real implementation, you'd get user type from login response
                userType = "host" // For demo purposes
                currentScreen = "dashboard"
            }
        )
    }
}
