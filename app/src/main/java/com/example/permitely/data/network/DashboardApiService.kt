package com.example.permitely.data.network

import com.example.permitely.data.models.DashboardStatsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Retrofit API service interface for dashboard endpoints
 */
interface DashboardApiService {

    /**
     * Get dashboard statistics
     * GET /api/user/dashboard/stats
     */
    @GET("api/users/dashboard/stats")
    suspend fun getDashboardStats(
        @Header("Authorization") token: String
    ): Response<DashboardStatsResponse>
}
