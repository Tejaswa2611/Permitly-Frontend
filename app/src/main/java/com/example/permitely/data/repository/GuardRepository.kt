package com.example.permitely.data.repository

import com.example.permitely.data.models.*
import com.example.permitely.data.network.GuardApiService
import com.example.permitely.data.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling guard operations with backend integration
 */
@Singleton
class GuardRepository @Inject constructor(
    private val guardApiService: GuardApiService,
    private val tokenStorage: TokenStorage
) {

    /**
     * Scan visitor pass using the pass ID extracted from QR code
     * @param passId The pass ID extracted from QR code URL
     * @return Flow<Result<PassScanResult>> indicating success or failure
     */
    suspend fun scanPass(passId: Int): Flow<Result<PassScanResult>> = flow {
        try {
            println("GuardRepository: Scanning pass with ID: $passId")

            // Get the access token
            val accessToken = tokenStorage.getAccessToken().first()
            if (accessToken.isNullOrEmpty()) {
                emit(Result.success(PassScanResult(
                    isSuccess = false,
                    errorMessage = "Authentication required. Please login again."
                )))
                return@flow
            }

            val response = guardApiService.scanPass(passId, "Bearer $accessToken")

            println("GuardRepository: Scan response code: ${response.code()}")
            println("GuardRepository: Scan response success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                println("GuardRepository: API Response: isSuccess=${apiResponse?.isSuccess}, data=${apiResponse?.data}")

                if (apiResponse?.isSuccess == true && apiResponse.data != null) {
                    val scanData = apiResponse.data

                    println("GuardRepository: Pass scan successful for visitor: ${scanData.visitor.name}")

                    val scanResult = PassScanResult(
                        isSuccess = true,
                        pass = scanData.pass,
                        visitor = scanData.visitor
                    )

                    emit(Result.success(scanResult))
                } else {
                    val errorMessage = apiResponse?.message ?: "Unknown error occurred"
                    println("GuardRepository: API returned error: $errorMessage")

                    val scanResult = PassScanResult(
                        isSuccess = false,
                        errorMessage = errorMessage
                    )

                    emit(Result.success(scanResult))
                }
            } else {
                // Handle HTTP error responses
                val errorBody = response.errorBody()?.string()
                println("GuardRepository: HTTP Error ${response.code()}: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Invalid pass ID format"
                    404 -> "Pass not found"
                    409 -> {
                        // Try to parse specific error messages from response body
                        when {
                            errorBody?.contains("already processed") == true -> "Pass already processed"
                            errorBody?.contains("already approved") == true -> "Visitor is already approved"
                            errorBody?.contains("expired") == true -> "Pass has expired"
                            else -> "Pass cannot be processed"
                        }
                    }
                    500 -> "Server error. Please try again later"
                    else -> "Failed to scan pass. Please try again"
                }

                val scanResult = PassScanResult(
                    isSuccess = false,
                    errorMessage = errorMessage
                )

                emit(Result.success(scanResult))
            }
        } catch (e: Exception) {
            println("GuardRepository: Exception during pass scan: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()

            val errorMessage = when (e) {
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Connection timeout"
                is com.google.gson.JsonSyntaxException -> "Invalid response format from server"
                is com.google.gson.JsonParseException -> "Failed to parse server response"
                else -> "Network error: ${e.message ?: "Unknown error"}"
            }

            val scanResult = PassScanResult(
                isSuccess = false,
                errorMessage = errorMessage
            )

            emit(Result.success(scanResult))
        }
    }

    /**
     * Extract pass ID from QR code URL
     * Expected format: https://yourbackend.com/api/guard/scan/789
     * @param qrCodeUrl The complete QR code URL
     * @return pass ID if valid URL, null otherwise
     */
    fun extractPassIdFromQrCode(qrCodeUrl: String): Int? {
        return try {
            // Extract pass ID from URL pattern: .../api/guard/scan/{passId}
            val regex = Regex(".*/api/guard/scan/(\\d+)/?.*")
            val matchResult = regex.find(qrCodeUrl)
            matchResult?.groupValues?.get(1)?.toIntOrNull()
        } catch (e: Exception) {
            println("GuardRepository: Failed to extract pass ID from QR code: $qrCodeUrl")
            null
        }
    }

    /**
     * Get today's guard statistics from API
     * @return Flow<Result<GuardTodayStatsData>> with today's visitor statistics
     */
    suspend fun getTodayStats(): Flow<Result<GuardTodayStatsData>> = flow {
        try {
            println("GuardRepository: Fetching today's guard statistics")

            // Get the access token
            val accessToken = tokenStorage.getAccessToken().first()
            if (accessToken.isNullOrEmpty()) {
                emit(Result.failure(Exception("Authentication required. Please login again.")))
                return@flow
            }

            val response = guardApiService.getTodayStats("Bearer $accessToken")

            println("GuardRepository: Stats response code: ${response.code()}")
            println("GuardRepository: Stats response success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                println("GuardRepository: API Response: isSuccess=${apiResponse?.isSuccess}, data=${apiResponse?.data}")

                if (apiResponse?.isSuccess == true && apiResponse.data != null) {
                    val statsResponse = apiResponse.data
                    val statsData = statsResponse.stats  // Extract from nested structure

                    println("GuardRepository: Today's stats loaded successfully - Total: ${statsData.totalVisitors}")

                    emit(Result.success(statsData))
                } else {
                    val errorMessage = apiResponse?.message ?: "Failed to load today's statistics"
                    println("GuardRepository: API returned error: $errorMessage")
                    emit(Result.failure(Exception(errorMessage)))
                }
            } else {
                // Handle HTTP error responses
                val errorBody = response.errorBody()?.string()
                println("GuardRepository: HTTP Error ${response.code()}: $errorBody")

                val errorMessage = when (response.code()) {
                    401 -> "Unauthorized access. Please login again"
                    403 -> "Access denied. Guard permissions required"
                    404 -> "Statistics endpoint not found"
                    500 -> "Server error. Please try again later"
                    else -> "Failed to load statistics. Please try again"
                }

                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            println("GuardRepository: Exception during stats fetch: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()

            val errorMessage = when (e) {
                is java.net.UnknownHostException -> "No internet connection"
                is java.net.SocketTimeoutException -> "Connection timeout"
                is com.google.gson.JsonSyntaxException -> "Invalid response format from server"
                is com.google.gson.JsonParseException -> "Failed to parse server response"
                else -> "Network error: ${e.message ?: "Unknown error"}"
            }

            emit(Result.failure(Exception(errorMessage)))
        }
    }
}
