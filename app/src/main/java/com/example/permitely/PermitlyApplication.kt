package com.example.permitely

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// ============================================================================
// Application Class for Permitely - Visitor Management System
// ============================================================================
// This is the main application class that serves as the entry point for the
// entire app and initializes dependency injection with Hilt.

/**
 * Main Application class for the Permitely visitor management system.
 *
 * This class is responsible for:
 * - Initializing Hilt dependency injection framework
 * - Setting up application-wide configurations
 * - Managing global app state and resources
 * - Providing application context throughout the app
 *
 * The @HiltAndroidApp annotation triggers Hilt's code generation and creates
 * an application-level dependency container that serves as the parent for all
 * other dependency containers in the app.
 */
@HiltAndroidApp
class PermitlyApplication : Application() {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects have been created.
     *
     * This is the perfect place to initialize:
     * - Logging frameworks
     * - Crash reporting tools
     * - Performance monitoring
     * - Global configurations
     * - Database initialization
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize any application-wide components here
        // TODO: Add initialization for:
        // - Timber logging
        // - Firebase crashlytics
        // - Room database pre-population
        // - WorkManager for background tasks
        // - Network configuration
    }
}
