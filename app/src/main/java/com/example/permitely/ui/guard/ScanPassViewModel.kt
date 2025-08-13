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
        // Prevent duplicate scans and multiple processing
        if (_uiState.value.isLoading || _uiState.value.lastScannedCode == qrCode) {
            return
        }

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
                val passId = extractPassIdFromQRCode(qrCode)
                println("ScanPassViewModel: After extraction - passId = $passId")

                if (passId == null) {
                    println("ScanPassViewModel: PassId is null, returning with error")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Invalid QR code format",
                        scanResult = PassScanResult(
                            isSuccess = false,
                            errorMessage = "Invalid QR code format"
                        )
                    )
                    return@launch
                }

                println("ScanPassViewModel: Extracted pass ID: $passId")
                println("ScanPassViewModel: About to call guardRepository.scanPass($passId)")
                println("ScanPassViewModel: Repository object: $guardRepository")
                println("ScanPassViewModel: Repository class: ${guardRepository.javaClass.name}")

                // Call guard repository to scan the pass
                try {
                    println("ScanPassViewModel: About to collect from guardRepository.scanPass($passId)")
                    guardRepository.scanPass(passId).collect { result ->
                        println("ScanPassViewModel: Repository returned result")
                        println("ScanPassViewModel: Result object: $result")
                        println("ScanPassViewModel: Result isSuccess: ${result.isSuccess}")
                        println("ScanPassViewModel: Result isFailure: ${result.isFailure}")

                        _uiState.value = _uiState.value.copy(isLoading = false)

                        result.onSuccess { scanResult ->
                            println("ScanPassViewModel: Scan successful: ${scanResult.isSuccess}")
                            if (scanResult.isSuccess) {
                                _uiState.value = _uiState.value.copy(
                                    scanResult = scanResult,
                                    error = null
                                )
                            } else {
                                println("ScanPassViewModel: Scan failed: ${scanResult.errorMessage}")
                                _uiState.value = _uiState.value.copy(
                                    error = scanResult.errorMessage ?: "Failed to scan pass",
                                    scanResult = scanResult
                                )
                            }
                        }.onFailure { error ->
                            println("ScanPassViewModel: Repository error: ${error.message}")
                            _uiState.value = _uiState.value.copy(
                                error = error.message ?: "Failed to scan pass",
                                scanResult = PassScanResult(
                                    isSuccess = false,
                                    errorMessage = error.message ?: "Failed to scan pass"
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("ScanPassViewModel: Exception calling repository: ${e.message}")
                    e.printStackTrace()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to scan pass",
                        scanResult = PassScanResult(
                            isSuccess = false,
                            errorMessage = e.message ?: "Failed to scan pass"
                        )
                    )
                }
            } catch (e: Exception) {
                println("ScanPassViewModel: Exception during scan: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred",
                    scanResult = PassScanResult(
                        isSuccess = false,
                        errorMessage = e.message ?: "Unknown error occurred"
                    )
                )
            }
        }
    }

    /**
     * Extract pass ID from QR code URL
     */
    private fun extractPassIdFromQRCode(qrCode: String): Int? {
        println("ScanPassViewModel: Raw QR code: '$qrCode'")

        return try {
            when {
                // Handle full URL format: https://domain.com/api/guard/scan/123
                qrCode.contains("/api/guard/scan/") -> {
                    val passId = qrCode.substringAfterLast("/").toIntOrNull()
                    println("ScanPassViewModel: Extracted from URL format: $passId")
                    passId
                }
                // Handle just the pass ID number
                qrCode.matches(Regex("^\\d+$")) -> {
                    val passId = qrCode.toIntOrNull()
                    println("ScanPassViewModel: Direct number format: $passId")
                    passId
                }
                // Handle QR codes that might have extra parameters or query strings
                qrCode.contains("scan/") -> {
                    val afterScan = qrCode.substringAfter("scan/")
                    val passId = afterScan.split("?", "&", "#").firstOrNull()?.toIntOrNull()
                    println("ScanPassViewModel: Extracted from scan/ pattern: $passId")
                    passId
                }
                // Try to find any number in the QR code as a last resort
                else -> {
                    val numbers = Regex("\\d+").findAll(qrCode).map { it.value }.toList()
                    val passId = numbers.lastOrNull()?.toIntOrNull()
                    println("ScanPassViewModel: Found numbers in QR: $numbers, using: $passId")
                    passId
                }
            }
        } catch (e: Exception) {
            println("ScanPassViewModel: Error extracting pass ID from: '$qrCode' - ${e.message}")
            null
        }
    }

    /**
     * Start scanning again after showing result
     */
    fun startScanningAgain() {
        _uiState.value = _uiState.value.copy(
            isScanning = true,
            scanResult = null,
            error = null,
            lastScannedCode = null
        )
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null
        )
    }
}
