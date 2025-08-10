package com.example.permitely.data.repository

import com.example.permitely.data.models.*
import com.example.permitely.data.network.AuthApiService
import com.example.permitely.data.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling authentication operations with real backend integration
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage
) {

    /**
     * Authenticate user with backend API
     */
    suspend fun login(loginRequest: LoginRequest): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            // Log the request data for debugging
            println("Login Request: email=${loginRequest.email}, password=[HIDDEN]")

            val response = authApiService.login(loginRequest)

            // Log the response for debugging
            println("Login Response Code: ${response.code()}")
            println("Login Response Success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                println("Login API Response: isSuccess=${apiResponse?.isSuccess}, data=${apiResponse?.data}")

                if (apiResponse?.isSuccess == true && apiResponse.data != null) {
                    val userData = apiResponse.data.user
                    val tokens = apiResponse.data.tokens

                    println("Login User Data: id=${userData.id}, name=${userData.name}, role=${userData.role}")
                    println("Login Tokens: accessToken length=${tokens.accessToken.length}, refreshToken length=${tokens.refreshToken.length}")

                    // Save tokens securely
                    tokenStorage.saveTokens(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken
                    )

                    // Save user information
                    tokenStorage.saveUserInfo(
                        id = userData.id.toString(), // Convert Int to String for storage
                        name = userData.name,
                        email = userData.email,
                        role = userData.role
                    )

                    emit(AuthResult.Success("Login successful"))
                } else {
                    emit(AuthResult.Error(apiResponse?.message ?: "Login failed"))
                }
            } else {
                // Get the actual error response body for debugging
                val errorBody = response.errorBody()?.string()
                println("Login HTTP Error ${response.code()}: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Invalid email or password format"
                    401 -> "Invalid credentials"
                    500 -> "Server error. Please try again later"
                    else -> "Login failed. Please try again"
                }
                emit(AuthResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            // Handle network errors with detailed logging
            println("Login Exception: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()

            val errorMessage = when (e) {
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Connection timeout"
                is com.google.gson.JsonSyntaxException -> "Invalid response format from server"
                is com.google.gson.JsonParseException -> "Failed to parse server response"
                else -> "Network error: ${e.message ?: "Unknown error"}"
            }
            emit(AuthResult.Error(errorMessage))
        }
    }

    /**
     * Register new user with backend API
     */
    suspend fun signup(signupRequest: SignupRequest): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            // Log the request data for debugging
            println("Signup Request: name=${signupRequest.name}, email=${signupRequest.email}, phone=${signupRequest.phoneNumber}, role=${signupRequest.role}")

            val response = authApiService.signup(signupRequest)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess == true && apiResponse.data != null) {
                    val userData = apiResponse.data.user
                    val tokens = apiResponse.data.tokens

                    // Save tokens securely
                    tokenStorage.saveTokens(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken
                    )

                    // Save user information
                    tokenStorage.saveUserInfo(
                        id = userData.id.toString(), // Convert Int to String for storage
                        name = userData.name,
                        email = userData.email,
                        role = userData.role
                    )

                    emit(AuthResult.Success("Account created successfully"))
                } else {
                    emit(AuthResult.Error(apiResponse?.message ?: "Signup failed"))
                }
            } else {
                // Get the actual error response body for debugging
                val errorBody = response.errorBody()?.string()
                println("Signup HTTP Error ${response.code()}: $errorBody")

                // Handle HTTP error codes
                val errorMessage = when (response.code()) {
                    400 -> {
                        // Try to parse the actual error message from backend
                        if (errorBody?.contains("email") == true) "Invalid email format"
                        else if (errorBody?.contains("password") == true) "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
                        else if (errorBody?.contains("phone") == true) "Invalid phone number format"
                        else if (errorBody?.contains("role") == true) "Invalid role. Must be admin, host, or guard"
                        else "Invalid input data. Please check your information"
                    }
                    409 -> "User already exists with this email"
                    500 -> "Server error. Please try again later"
                    else -> "Signup failed. Please try again"
                }
                emit(AuthResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            // Handle network errors
            val errorMessage = when (e) {
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Connection timeout"
                else -> e.message ?: "Network error occurred"
            }
            emit(AuthResult.Error(errorMessage))
        }
    }

    /**
     * Logout user and clear stored tokens
     */
    suspend fun logout(): Flow<AuthResult> = flow {
        try {
            println("DEBUG: Starting logout process")
            emit(AuthResult.Loading)

            // Get refresh token for logout request
            val refreshToken = tokenStorage.getRefreshToken().first()
            println("DEBUG: Retrieved refresh token: ${refreshToken?.take(10)}...")

            if (refreshToken != null) {
                try {
                    val response = authApiService.logout(LogoutRequest(refreshToken))
                    println("DEBUG: Logout API response: ${response.code()}")
                } catch (e: Exception) {
                    println("DEBUG: Logout API failed: ${e.message}")
                    // Continue with local cleanup even if API fails
                }
            }

            // Always clear local tokens regardless of API response
            // This ensures the user is logged out locally
            println("DEBUG: Clearing all tokens from storage")
            tokenStorage.clearAll()
            println("DEBUG: Logout completed successfully")
            emit(AuthResult.Success("Logged out successfully"))

        } catch (e: Exception) {
            println("DEBUG: Logout exception: ${e.message}")
            // Even if logout API fails, clear local tokens
            tokenStorage.clearAll()
            emit(AuthResult.Success("Logged out successfully"))
        }
    }

    /**
     * Check if user is currently logged in
     */
    fun isLoggedIn(): Flow<Boolean> = tokenStorage.isLoggedIn()

    /**
     * Get stored user information
     */
    fun getUserInfo(): Flow<TokenStorage.UserInfo?> = tokenStorage.getUserInfo()
}

/**
 * Sealed class representing authentication operation results
 */
sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
