package com.example.permitely.ui.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.DashboardStatsUiState
import com.example.permitely.data.repository.DashboardRepository
import com.example.permitely.data.storage.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Guard Dashboard Screen
 * Manages guard dashboard statistics and UI state
 */
@HiltViewModel
class GuardDashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardStatsUiState())
    val uiState: StateFlow<DashboardStatsUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    /**
     * Load guard dashboard statistics and user info
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load user info from storage (Flow-based)
            tokenStorage.getUserInfo().collect { userInfo ->
                _uiState.value = _uiState.value.copy(
                    userName = userInfo?.name ?: "Guard",
                    userEmail = userInfo?.email ?: "",
                    userRole = "Guard"
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
            try {
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
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to load dashboard stats"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
            }
        }
    }

    /**
     * Refresh dashboard data
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
