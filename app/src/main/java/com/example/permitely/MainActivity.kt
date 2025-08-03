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
import dagger.hilt.android.AndroidEntryPoint
import com.example.permitely.ui.auth.AuthNavigation
import com.example.permitely.ui.theme.PermitelyTheme

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
                    var isAuthenticated by remember { mutableStateOf(false) }

                    if (isAuthenticated) {
                        // TODO: Navigate to main app content
                        // For now, show a placeholder
                        MainContent()
                    } else {
                        AuthNavigation(
                            onAuthSuccess = {
                                isAuthenticated = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainContent() {
    // Placeholder for main app content
    // This will be replaced with your main navigation
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // TODO: Implement main app navigation
    }
}