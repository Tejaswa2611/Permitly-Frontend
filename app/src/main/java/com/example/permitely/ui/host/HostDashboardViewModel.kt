package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.DashboardStatsUiState
import com.example.permitely.data.repository.DashboardRepository
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
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardStatsUiState())
    val uiState: StateFlow<DashboardStatsUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
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
     * Refresh dashboard statistics
     */
    fun refresh() {
        loadDashboardData()
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
