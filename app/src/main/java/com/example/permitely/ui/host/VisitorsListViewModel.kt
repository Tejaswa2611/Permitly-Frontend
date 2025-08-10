package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.VisitorData
import com.example.permitely.data.repository.VisitorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Visitors List Screen
 */
data class VisitorsListUiState(
    val isLoading: Boolean = false,
    val visitors: List<VisitorData> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for Visitors List Screen
 * Manages fetching and filtering of visitors from the API
 */
@HiltViewModel
class VisitorsListViewModel @Inject constructor(
    private val visitorRepository: VisitorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitorsListUiState())
    val uiState: StateFlow<VisitorsListUiState> = _uiState.asStateFlow()

    init {
        // Load all visitors when ViewModel is created
        loadVisitors()
    }

    /**
     * Load all visitors from the API
     * @param status Optional filter by visitor status
     * @param isRefresh Whether this is a refresh operation
     */
    fun loadVisitors(status: String? = null, isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh,
                errorMessage = null
            )

            visitorRepository.getAllVisitors(status).collect { result ->
                result.fold(
                    onSuccess = { visitors ->
                        println("DEBUG: Successfully loaded ${visitors.size} visitors")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            visitors = visitors,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to load visitors: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = exception.message
                        )
                    }
                )
            }
        }
    }

    /**
     * Refresh visitors list
     */
    fun refreshVisitors(status: String? = null) {
        loadVisitors(status, isRefresh = true)
    }

    /**
     * Filter visitors by status
     */
    fun filterByStatus(status: String?) {
        loadVisitors(status)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Convert API VisitorData to UI Visitor model
     */
    fun convertToUiVisitor(visitorData: VisitorData): Visitor {
        return Visitor(
            id = visitorData.visitorId.toString(),
            name = visitorData.name,
            email = visitorData.email,
            phone = visitorData.phoneNumber,
            purpose = visitorData.purposeOfVisit,
            date = visitorData.createdAt.split("T")[0], // Extract date part
            time = visitorData.createdAt.split("T")[1].split("Z")[0], // Extract time part
            status = when(visitorData.status) {
                "PENDING" -> VisitorStatus.PENDING
                "APPROVED" -> VisitorStatus.APPROVED
                "REJECTED" -> VisitorStatus.REJECTED
                "EXPIRED" -> VisitorStatus.EXPIRED
                else -> VisitorStatus.PENDING
            },
            createdAt = visitorData.createdAt,
            hasQRCode = false, // Will be updated when we have pass information
            qrCodeUrl = null,
            passId = null,
            expiryTime = null
        )
    }
}
