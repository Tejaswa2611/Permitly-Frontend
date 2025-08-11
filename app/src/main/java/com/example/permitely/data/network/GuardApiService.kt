package com.example.permitely.data.network

import com.example.permitely.data.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API service interface for guard endpoints
 * Base URL: https://permitly-production.up.railway.app
 */
interface GuardApiService {

    /**
     * Scan visitor pass endpoint
     * POST /api/guard/scan/{passId}
     */
    @POST("api/guard/scan/{passId}")
    suspend fun scanPass(
        @Path("passId") passId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<PassScanResponseData>>

    /**
     * Get today's guard statistics
     * GET /api/guard/stats/today
     */
    @GET("api/guard/stats/today")
    suspend fun getTodayStats(
        @Header("Authorization") token: String
    ): Response<GuardTodayStatsResponse>
}
