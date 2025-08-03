package com.example.permitely.data.network

import com.example.permitely.data.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service interface for authentication endpoints
 * Base URL: http://localhost:5500
 */
interface AuthApiService {

    /**
     * User login endpoint
     * POST /api/auth/login
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponseData>>

    /**
     * User registration endpoint
     * POST /api/auth/register
     */
    @POST("api/auth/register")
    suspend fun signup(@Body request: SignupRequest): Response<ApiResponse<SignupResponseData>>

    /**
     * Token refresh endpoint
     * POST /api/auth/refresh-token
     */
    @POST("api/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<TokenResponseData>>

    /**
     * User logout endpoint
     * POST /api/auth/logout
     */
    @POST("api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<ApiResponse<String>>
}
