package com.example.permitely.data.network

import com.example.permitely.data.models.ProfileResponse
import com.example.permitely.data.models.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for user profile endpoints
 * Matches the actual backend API structure
 */
interface ProfileApiService {

    /**
     * Get current user's profile
     * GET /api/users/profile
     */
    @GET("api/users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    /**
     * Update current user's profile
     * PUT /api/users/profile
     */
    @PUT("api/users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>
}
