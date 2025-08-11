package com.example.permitely.data.models

/**
 * Dashboard statistics response data model
 * Matches the API response from /api/user/dashboard/stats
 */
data class DashboardStatsResponse(
    val success: Boolean,
    val data: DashboardStats? = null,
    val message: String? = null
)

/**
 * Dashboard statistics data
 */
data class DashboardStats(
    val totalVisitors: Int,
    val approved: Int,
    val pending: Int,
    val rejected: Int,
    val expired: Int
)

/**
 * UI state for dashboard stats
 */
data class DashboardStatsUiState(
    val totalVisitors: Int = 0,
    val approved: Int = 0,
    val pending: Int = 0,
    val rejected: Int = 0,
    val expired: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "User",
    val userEmail: String = "",
    val userRole: String = ""
)

/**
 * Recent visitors API response data model
 * Matches the API response from /api/users/recent-visitors
 */
data class RecentVisitorsResponse(
    val success: Boolean,
    val data: List<RecentVisitor>? = null,
    val message: String? = null
)

/**
 * Recent visitor data model
 */
data class RecentVisitor(
    val visitor_id: Int,
    val name: String,
    val phone_number: String,
    val email: String,
    val purpose_of_visit: String,
    val host_id: Int,
    val created_by_guard_id: Int?,
    val status: String,
    val entry_time: String?,
    val exit_time: String?,
    val created_at: String,
    val updated_at: String,
    val passes: List<VisitorPass>,
    val host: HostInfo
)

/**
 * Visitor pass information
 */
data class VisitorPass(
    val pass_id: Int,
    val visitor_id: Int,
    val qr_code_data: String,
    val qr_code_url: String,
    val created_at: String,
    val expiry_time: String,
    val approved_at: String?,
    val approved_by: String?
)

/**
 * Host information for visitor
 */
data class HostInfo(
    val name: String,
    val email: String,
    val phone_number: String
)
