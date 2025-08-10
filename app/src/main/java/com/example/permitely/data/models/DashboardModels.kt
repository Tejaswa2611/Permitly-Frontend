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
