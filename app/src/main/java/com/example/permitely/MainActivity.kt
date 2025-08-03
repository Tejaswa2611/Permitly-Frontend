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

    if (isAuthenticated) {
        // Show welcome screen with logout functionality
        WelcomeScreen(
            onLogout = {
                isAuthenticated = false
            }
        )
    } else {
        // Show authentication screens
        AuthNavigation(
            onAuthSuccess = {
                isAuthenticated = true
            }
        )
    }
}
