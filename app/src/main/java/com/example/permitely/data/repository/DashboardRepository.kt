package com.example.permitely.data.repository

import com.example.permitely.data.models.DashboardStats
import com.example.permitely.data.models.RecentVisitor
import com.example.permitely.data.network.DashboardApiService
import com.example.permitely.data.network.ProfileApiService
import com.example.permitely.data.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for dashboard-related operations
 * Handles data operations and provides a clean API for ViewModels
 */
@Singleton
class DashboardRepository @Inject constructor(
    private val dashboardApiService: DashboardApiService,
    private val profileApiService: ProfileApiService,
    private val tokenStorage: TokenStorage
) {

    /**
     * Fetch dashboard statistics from the API
     * @return Flow<Result<DashboardStats>> - Flow containing success/failure results
     */
    fun getDashboardStats(): Flow<Result<DashboardStats>> = flow {
        try {
            val token = tokenStorage.getAccessToken().first()
            if (token == null || token.isEmpty()) {
                emit(Result.failure(Exception("No access token available")))
                return@flow
            }

            val response = dashboardApiService.getDashboardStats("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Result.success(body.data))
                } else {
                    emit(Result.failure(Exception(body?.message ?: "Failed to fetch dashboard stats")))
                }
            } else {
                emit(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get fresh user profile data for dashboard
     * @return Flow<Result<String>> - Returns the user's current name
     */
    fun getUserName(): Flow<Result<String>> = flow {
        try {
            val token = tokenStorage.getAccessToken().first()
            if (token == null || token.isEmpty()) {
                emit(Result.failure(Exception("No access token available")))
                return@flow
            }

            // Try to get fresh data from profile API
            val response = profileApiService.getUserProfile("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    val profileData = body.data

                    // Update TokenStorage with fresh data to keep everything in sync
                    tokenStorage.saveUserInfo(
                        id = profileData.userId.toString(),
                        name = profileData.name,
                        email = profileData.email,
                        role = profileData.role
                    )

                    emit(Result.success(profileData.name))
                    return@flow
                }
            }

            // Fallback to TokenStorage
            val userInfo = tokenStorage.getUserInfo().first()
            if (userInfo != null) {
                emit(Result.success(userInfo.name))
            } else {
                emit(Result.failure(Exception("No user data available")))
            }
        } catch (e: Exception) {
            // Fallback to local storage on network error
            try {
                val userInfo = tokenStorage.getUserInfo().first()
                if (userInfo != null) {
                    emit(Result.success(userInfo.name))
                } else {
                    emit(Result.failure(e))
                }
            } catch (fallbackError: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    /**
     * Fetch recent visitors from the API
     * @return Flow<Result<List<RecentVisitor>>> - Flow containing success/failure results
     */
    fun getRecentVisitors(): Flow<Result<List<RecentVisitor>>> = flow {
        try {
            val token = tokenStorage.getAccessToken().first()
            if (token == null || token.isEmpty()) {
                emit(Result.failure(Exception("No access token available")))
                return@flow
            }

            val response = dashboardApiService.getRecentVisitors("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Result.success(body.data))
                } else {
                    emit(Result.failure(Exception(body?.message ?: "Failed to fetch recent visitors")))
                }
            } else {
                emit(Result.failure(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
