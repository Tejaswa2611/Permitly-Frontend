package com.example.permitely.data.models

// ============================================================================
// Authentication Data Models for Permitely - Visitor Management System
// ============================================================================
// This file contains all data models related to user authentication,
// including login/signup requests, responses, and user information.

/**
 * Data class representing a login request to the authentication API.
 *
 * This model is used when a user attempts to sign in to the app.
 * It contains the minimal required information for authentication.
 *
 * @param email The user's email address (used as username)
 * @param password The user's password (will be encrypted before transmission)
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Data class representing a user registration request to the authentication API.
 *
 * This model contains all the information required to create a new user account
 * in the Permitely system. Different user types have different permissions and
 * access levels within the app.
 *
 * @param name The user's full name for display purposes
 * @param email The user's email address (must be unique)
 * @param password The user's chosen password (will be encrypted)
 * @param phoneNumber The user's phone number for SMS notifications
 * @param userType The type of user account (admin, host, guard)
 */
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val userType: String // Values: "admin", "host", "guard"
)

/**
 * Data class representing the response from authentication API calls.
 *
 * This unified response model is used for both login and signup operations,
 * providing consistent error handling and success indicators across the app.
 *
 * @param success Whether the authentication operation was successful
 * @param message Human-readable message describing the result (success or error)
 * @param token JWT token for authenticated requests (null if authentication failed)
 * @param user User information object (null if authentication failed)
 */
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,      // JWT token for API authentication
    val user: User? = null          // User details if authentication successful
)

/**
 * Data class representing a user in the Permitly system.
 *
 * This model contains the core user information that is stored locally
 * and used throughout the app for personalization and access control.
 *
 * @param id Unique identifier for the user in the system
 * @param name The user's display name
 * @param email The user's email address
 * @param phoneNumber The user's phone number for notifications
 * @param userType The user's role (determines app permissions and UI)
 * @param isVerified Whether the user's account has been verified (email/phone)
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val userType: String,           // "admin", "host", "guard"
    val isVerified: Boolean = false // Account verification status
)
