package com.example.permitely.ui.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.DashboardStatsUiState
import com.example.permitely.data.repository.GuardRepository
import com.example.permitely.data.storage.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * ViewModel for Guard Dashboard Screen
 * Manages guard dashboard statistics and UI state with today's stats
 */
@HiltViewModel
class GuardDashboardViewModel @Inject constructor(
    private val guardRepository: GuardRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardStatsUiState())
    val uiState: StateFlow<DashboardStatsUiState> = _uiState.asStateFlow()

    init {
        println("GuardDashboardViewModel: Initializing...")
        println("GuardDashboardViewModel: Initial UI State - Total: ${_uiState.value.totalVisitors}")
        loadDashboardData()
    }

    /**
     * Load guard dashboard statistics and user info
     */
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            println("GuardDashboardViewModel: Starting to load dashboard data")

            // Load user info from storage (Flow-based) - but don't collect continuously
            val userInfo = tokenStorage.getUserInfo().first()
            _uiState.value = _uiState.value.copy(
                userName = userInfo?.name ?: "Guard",
                userEmail = userInfo?.email ?: "",
                userRole = "Guard"
            )

            println("GuardDashboardViewModel: User info loaded - ${userInfo?.name}")
        }

        // Load today's guard stats
        loadTodayStats()
    }

    /**
     * Load today's guard statistics from the API
     */
    private fun loadTodayStats() {
        viewModelScope.launch {
            try {
                guardRepository.getTodayStats().collect { result ->
                    result.fold(
                        onSuccess = { todayStats ->
                            _uiState.value = _uiState.value.copy(
                                totalVisitors = todayStats.totalVisitors,
                                approved = todayStats.approvedVisitors,
                                pending = todayStats.pendingVisitors,
                                rejected = todayStats.rejectedVisitors,
                                expired = todayStats.expiredVisitors,
                                isLoading = false,
                                error = null
                            )
                            println("GuardDashboardViewModel: Today's stats loaded - Total: ${todayStats.totalVisitors}")
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to load today's statistics"
                            )
                            println("GuardDashboardViewModel: Failed to load stats - ${error.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard data"
                )
                println("GuardDashboardViewModel: Exception loading stats - ${e.message}")
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
