package com.example.permitely.data.models

import com.google.gson.annotations.SerializedName

// ============================================================================
// Authentication Data Models for Permitly - Visitor Management System
// ============================================================================
// Updated to match backend API structure exactly

/**
 * Login request matching backend /api/auth/signin endpoint
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Signup request matching backend /api/auth/signup endpoint
 */
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val role: String // "admin", "host", "guard"
)

/**
 * Base API response structure from backend
 */
data class ApiResponse<T>(
    @SerializedName("status") val success: String,  // Backend sends "status": "success"
    val message: String? = null,
    val data: T? = null
) {
    // Helper property to check if response is successful
    val isSuccess: Boolean get() = success == "success"
}

/**
 * Token refresh request
 */
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Logout request
 */
data class LogoutRequest(
    val refreshToken: String
)

/**
 * Login response data structure from backend
 */
data class LoginResponseData(
    val user: UserData,
    val tokens: TokenData
)

/**
 * Signup response data structure from backend
 */
data class SignupResponseData(
    val user: UserData,
    val tokens: TokenData
)

/**
 * Token refresh response data structure
 */
data class TokenResponseData(
    val tokens: TokenData
)

/**
 * User data from backend response
 */
data class UserData(
    val id: Int,  // Changed from String to Int to match backend
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("phone_number") val phoneNumber: String
)

/**
 * Token data structure
 */
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)

/**
 * User data structure (when returned from backend)
 */
data class User(
    val id: Int,  // Changed from String to Int to match backend
    val name: String,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val role: String, // "admin", "host", "guard"
    val isVerified: Boolean = false
)
