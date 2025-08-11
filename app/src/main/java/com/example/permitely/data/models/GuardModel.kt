package com.example.permitely.data.models

import com.google.gson.annotations.SerializedName

/**
 * Pass scanning API response data structure
 */
data class PassScanResponseData(
    val pass: PassScanData,
    val visitor: VisitorScanData
)

/**
 * Pass data from scan API response
 */
data class PassScanData(
    @SerializedName("pass_id") val passId: Int,
    @SerializedName("visitor_id") val visitorId: Int,
    @SerializedName("qr_code_data") val qrCodeData: String,
    @SerializedName("qr_code_url") val qrCodeUrl: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("expiry_time") val expiryTime: String,
    @SerializedName("approved_at") val approvedAt: String?,
    @SerializedName("approved_by") val approvedBy: Int?
)

/**
 * Visitor data from scan API response
 */
data class VisitorScanData(
    @SerializedName("visitor_id") val visitorId: Int,
    val name: String,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("purpose_of_visit") val purposeOfVisit: String,
    @SerializedName("host_id") val hostId: Int,
    @SerializedName("created_by_guard_id") val createdByGuardId: Int?,
    val status: String, // PENDING, APPROVED, REJECTED, EXPIRED
    @SerializedName("entry_time") val entryTime: String?,
    @SerializedName("exit_time") val exitTime: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * UI state for pass scanning result
 */
data class PassScanResult(
    val isSuccess: Boolean,
    val pass: PassScanData? = null,
    val visitor: VisitorScanData? = null,
    val errorMessage: String? = null
)

/**
 * Guard today stats API response data structure
 * Updated to match actual backend response: {"status":"success","data":{"stats":{...}}}
 */
data class GuardTodayStatsResponse(
    val status: String,
    val data: GuardTodayStatsDataWrapper
)

data class GuardTodayStatsDataWrapper(
    val stats: GuardTodayStatsData
)

/**
 * Guard today stats data from API response
 */
data class GuardTodayStatsData(
    val approvedVisitors: Int,    // Today's approved visitors (entered)
    val pendingVisitors: Int,     // Today's pending visitors (waiting)
    val expiredVisitors: Int,     // Today's expired visitors
    val rejectedVisitors: Int,    // Today's rejected visitors
    val totalVisitors: Int,       // Sum of all above (total for today)
    val date: String              // Date for these stats (2025-08-11)
)
