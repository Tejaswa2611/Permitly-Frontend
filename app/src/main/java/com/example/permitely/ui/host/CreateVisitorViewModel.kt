package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.CreateVisitorRequest
import com.example.permitely.data.models.CreateVisitorResponseData
import com.example.permitely.data.repository.VisitorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for Create Visitor Screen
 */
data class CreateVisitorUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val createdVisitorData: CreateVisitorResponseData? = null
)

/**
 * ViewModel for Create Visitor Screen
 * Handles visitor creation with API integration
 */
@HiltViewModel
class CreateVisitorViewModel @Inject constructor(
    private val visitorRepository: VisitorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateVisitorUiState())
    val uiState: StateFlow<CreateVisitorUiState> = _uiState.asStateFlow()

    /**
     * Create a new visitor with the provided details
     */
    fun createVisitor(
        name: String,
        email: String,
        phoneNumber: String,
        purposeOfVisit: String,
        expiryDateTime: String? = null // ISO format: "2025-08-10T15:30:00Z"
    ) {
        // Client-side validation
        if (name.isBlank()) {
            _uiState.value = CreateVisitorUiState(error = "Visitor name is required")
            return
        }

        if (email.isBlank()) {
            _uiState.value = CreateVisitorUiState(error = "Email address is required")
            return
        }

        if (!isValidEmail(email)) {
            _uiState.value = CreateVisitorUiState(error = "Please enter a valid email address")
            return
        }

        if (phoneNumber.isBlank()) {
            _uiState.value = CreateVisitorUiState(error = "Phone number is required")
            return
        }

        if (phoneNumber.length < 10) {
            _uiState.value = CreateVisitorUiState(error = "Please enter a valid phone number")
            return
        }

        if (purposeOfVisit.isBlank()) {
            _uiState.value = CreateVisitorUiState(error = "Purpose of visit is required")
            return
        }

        // Create visitor request
        val request = CreateVisitorRequest(
            name = name.trim(),
            phoneNumber = phoneNumber.trim(),
            email = email.trim(),
            purposeOfVisit = purposeOfVisit.trim(),
            expiryTime = expiryDateTime
        )

        viewModelScope.launch {
            _uiState.value = CreateVisitorUiState(isLoading = true)

            visitorRepository.createVisitor(request).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { responseData ->
                        CreateVisitorUiState(
                            isSuccess = true,
                            createdVisitorData = responseData
                        )
                    },
                    onFailure = { exception ->
                        CreateVisitorUiState(
                            error = exception.message ?: "Failed to create visitor"
                        )
                    }
                )
            }
        }
    }

    /**
     * Create visitor with date and time (converts to ISO format)
     */
    fun createVisitorWithDateTime(
        name: String,
        email: String,
        phoneNumber: String,
        purposeOfVisit: String,
        selectedDate: String,
        selectedTime: String
    ) {
        val expiryDateTime = if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
            try {
                // Convert date and time to ISO format
                convertToISOFormat(selectedDate, selectedTime)
            } catch (_: Exception) {
                _uiState.value = CreateVisitorUiState(error = "Invalid date or time format")
                return
            }
        } else {
            null
        }

        createVisitor(name, email, phoneNumber, purposeOfVisit, expiryDateTime)
    }

    /**
     * Convert date and time strings to ISO format
     */
    private fun convertToISOFormat(date: String, time: String): String {
        try {
            // Parse the date string (format: "MMM dd, yyyy" from DatePicker)
            val inputDateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.ENGLISH)
            val parsedDate = inputDateFormat.parse(date)

            // Parse the time string (format: "HH:mm" from TimePicker)
            val timeComponents = time.split(":")
            val hour = timeComponents[0].toInt()
            val minute = timeComponents[1].toInt()

            // Create Calendar instance and set the date and time
            val calendar = java.util.Calendar.getInstance()
            calendar.time = parsedDate
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
            calendar.set(java.util.Calendar.MINUTE, minute)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)

            // Format to ISO 8601 with UTC timezone
            val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.ENGLISH)
            isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")

            return isoFormat.format(calendar.time)
        } catch (e: Exception) {
            println("DEBUG: Date/Time conversion error: ${e.message}")
            // Fallback: try to create a basic ISO format
            throw Exception("Invalid date or time format")
        }
    }

    /**
     * Validate email format
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Reset state
     */
    fun resetState() {
        _uiState.value = CreateVisitorUiState()
    }
}
