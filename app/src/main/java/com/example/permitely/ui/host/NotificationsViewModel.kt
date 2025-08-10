package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.NotificationData
import com.example.permitely.data.repository.VisitorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Notifications Screen
 */
data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationData> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel for Notifications Screen
 * Manages fetching notifications from the API
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val visitorRepository: VisitorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        // Load notifications when ViewModel is created
        loadNotifications()
    }

    /**
     * Load notifications from the API
     */
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            visitorRepository.getNotifications().collect { result ->
                result.fold(
                    onSuccess = { notifications ->
                        println("DEBUG: Successfully loaded ${notifications.size} notifications")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            notifications = notifications,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to load notifications: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                )
            }
        }
    }

    /**
     * Refresh notifications
     */
    fun refreshNotifications() {
        loadNotifications()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Convert API NotificationData to UI Notification model
     * Only extracts fields needed for UI: name, created_at, status, content
     */
    fun convertToUiNotification(apiNotification: NotificationData): Notification {
        return Notification(
            id = apiNotification.notificationId.toString(),
            message = apiNotification.content,
            timestamp = formatTimestamp(apiNotification.createdAt),
            visitorName = apiNotification.visitor?.name ?: "System Admin",
            isRead = false, // API doesn't provide read status, default to false
            type = determineNotificationType(apiNotification)
        )
    }

    /**
     * Format ISO timestamp to readable format
     */
    private fun formatTimestamp(isoTimestamp: String): String {
        return try {
            // Convert "2025-08-10T10:30:00Z" to "2025-08-10 10:30:00"
            isoTimestamp.replace("T", " ").replace("Z", "")
        } catch (e: Exception) {
            isoTimestamp
        }
    }

    /**
     * Determine notification type based on content and visitor status
     */
    private fun determineNotificationType(notification: NotificationData): NotificationType {
        return when {
            notification.visitor == null -> NotificationType.SYSTEM
            notification.content.contains("approved", ignoreCase = true) -> NotificationType.VISITOR_APPROVED
            notification.content.contains("rejected", ignoreCase = true) -> NotificationType.VISITOR_REJECTED
            notification.content.contains("pass", ignoreCase = true) -> NotificationType.PASS_GENERATED
            notification.content.contains("arrived", ignoreCase = true) -> NotificationType.VISITOR_ARRIVED
            notification.content.contains("departed", ignoreCase = true) -> NotificationType.VISITOR_DEPARTED
            else -> NotificationType.VISITOR_REQUEST
        }
    }
}
