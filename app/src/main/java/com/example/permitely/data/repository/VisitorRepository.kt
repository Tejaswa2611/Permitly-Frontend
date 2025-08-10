package com.example.permitely.data.repository

import com.example.permitely.data.models.CreateVisitorRequest
import com.example.permitely.data.models.CreateVisitorResponseData
import com.example.permitely.data.models.VisitorData
import com.example.permitely.data.network.VisitorApiService
import com.example.permitely.data.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for visitor-related operations
 * Handles visitor creation, management, and API communications
 */
@Singleton
class VisitorRepository @Inject constructor(
    private val visitorApiService: VisitorApiService,
    private val tokenStorage: TokenStorage
) {

    /**
     * Create a new visitor
     * @param request CreateVisitorRequest containing visitor details
     * @return Flow<Result<CreateVisitorResponseData>> - Flow containing success/failure results
     */
    fun createVisitor(request: CreateVisitorRequest): Flow<Result<CreateVisitorResponseData>> = flow {
        try {
            val token = tokenStorage.getAccessToken().first()
            if (token == null || token.isEmpty()) {
                emit(Result.failure(Exception("No access token available")))
                return@flow
            }

            println("DEBUG: Creating visitor - Name: ${request.name}, Email: ${request.email}")

            val response = visitorApiService.createVisitor("Bearer $token", request)

            println("DEBUG: Create visitor API response code: ${response.code()}")
            println("DEBUG: Create visitor API response successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                println("DEBUG: Create visitor response body: $body")

                if (body?.status == "success" && body.data != null) {
                    println("DEBUG: Visitor created successfully - ID: ${body.data.visitor.visitorId}")
                    emit(Result.success(body.data))
                } else {
                    val errorMessage = body?.message ?: "Failed to create visitor"
                    println("DEBUG: Create visitor failed: $errorMessage")
                    emit(Result.failure(Exception(errorMessage)))
                }
            } else {
                // Handle HTTP error responses - Parse JSON error response from backend
                val errorBody = response.errorBody()?.string()
                println("DEBUG: Create visitor HTTP error ${response.code()}: $errorBody")

                // Try to parse the JSON error response from backend
                val errorMessage = try {
                    if (errorBody != null) {
                        // Parse the JSON error response: {"status": "error", "message": "error message"}
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.google.gson.JsonObject::class.java)

                        // Extract the message field from the error response
                        val backendMessage = errorResponse.get("message")?.asString

                        if (!backendMessage.isNullOrEmpty()) {
                            backendMessage // Use the exact error message from backend
                        } else {
                            // Fallback to HTTP status code based messages
                            when (response.code()) {
                                400 -> "Invalid visitor information provided"
                                409 -> "This visitor already has an active or pending visit"
                                401 -> "Authentication failed. Please login again"
                                403 -> "You don't have permission to create visitors"
                                500 -> "Server error. Please try again later"
                                else -> "Failed to create visitor. Please try again"
                            }
                        }
                    } else {
                        "Failed to create visitor. Please try again"
                    }
                } catch (jsonException: Exception) {
                    println("DEBUG: Failed to parse JSON error response: ${jsonException.message}")
                    // Fallback to HTTP status code based messages if JSON parsing fails
                    when (response.code()) {
                        400 -> "Invalid visitor information provided"
                        409 -> "This visitor already has an active or pending visit"
                        401 -> "Authentication failed. Please login again"
                        403 -> "You don't have permission to create visitors"
                        500 -> "Server error. Please try again later"
                        else -> "Failed to create visitor. Please try again"
                    }
                }

                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            println("DEBUG: Create visitor exception: ${e.message}")
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

    /**
     * Get all visitors for the authenticated host
     * @param status Optional filter by visitor status
     * @return Flow<Result<List<VisitorData>>> - Flow containing list of visitors or error
     */
    fun getAllVisitors(status: String? = null): Flow<Result<List<VisitorData>>> = flow {
        try {
            val token = tokenStorage.getAccessToken().first()
            if (token == null || token.isEmpty()) {
                emit(Result.failure(Exception("No access token available")))
                return@flow
            }

            println("DEBUG: Getting all visitors - Status filter: $status")

            val response = visitorApiService.getAllVisitors("Bearer $token", status)

            println("DEBUG: Get visitors API response code: ${response.code()}")
            println("DEBUG: Get visitors API response successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val body = response.body()
                println("DEBUG: Get visitors response body: $body")

                if (body?.status == "success" && body.data != null) {
                    println("DEBUG: Found ${body.data.visitors.size} visitors")
                    emit(Result.success(body.data.visitors))
                } else {
                    val errorMessage = body?.message ?: "Failed to fetch visitors"
                    println("DEBUG: Get visitors failed: $errorMessage")
                    emit(Result.failure(Exception(errorMessage)))
                }
            } else {
                // Handle HTTP error responses - Parse JSON error response from backend
                val errorBody = response.errorBody()?.string()
                println("DEBUG: Get visitors HTTP error ${response.code()}: $errorBody")

                // Try to parse the JSON error response from backend
                val errorMessage = try {
                    if (errorBody != null) {
                        // Parse the JSON error response: {"status": "error", "message": "error message"}
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.google.gson.JsonObject::class.java)

                        // Extract the message field from the error response
                        val backendMessage = errorResponse.get("message")?.asString

                        if (!backendMessage.isNullOrEmpty()) {
                            backendMessage // Use the exact error message from backend
                        } else {
                            // Fallback to HTTP status code based messages
                            when (response.code()) {
                                400 -> "Invalid request"
                                401 -> "Authentication failed. Please login again"
                                403 -> "You don't have permission to view visitors"
                                404 -> "No visitors found"
                                500 -> "Server error. Please try again later"
                                else -> "Failed to fetch visitors. Please try again"
                            }
                        }
                    } else {
                        "Failed to fetch visitors. Please try again"
                    }
                } catch (jsonException: Exception) {
                    println("DEBUG: Failed to parse JSON error response: ${jsonException.message}")
                    // Fallback to HTTP status code based messages if JSON parsing fails
                    when (response.code()) {
                        400 -> "Invalid request"
                        401 -> "Authentication failed. Please login again"
                        403 -> "You don't have permission to view visitors"
                        404 -> "No visitors found"
                        500 -> "Server error. Please try again later"
                        else -> "Failed to fetch visitors. Please try again"
                    }
                }

                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            println("DEBUG: Get visitors exception: ${e.message}")
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
