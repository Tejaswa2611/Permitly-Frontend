package com.example.permitely.data.models

import com.google.gson.annotations.SerializedName

/**
 * Complete user profile data model matching the actual API response
 * From GET /api/users/profile
 */
data class UserProfile(
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("_count") val count: UserCount?
)

/**
 * User statistics count
 */
data class UserCount(
    val visitors: Int,
    val passes: Int
)

/**
 * Profile API response wrapper
 */
data class ProfileResponse(
    val success: Boolean,
    val data: UserProfile? = null,
    val message: String? = null
)

/**
 * Update profile request model
 */
data class UpdateProfileRequest(
    val name: String,
    @SerializedName("phone_number") val phoneNumber: String?
)
