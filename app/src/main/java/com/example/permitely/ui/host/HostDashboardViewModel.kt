package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.DashboardStatsUiState
import com.example.permitely.data.repository.DashboardRepository
import com.example.permitely.data.repository.VisitorRepository
import com.example.permitely.data.storage.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Host Dashboard Screen
 * Manages dashboard statistics and UI state
 */
@HiltViewModel
class HostDashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val visitorRepository: VisitorRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardStatsUiState())
    val uiState: StateFlow<DashboardStatsUiState> = _uiState.asStateFlow()

    // Recent visitors state
    private val _recentVisitors = MutableStateFlow<List<RecentVisitor>>(emptyList())
    val recentVisitors: StateFlow<List<RecentVisitor>> = _recentVisitors.asStateFlow()

    private val _isLoadingVisitors = MutableStateFlow(false)
    val isLoadingVisitors: StateFlow<Boolean> = _isLoadingVisitors.asStateFlow()

    init {
        loadDashboardData()
        loadRecentVisitors()
    }

    /**
     * Load dashboard statistics and user info from the API and storage
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load user name from profile API (fresh data)
            dashboardRepository.getUserName().collect { nameResult ->
                nameResult.fold(
                    onSuccess = { userName ->
                        _uiState.value = _uiState.value.copy(userName = userName)
                    },
                    onFailure = { /* Keep existing name or use default */ }
                )
            }
        }

        // Load dashboard stats separately
        loadDashboardStats()
    }

    /**
     * Load dashboard statistics from the API
     */
    private fun loadDashboardStats() {
        viewModelScope.launch {
            dashboardRepository.getDashboardStats().collect { result ->
                result.fold(
                    onSuccess = { stats ->
                        _uiState.value = _uiState.value.copy(
                            totalVisitors = stats.totalVisitors,
                            approved = stats.approved,
                            pending = stats.pending,
                            rejected = stats.rejected,
                            expired = stats.expired,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }

    /**
     * Load recent visitors from the API
     */
    private fun loadRecentVisitors() {
        viewModelScope.launch {
            _isLoadingVisitors.value = true
            visitorRepository.getRecentVisitors().collect { result ->
                result.fold(
                    onSuccess = { visitors ->
                        // Convert API visitors to UI visitors (only need name, purpose, time, status)
                        val recentVisitors = visitors.map { apiVisitor ->
                            RecentVisitor(
                                name = apiVisitor.name,
                                purpose = apiVisitor.purposeOfVisit,
                                time = formatTime(apiVisitor.createdAt),
                                status = apiVisitor.status
                            )
                        }
                        _recentVisitors.value = recentVisitors
                        println("DEBUG: Loaded ${recentVisitors.size} recent visitors")
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to load recent visitors: ${exception.message}")
                        // Keep empty list on error
                        _recentVisitors.value = emptyList()
                    }
                )
                _isLoadingVisitors.value = false
            }
        }
    }

    /**
     * Refresh recent visitors
     */
    fun refreshRecentVisitors() {
        loadRecentVisitors()
    }

    /**
     * Refresh dashboard statistics
     */
    fun refresh() {
        loadDashboardData()
        loadRecentVisitors()
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Format timestamp to readable time
     */
    private fun formatTime(timestamp: String): String {
        return try {
            // Extract time from ISO timestamp: "2025-08-10T08:00:00Z" -> "08:00"
            timestamp.split("T")[1].split("Z")[0].substring(0, 5)
        } catch (e: Exception) {
            "N/A"
        }
    }
}

/**
 * Data class for recent visitor UI display
 * Only contains fields needed for dashboard display
 */
data class RecentVisitor(
    val name: String,
    val purpose: String,
    val time: String, // Formatted time string
    val status: String // PENDING, APPROVED, REJECTED, EXPIRED
)
