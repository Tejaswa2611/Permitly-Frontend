package com.example.permitely.data.network

import com.example.permitely.data.models.CreateVisitorRequest
import com.example.permitely.data.models.CreateVisitorResponse
import com.example.permitely.data.models.GetAllVisitorsResponse
import com.example.permitely.data.models.GetVisitorByIdResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for visitor endpoints
 */
interface VisitorApiService {

    /**
     * Create a new visitor
     * POST /api/visitors/
     */
    @POST("api/visitors/")
    suspend fun createVisitor(
        @Header("Authorization") token: String,
        @Body request: CreateVisitorRequest
    ): Response<CreateVisitorResponse>

    /**
     * Get all visitors for the authenticated host
     * GET /api/visitors
     * @param status Optional filter by visitor status (PENDING, APPROVED, REJECTED, EXPIRED)
     */
    @GET("api/visitors")
    suspend fun getAllVisitors(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): Response<GetAllVisitorsResponse>

    /**
     * Get visitor details by ID
     * GET /api/visitors/{id}
     */
    @GET("api/visitors/{id}")
    suspend fun getVisitorById(
        @Header("Authorization") token: String,
        @Path("id") visitorId: String
    ): Response<GetVisitorByIdResponse>

    // TODO: Add other visitor endpoints as needed
    // PUT /api/visitors/{id} - Update visitor
    // DELETE /api/visitors/{id} - Delete visitor
}
