package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.GetVisitorByIdData
import com.example.permitely.data.repository.VisitorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Visitor Details Screen
 */
data class VisitorDetailsUiState(
    val isLoading: Boolean = false,
    val visitorData: GetVisitorByIdData? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel for Visitor Details Screen
 * Manages fetching detailed visitor information by ID from the API
 */
@HiltViewModel
class VisitorDetailsViewModel @Inject constructor(
    private val visitorRepository: VisitorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitorDetailsUiState())
    val uiState: StateFlow<VisitorDetailsUiState> = _uiState.asStateFlow()

    /**
     * Load visitor details by ID from the API
     * @param visitorId The ID of the visitor to fetch
     */
    fun loadVisitorDetails(visitorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            visitorRepository.getVisitorById(visitorId).collect { result ->
                result.fold(
                    onSuccess = { visitorData ->
                        println("DEBUG: Successfully loaded visitor details for ID: $visitorId")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            visitorData = visitorData,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to load visitor details: ${exception.message}")
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
     * Refresh visitor details
     */
    fun refreshVisitorDetails(visitorId: String) {
        loadVisitorDetails(visitorId)
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
    fun convertToUiVisitor(visitorData: GetVisitorByIdData): Visitor {
        val visitor = visitorData.visitor
        val latestPass = visitor.passes.firstOrNull() // Get newest pass (first in array)

        return Visitor(
            id = visitor.visitorId.toString(),
            name = visitor.name,
            email = visitor.email,
            phone = visitor.phoneNumber,
            purpose = visitor.purposeOfVisit,
            date = visitor.createdAt.split("T")[0], // Extract date part
            time = visitor.createdAt.split("T")[1].split("Z")[0], // Extract time part
            status = when(visitor.status) {
                "PENDING" -> VisitorStatus.PENDING
                "APPROVED" -> VisitorStatus.APPROVED
                "REJECTED" -> VisitorStatus.REJECTED
                "EXPIRED" -> VisitorStatus.EXPIRED
                else -> VisitorStatus.PENDING
            },
            createdAt = visitor.createdAt,
            // Pass information from latest pass
            hasQRCode = latestPass != null,
            qrCodeUrl = latestPass?.qrCodeData, // This is the actual QR code URL
            passId = latestPass?.passId?.toString(),
            expiryTime = latestPass?.expiryTime
        )
    }
}
