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
 * Visitor data model from API response - updated to match actual API structure
 */
data class VisitorData(
    @SerializedName("visitor_id") val visitorId: Int,
    val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val email: String,
    @SerializedName("purpose_of_visit") val purposeOfVisit: String,
    val status: String, // PENDING, APPROVED, REJECTED, EXPIRED
    @SerializedName("entry_time") val entryTime: String?,
    @SerializedName("exit_time") val exitTime: String?,
    @SerializedName("created_at") val createdAt: String,
    val passes: List<PassData> = emptyList() // Array of passes, newest first
)

/**
 * Pass data model from API response - updated structure
 */
data class PassData(
    @SerializedName("pass_id") val passId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("expiry_time") val expiryTime: String,
    @SerializedName("approved_at") val approvedAt: String,
    @SerializedName("qr_code_data") val qrCodeData: String // This is the QR code URL
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

/**
 * Get visitor by ID API response wrapper
 */
data class GetVisitorByIdResponse(
    val status: String,
    val data: GetVisitorByIdData? = null,
    val message: String? = null
)

/**
 * Get visitor by ID response data - updated to match API structure
 */
data class GetVisitorByIdData(
    val visitor: VisitorData // Visitor already contains passes array
)

/**
 * Get recent visitors API response wrapper
 */
data class GetRecentVisitorsResponse(
    val success: Boolean,
    val data: List<VisitorData>? = null,
    val message: String? = null
)

/**
 * Get notifications API response wrapper
 */
data class GetNotificationsResponse(
    val status: String,
    val results: Int? = null,
    val data: GetNotificationsData? = null,
    val message: String? = null
)

/**
 * Get notifications response data
 */
data class GetNotificationsData(
    val notifications: List<NotificationData>
)

/**
 * Notification data model from API response
 * Only includes fields needed for UI: name, created_at, status, content
 */
data class NotificationData(
    @SerializedName("notification_id") val notificationId: Int,
    val content: String,
    @SerializedName("created_at") val createdAt: String,
    val visitor: VisitorNotificationData?
)

/**
 * Visitor data in notification (simplified)
 */
data class VisitorNotificationData(
    val name: String,
    val status: String // PENDING, APPROVED, REJECTED, EXPIRED
)
