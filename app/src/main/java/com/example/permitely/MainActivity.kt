package com.example.permitely

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.ui.auth.AuthNavigation
import com.example.permitely.ui.theme.PermitelyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.example.permitely.ui.theme.Primary

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
    // Get AuthViewModel to handle proper authentication state
    val authViewModel = hiltViewModel<com.example.permitely.ui.auth.AuthViewModel>()

    // Single source of truth for authentication state
    var isAuthenticated by remember { mutableStateOf(false) }
    var userInfo by remember { mutableStateOf<com.example.permitely.data.storage.TokenStorage.UserInfo?>(null) }
    var currentScreen by remember { mutableStateOf("dashboard") }
    var selectedVisitor by remember { mutableStateOf<com.example.permitely.ui.host.Visitor?>(null) }
    var showSplash by remember { mutableStateOf(true) }
    var isCheckingAuth by remember { mutableStateOf(true) } // Add this to track auth check

    // Observe both authentication state and auth UI state
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // FIXED: Check authentication state on app start
    LaunchedEffect(Unit) {
        // Observe authentication state from TokenStorage
        authViewModel.isLoggedIn().collect { loggedIn ->
            println("DEBUG: isLoggedIn changed to: $loggedIn")
            isAuthenticated = loggedIn
            isCheckingAuth = false // Auth check completed

            // When logged out, immediately clear all user data
            if (!loggedIn) {
                println("DEBUG: User logged out, clearing all user data")
                currentScreen = "dashboard"
                selectedVisitor = null
                userInfo = null
            }
        }
    }

    // Separate LaunchedEffect for user info to avoid deadlock
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            // Get fresh user data from TokenStorage first, then let individual screens
            // fetch fresh API data as needed
            authViewModel.getUserInfo().collect { info ->
                println("DEBUG: Fresh userInfo for logged in user: $info")
                userInfo = info

                // Force refresh of any cached data when switching users
                if (info != null) {
                    println("DEBUG: User switched to: ${info.name} (${info.email})")
                }
            }
        } else {
            // Clear user info when not authenticated
            userInfo = null
        }
    }

    // Handle successful login from AuthViewModel
    LaunchedEffect(authUiState.isSuccess) {
        if (authUiState.isSuccess) {
            println("DEBUG: Login success detected, resetting AuthViewModel state")
            // Reset the AuthViewModel state after successful login
            authViewModel.resetState()
        }
    }

    // Proper logout function that clears all stored data
    fun performLogout() {
        println("DEBUG: Performing logout")
        // Clear local state immediately
        isAuthenticated = false
        currentScreen = "dashboard"
        selectedVisitor = null
        userInfo = null
        // Then call backend logout
        authViewModel.logout()
    }

    // Show splash screen first, then check if we need to show loading
    if (showSplash) {
        // Show splash screen first
        com.example.permitely.ui.splash.SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else if (isCheckingAuth) {
        // Show loading while checking authentication
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
    } else if (isAuthenticated) {
        // Show appropriate dashboard based on user type
        val userType = userInfo?.role ?: "host"

        // Debug logging to understand what's happening
        println("DEBUG: User authenticated - userInfo: $userInfo")
        println("DEBUG: User role: ${userInfo?.role}")
        println("DEBUG: Resolved userType: $userType")

        when (userType.lowercase()) {
            "host", "" -> {
                // Show host dashboard for both "host" role and empty/null role
                when (currentScreen) {
                    "dashboard" -> {
                        com.example.permitely.ui.host.HostDashboardScreen(
                            onCreateVisitor = { currentScreen = "create_visitor" },
                            onViewAllVisitors = { currentScreen = "visitors_list" },
                            onViewNotifications = { currentScreen = "notifications" },
                            onViewProfile = { currentScreen = "profile" },
                            onLogout = {
                                performLogout()
                            }
                        )
                    }
                    "create_visitor" -> {
                        // Use a unique key to force new ViewModel instance each time
                        key(currentScreen + System.currentTimeMillis()) {
                            com.example.permitely.ui.host.CreateVisitorScreen(
                                onNavigateBack = {
                                    currentScreen = "dashboard"
                                },
                                onVisitorCreated = { visitorData ->
                                    try {
                                        // Store the created visitor data and navigate to details
                                        selectedVisitor = com.example.permitely.ui.host.Visitor(
                                            id = visitorData.visitor.visitorId.toString(),
                                            name = visitorData.visitor.name,
                                            email = visitorData.visitor.email,
                                            phone = visitorData.visitor.phoneNumber,
                                            purpose = visitorData.visitor.purposeOfVisit,
                                            date = visitorData.visitor.createdAt.split("T")[0],
                                            time = visitorData.visitor.createdAt.split("T")[1].split("Z")[0],
                                            status = when(visitorData.visitor.status) {
                                                "PENDING" -> com.example.permitely.ui.host.VisitorStatus.PENDING
                                                "APPROVED" -> com.example.permitely.ui.host.VisitorStatus.APPROVED
                                                "REJECTED" -> com.example.permitely.ui.host.VisitorStatus.REJECTED
                                                "EXPIRED" -> com.example.permitely.ui.host.VisitorStatus.EXPIRED
                                                else -> com.example.permitely.ui.host.VisitorStatus.PENDING
                                            },
                                            createdAt = visitorData.visitor.createdAt,
                                            hasQRCode = visitorData.pass != null,
                                            qrCodeUrl = visitorData.pass?.qrCodeUrl,
                                            passId = visitorData.pass?.passId?.toString(),
                                            expiryTime = visitorData.pass?.expiryTime
                                        )
                                        currentScreen = "visitor_details"
                                    } catch (e: Exception) {
                                        println("Error processing visitor data: ${e.message}")
                                        e.printStackTrace()
                                        // On error, just go back to dashboard
                                        currentScreen = "dashboard"
                                    }
                                }
                            )
                        }
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
                                performLogout()
                            }
                        )
                    }
                }
            }
            "admin" -> {
                // TODO: Implement admin dashboard when available
                // For now, show host dashboard
                when (currentScreen) {
                    "dashboard" -> {
                        com.example.permitely.ui.host.HostDashboardScreen(
                            onCreateVisitor = { currentScreen = "create_visitor" },
                            onViewAllVisitors = { currentScreen = "visitors_list" },
                            onViewNotifications = { currentScreen = "notifications" },
                            onViewProfile = { currentScreen = "profile" },
                            onLogout = {
                                performLogout()
                            }
                        )
                    }
                    "create_visitor" -> {
                        com.example.permitely.ui.host.CreateVisitorScreen(
                            onNavigateBack = { currentScreen = "dashboard" },
                            onVisitorCreated = { visitorData ->
                                // Store the created visitor data and navigate to details
                                selectedVisitor = com.example.permitely.ui.host.Visitor(
                                    id = visitorData.visitor.visitorId.toString(), // Convert Int to String
                                    name = visitorData.visitor.name,
                                    email = visitorData.visitor.email,
                                    phone = visitorData.visitor.phoneNumber, // phoneNumber -> phone
                                    purpose = visitorData.visitor.purposeOfVisit, // purposeOfVisit -> purpose
                                    date = visitorData.visitor.createdAt.split("T")[0], // Extract date part
                                    time = visitorData.visitor.createdAt.split("T")[1].split("Z")[0], // Extract time part
                                    status = when(visitorData.visitor.status) {
                                        "PENDING" -> com.example.permitely.ui.host.VisitorStatus.PENDING
                                        "APPROVED" -> com.example.permitely.ui.host.VisitorStatus.APPROVED
                                        "REJECTED" -> com.example.permitely.ui.host.VisitorStatus.REJECTED
                                        "EXPIRED" -> com.example.permitely.ui.host.VisitorStatus.EXPIRED
                                        else -> com.example.permitely.ui.host.VisitorStatus.PENDING
                                    },
                                    createdAt = visitorData.visitor.createdAt,
                                    // Map QR code and pass information
                                    hasQRCode = visitorData.pass != null,
                                    qrCodeUrl = visitorData.pass?.qrCodeUrl,
                                    passId = visitorData.pass?.passId?.toString(),
                                    expiryTime = visitorData.pass?.expiryTime
                                )
                                currentScreen = "visitor_details"
                            }
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
                                performLogout()
                            }
                        )
                    }
                }
            }
            else -> {
                // Show host dashboard as default fallback instead of WelcomeScreen
                println("DEBUG: Unknown role '$userType', defaulting to host dashboard")
                when (currentScreen) {
                    "dashboard" -> {
                        com.example.permitely.ui.host.HostDashboardScreen(
                            onCreateVisitor = { currentScreen = "create_visitor" },
                            onViewAllVisitors = { currentScreen = "visitors_list" },
                            onViewNotifications = { currentScreen = "notifications" },
                            onViewProfile = { currentScreen = "profile" },
                            onLogout = {
                                performLogout()
                            }
                        )
                    }
                    "create_visitor" -> {
                        com.example.permitely.ui.host.CreateVisitorScreen(
                            onNavigateBack = { currentScreen = "dashboard" },
                            onVisitorCreated = { visitorData ->
                                // Store the created visitor data and navigate to details
                                selectedVisitor = com.example.permitely.ui.host.Visitor(
                                    id = visitorData.visitor.visitorId.toString(), // Convert Int to String
                                    name = visitorData.visitor.name,
                                    email = visitorData.visitor.email,
                                    phone = visitorData.visitor.phoneNumber, // phoneNumber -> phone
                                    purpose = visitorData.visitor.purposeOfVisit, // purposeOfVisit -> purpose
                                    date = visitorData.visitor.createdAt.split("T")[0], // Extract date part
                                    time = visitorData.visitor.createdAt.split("T")[1].split("Z")[0], // Extract time part
                                    status = when(visitorData.visitor.status) {
                                        "PENDING" -> com.example.permitely.ui.host.VisitorStatus.PENDING
                                        "APPROVED" -> com.example.permitely.ui.host.VisitorStatus.APPROVED
                                        "REJECTED" -> com.example.permitely.ui.host.VisitorStatus.REJECTED
                                        "EXPIRED" -> com.example.permitely.ui.host.VisitorStatus.EXPIRED
                                        else -> com.example.permitely.ui.host.VisitorStatus.PENDING
                                    },
                                    createdAt = visitorData.visitor.createdAt,
                                    // Map QR code and pass information
                                    hasQRCode = visitorData.pass != null,
                                    qrCodeUrl = visitorData.pass?.qrCodeUrl,
                                    passId = visitorData.pass?.passId?.toString(),
                                    expiryTime = visitorData.pass?.expiryTime
                                )
                                currentScreen = "visitor_details"
                            }
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
                                performLogout()
                            }
                        )
                    }
                }
            }
        }
    } else {
        // Show authentication screens only after confirming user is not logged in
        AuthNavigation(
            onAuthSuccess = {
                // Don't manually set isAuthenticated = true, let the Flow handle it
                currentScreen = "dashboard"
            }
        )
    }
}
