package com.example.permitely.data.network

import com.example.permitely.data.models.*
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API service interface for guard endpoints
 * Base URL: http://10.0.2.2:5500
 */
interface GuardApiService {

    /**
     * Scan visitor pass endpoint
     * POST /api/guard/scan/{passId}
     */
    @POST("api/guard/scan/{passId}")
    suspend fun scanPass(@Path("passId") passId: Int): Response<ApiResponse<PassScanResponseData>>
}
