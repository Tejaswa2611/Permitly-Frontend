package com.example.permitely.data.repository

import com.example.permitely.data.models.CreateVisitorRequest
import com.example.permitely.data.models.CreateVisitorResponseData
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
                // Handle HTTP error responses
                val errorBody = response.errorBody()?.string()
                println("DEBUG: Create visitor HTTP error ${response.code()}: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> {
                        // Parse specific validation errors from backend
                        when {
                            errorBody?.contains("email") == true -> "Invalid email format"
                            errorBody?.contains("phone") == true -> "Invalid phone number format"
                            errorBody?.contains("name") == true -> "Invalid visitor name"
                            errorBody?.contains("purpose") == true -> "Purpose of visit is required"
                            errorBody?.contains("expiry_time") == true -> "Invalid expiry time format"
                            else -> "Invalid visitor information provided"
                        }
                    }
                    409 -> "This visitor already has an active or pending visit. Cannot create multiple active visits."
                    401 -> "Authentication failed. Please login again."
                    403 -> "You don't have permission to create visitors"
                    500 -> "Server error. Please try again later"
                    else -> "Failed to create visitor. Please try again"
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
}
