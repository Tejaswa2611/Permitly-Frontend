package com.example.permitely.data.models

import com.google.gson.annotations.SerializedName

/**
 * Create visitor request model
 */
data class CreateVisitorRequest(
    val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val email: String,
    @SerializedName("purpose_of_visit") val purposeOfVisit: String,
    @SerializedName("expiry_time") val expiryTime: String? = null // Optional ISO string format
)

/**
 * Visitor data model from API response
 */
data class VisitorData(
    @SerializedName("visitor_id") val visitorId: Int,
    val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val email: String,
    @SerializedName("purpose_of_visit") val purposeOfVisit: String,
    @SerializedName("host_id") val hostId: Int,
    val status: String, // PENDING, APPROVED, REJECTED, etc.
    @SerializedName("entry_time") val entryTime: String?,
    @SerializedName("exit_time") val exitTime: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val host: HostInfo
)

/**
 * Host information in visitor response
 */
data class HostInfo(
    val name: String,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String
)

/**
 * Pass data model from API response
 */
data class PassData(
    @SerializedName("pass_id") val passId: Int,
    @SerializedName("visitor_id") val visitorId: Int,
    @SerializedName("qr_code_data") val qrCodeData: String,
    @SerializedName("qr_code_url") val qrCodeUrl: String,
    @SerializedName("expiry_time") val expiryTime: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * Create visitor response data
 */
data class CreateVisitorResponseData(
    val visitor: VisitorData,
    val pass: PassData?, // Only present if expiry_time was provided
    val message: String
)

/**
 * Create visitor API response wrapper
 */
data class CreateVisitorResponse(
    val status: String,
    val data: CreateVisitorResponseData? = null,
    val message: String? = null
)

/**
 * Get all visitors API response wrapper
 */
data class GetAllVisitorsResponse(
    val status: String,
    val results: Int? = null,
    val data: GetAllVisitorsData? = null,
    val message: String? = null
)

/**
 * Get all visitors response data
 */
data class GetAllVisitorsData(
    val visitors: List<VisitorData>
)
