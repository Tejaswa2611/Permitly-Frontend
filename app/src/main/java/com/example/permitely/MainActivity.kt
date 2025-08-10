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
import androidx.navigation.compose.rememberNavController
import com.example.permitely.ui.auth.AuthNavigation
import com.example.permitely.ui.main.MainNavigation
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

    // Navigation controllers
    val authNavController = rememberNavController()
    val mainNavController = rememberNavController()

    // Authentication state
    var isAuthenticated by remember { mutableStateOf(false) }
    var userInfo by remember { mutableStateOf<com.example.permitely.data.storage.TokenStorage.UserInfo?>(null) }
    var showSplash by remember { mutableStateOf(true) }
    var isCheckingAuth by remember { mutableStateOf(true) }

    // Observe authentication state and auth UI state
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Check authentication state on app start
    LaunchedEffect(Unit) {
        authViewModel.isLoggedIn().collect { loggedIn ->
            println("DEBUG: isLoggedIn changed to: $loggedIn")
            isAuthenticated = loggedIn
            isCheckingAuth = false

            // When logged out, immediately clear all user data
            if (!loggedIn) {
                println("DEBUG: User logged out, clearing all user data")
                userInfo = null
            }
        }
    }

    // Get user info when authenticated
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            authViewModel.getUserInfo().collect { info ->
                println("DEBUG: Fresh userInfo for logged in user: $info")
                userInfo = info

                if (info != null) {
                    println("DEBUG: User switched to: ${info.name} (${info.email})")
                }
            }
        } else {
            userInfo = null
        }
    }

    // Handle successful login from AuthViewModel
    LaunchedEffect(authUiState.isSuccess) {
        if (authUiState.isSuccess) {
            println("DEBUG: Login success detected, resetting AuthViewModel state")
            authViewModel.resetState()
        }
    }

    // Proper logout function that clears all stored data
    fun performLogout() {
        println("DEBUG: Performing logout")
        // Clear local state immediately
        isAuthenticated = false
        userInfo = null
        // Then call backend logout
        authViewModel.logout()
    }

    // Show splash screen first, then check if we need to show loading
    if (showSplash) {
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
        // Show main app navigation with proper back button handling
        MainNavigation(
            navController = mainNavController,
            userInfo = userInfo,
            onLogout = ::performLogout
        )
    } else {
        // Show authentication flow
        AuthNavigation(
            navController = authNavController,
            onAuthSuccess = {
                // Authentication successful - the LaunchedEffect above will handle the state change
                println("DEBUG: Authentication successful")
            }
        )
    }
}
