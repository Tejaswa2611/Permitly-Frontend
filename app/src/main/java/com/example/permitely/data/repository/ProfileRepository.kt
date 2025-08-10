package com.example.permitely.data.repository

import com.example.permitely.data.models.UpdateProfileRequest
import com.example.permitely.data.models.UserProfile
import com.example.permitely.data.models.UserCount
import com.example.permitely.data.network.ProfileApiService
import com.example.permitely.data.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user profile operations
 * Handles both API calls and local storage fallback
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val tokenStorage: TokenStorage
) {

    /**
     * Get user profile with fallback to local storage
     * @return Flow<Result<UserProfile>>
     */
    fun getUserProfile(): Flow<Result<UserProfile>> = flow {
        try {
            val token = tokenStorage.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                emit(Result.failure(Exception("No access token available")))
                return@flow
            }

            // Try to get profile from API first
            val response = profileApiService.getUserProfile("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Result.success(body.data))
                    return@flow
                }
            }

            // Fallback to TokenStorage if API fails
            val userInfo = tokenStorage.getUserInfo().first()
            if (userInfo != null) {
                val fallbackProfile = UserProfile(
                    userId = userInfo.id.toIntOrNull() ?: 0,
                    name = userInfo.name,
                    email = userInfo.email,
                    phoneNumber = null,
                    role = userInfo.role,
                    createdAt = "",
                    count = UserCount(visitors = 0, passes = 0)
                )
                emit(Result.success(fallbackProfile))
            } else {
                emit(Result.failure(Exception("No profile data available")))
            }
        } catch (e: Exception) {
            // Fallback to local storage on network error
            try {
                val userInfo = tokenStorage.getUserInfo().first()
                if (userInfo != null) {
                    val fallbackProfile = UserProfile(
                        userId = userInfo.id.toIntOrNull() ?: 0,
                        name = userInfo.name,
                        email = userInfo.email,
                        phoneNumber = null,
                        role = userInfo.role,
                        createdAt = "",
                        count = UserCount(visitors = 0, passes = 0)
                    )
                    emit(Result.success(fallbackProfile))
                } else {
                    emit(Result.failure(e))
                }
            } catch (fallbackError: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    /**
     * Update user profile via API
     * @param request UpdateProfileRequest
     * @return Flow<Result<UserProfile>>
     */
    fun updateUserProfile(request: UpdateProfileRequest): Flow<Result<UserProfile>> = flow {
        val token = tokenStorage.getAccessToken().first()
        if (token.isNullOrEmpty()) {
            emit(Result.failure(Exception("No access token available")))
            return@flow
        }

        val response = profileApiService.updateUserProfile("Bearer $token", request)

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.success == true && body.data != null) {
                // Update local storage with fresh data
                try {
                    tokenStorage.saveUserInfo(
                        id = body.data.userId.toString(),
                        name = body.data.name,
                        email = body.data.email,
                        role = body.data.role
                    )
                } catch (e: Exception) {
                    // Continue even if local storage update fails
                }
                emit(Result.success(body.data))
            } else {
                emit(Result.failure(Exception(body?.message ?: "Failed to update profile")))
            }
        } else {
            emit(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
        }
    }.catch { exception ->
        emit(Result.failure(exception))
    }
}
