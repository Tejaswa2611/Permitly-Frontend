package com.example.permitely.data.repository

import android.util.Log
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

    companion object {
        private const val TAG = "GuardRepository"
    }

    /**
     * Scan visitor pass using the pass ID extracted from QR code
     * @param passId The pass ID extracted from QR code URL
     * @return Flow<Result<PassScanResult>> indicating success or failure
     */
    fun scanPass(passId: Int): Flow<Result<PassScanResult>> = flow {
        // Force immediate emission to test if flow block executes


        try {
            Log.d(TAG, "FLOW BLOCK STARTED - Entering scanPass flow for passId: $passId")
            Log.d(TAG, "FLOW BLOCK - Inside try block")
            Log.d(TAG, "Scanning pass with ID: $passId")
            Log.d(TAG, "Constructing URL - Base: https://permitly-production.up.railway.app/, Endpoint: api/guard/scan/$passId")
            Log.d(TAG, "Full expected URL: https://permitly-production.up.railway.app/api/guard/scan/$passId")

            // Get the access token
            Log.d(TAG, "About to get access token from tokenStorage")
            val accessToken = tokenStorage.getAccessToken().first()
            Log.d(TAG, "Access token retrieved: ${if (accessToken?.isNotEmpty() == true) "Token exists (${accessToken.take(10)}...)" else "Token is null/empty"}")

            if (accessToken.isNullOrEmpty()) {
                Log.d(TAG, "Access token is null/empty, returning auth error")
                emit(Result.success(PassScanResult(
                    isSuccess = false,
                    errorMessage = "Authentication required. Please login again."
                )))
                return@flow
            }

            Log.d(TAG, "About to make POST request to /api/guard/scan/$passId")
            Log.d(TAG, "Authorization header: Bearer ${accessToken.take(20)}...")
            Log.d(TAG, "Calling guardApiService.scanPass($passId, \"Bearer ...\")")
            Log.d(TAG, "Expected final URL: https://permitly-production.up.railway.app/api/guard/scan/$passId")
            Log.d(TAG, "Pass ID type: ${passId.javaClass.simpleName}, value: '$passId'")

            val response = guardApiService.scanPass(passId, "Bearer $accessToken")

            Log.d(TAG, "Scan response received")
            Log.d(TAG, "Scan response code: ${response.code()}")
            Log.d(TAG, "Scan response success: ${response.isSuccessful}")
            Log.d(TAG, "Response headers: ${response.headers()}")
            Log.d(TAG, "Response URL: ${response.raw().request.url}")

            // Log the error body for 400 responses to see actual backend error
            if (!response.isSuccessful && response.code() == 400) {
                val errorBody = response.errorBody()?.string()
                Log.d(TAG, "400 Error body: $errorBody")
            }

            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d(TAG, "API Response: status=${apiResponse?.status}, data=${apiResponse?.data}")

                // Your backend returns "status": "success", not "success": true
                if (apiResponse?.status == "success" && apiResponse.data != null) {
                    val scanData = apiResponse.data

                    Log.d(TAG, "Pass scan successful for visitor: ${scanData.visitor.name}")

                    val scanResult = PassScanResult(
                        isSuccess = true,
                        pass = scanData.pass,
                        visitor = scanData.visitor
                    )

                    emit(Result.success(scanResult))
                } else {
                    // Extract actual error message from your backend response
                    val errorMessage = when (apiResponse?.status) {
                        "error" -> apiResponse.message ?: "Unknown error occurred"
                        null -> "Failed to parse server response"
                        else -> "Unknown error occurred"
                    }
                    Log.d(TAG, "API returned error: $errorMessage")

                    val scanResult = PassScanResult(
                        isSuccess = false,
                        errorMessage = errorMessage
                    )

                    emit(Result.success(scanResult))
                }
            } else {
                // Handle HTTP error responses
                val errorBody = response.errorBody()?.string()
                Log.d(TAG, "HTTP Error ${response.code()}: $errorBody")

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
            Log.e(TAG, "Exception during pass scan: ${e.javaClass.simpleName}: ${e.message}", e)

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
            Log.d(TAG, "Failed to extract pass ID from QR code: $qrCodeUrl")
            null
        }
    }

    /**
     * Get today's guard statistics from API
     * @return Flow<Result<GuardTodayStatsData>> with today's visitor statistics
     */
    suspend fun getTodayStats(): Flow<Result<GuardTodayStatsData>> = flow {
        try {
            Log.d(TAG, "Fetching today's guard statistics")

            // Get the access token
            val accessToken = tokenStorage.getAccessToken().first()
            if (accessToken.isNullOrEmpty()) {
                emit(Result.failure(Exception("Authentication required. Please login again.")))
                return@flow
            }

            val response = guardApiService.getTodayStats("Bearer $accessToken")

            Log.d(TAG, "Stats response code: ${response.code()}")
            Log.d(TAG, "Stats response success: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d(TAG, "API Response: ${apiResponse?.status}, data=${apiResponse?.data}")

                if (apiResponse?.status == "success" && apiResponse.data != null) {
                    val statsResponse = apiResponse.data
                    val statsData = statsResponse.stats  // Extract from nested structure

                    Log.d(TAG, "Today's stats loaded successfully - Total: ${statsData.totalVisitors}")

                    emit(Result.success(statsData))
                } else {
                    val errorMessage = "Failed to load today's statistics"
                    Log.d(TAG, "API returned error: $errorMessage")
                    emit(Result.failure(Exception(errorMessage)))
                }
            } else {
                // Handle HTTP error responses
                val errorBody = response.errorBody()?.string()
                Log.d(TAG, "HTTP Error ${response.code()}: $errorBody")

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
            Log.e(TAG, "Exception during stats fetch: ${e.javaClass.simpleName}: ${e.message}", e)

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
