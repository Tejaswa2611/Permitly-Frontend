package com.example.permitely.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Navigation controller for authentication flow.
 * Handles routing between login and signup screens.
 */
@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    onAuthSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "login"  // Start with login screen
    ) {
        // Login screen route
        composable("login") {
            LoginScreen(
                onNavigateToSignup = {
                    navController.navigate("signup")
                },
                onLoginSuccess = onAuthSuccess
            )
        }

        // Signup screen route
        composable("signup") {
            SignupScreen(
                onNavigateToLogin = {
                    navController.popBackStack()  // Go back to login
                },
                onSignupSuccess = onAuthSuccess
            )
        }
    }
}
