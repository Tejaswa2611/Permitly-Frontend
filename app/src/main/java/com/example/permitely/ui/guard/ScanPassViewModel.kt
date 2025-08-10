package com.example.permitely.ui.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.PassScanResult
import com.example.permitely.data.repository.GuardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Scan Pass Screen
 */
data class ScanPassUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val scanResult: PassScanResult? = null,
    val isScanning: Boolean = true,
    val lastScannedCode: String? = null
)

/**
 * ViewModel for Scan Pass Screen
 * Handles QR code scanning and pass verification with backend integration
 */
@HiltViewModel
class ScanPassViewModel @Inject constructor(
    private val guardRepository: GuardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanPassUiState())
    val uiState: StateFlow<ScanPassUiState> = _uiState.asStateFlow()

    /**
     * Process scanned QR code and verify pass with backend
     */
    fun onQRCodeScanned(qrCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isScanning = false,
                lastScannedCode = qrCode
            )

            try {
                println("ScanPassViewModel: Processing QR code: $qrCode")

                // Extract pass ID from QR code URL
                val passId = guardRepository.extractPassIdFromQrCode(qrCode)

                if (passId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Invalid QR code format. Please scan a valid visitor pass.",
                        scanResult = null
                    )
                    return@launch
                }

                println("ScanPassViewModel: Extracted pass ID: $passId")

                // Scan the pass using the repository
                guardRepository.scanPass(passId).collect { result ->
                    result.fold(
                        onSuccess = { scanResult ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = if (!scanResult.isSuccess) scanResult.errorMessage else null,
                                scanResult = scanResult
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to scan pass",
                                scanResult = null
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                println("ScanPassViewModel: Exception during QR processing: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to process QR code: ${e.message}",
                    scanResult = null
                )
            }
        }
    }

    /**
     * Reset scan state to allow scanning again
     */
    fun resetScan() {
        _uiState.value = ScanPassUiState(isScanning = true)
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Start scanning again after showing results
     */
    fun startScanningAgain() {
        _uiState.value = _uiState.value.copy(
            isScanning = true,
            scanResult = null,
            error = null,
            lastScannedCode = null
        )
    }
}
