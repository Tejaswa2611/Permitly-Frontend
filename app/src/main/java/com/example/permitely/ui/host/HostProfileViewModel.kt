package com.example.permitely.ui.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.UpdateProfileRequest
import com.example.permitely.data.repository.ProfileRepository
import com.example.permitely.data.storage.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for Host Profile Screen
 */
data class HostProfileUiState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "",
    val userId: String = "",
    val createdAt: String = "",
    val visitorsCount: Int = 0,
    val passesCount: Int = 0,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for Host Profile Screen
 * Manages user profile data and state with API integration
 */
@HiltViewModel
class HostProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(HostProfileUiState())
    val uiState: StateFlow<HostProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Load user profile information from API with TokenStorage fallback
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            profileRepository.getUserProfile().collect { result ->
                result.fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(
                            name = profile.name,
                            email = profile.email,
                            phoneNumber = profile.phoneNumber ?: "",
                            role = profile.role,
                            userId = profile.userId.toString(),
                            createdAt = profile.createdAt,
                            visitorsCount = profile.count?.visitors ?: 0,
                            passesCount = profile.count?.passes ?: 0,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load profile"
                        )
                    }
                )
            }
        }
    }

    /**
     * Enable editing mode
     */
    fun startEditing() {
        _uiState.value = _uiState.value.copy(isEditing = true)
    }

    /**
     * Cancel editing mode
     */
    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(isEditing = false)
        // Reload original data
        loadUserProfile()
    }

    /**
     * Update name field
     */
    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    /**
     * Update email field
     */
    fun updateEmail(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    /**
     * Update phone number field
     */
    fun updatePhoneNumber(newPhone: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = newPhone)
    }

    /**
     * Save profile changes via API
     */
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            val updateRequest = UpdateProfileRequest(
                name = _uiState.value.name,
                phoneNumber = _uiState.value.phoneNumber.takeIf { it.isNotBlank() }
            )

            try {
                val result = profileRepository.updateUserProfile(updateRequest).first()
                result.fold(
                    onSuccess = { updatedProfile ->
                        _uiState.value = _uiState.value.copy(
                            name = updatedProfile.name,
                            email = updatedProfile.email,
                            phoneNumber = updatedProfile.phoneNumber ?: "",
                            role = updatedProfile.role,
                            userId = updatedProfile.userId.toString(),
                            createdAt = updatedProfile.createdAt,
                            visitorsCount = updatedProfile.count?.visitors ?: 0,
                            passesCount = updatedProfile.count?.passes ?: 0,
                            isSaving = false,
                            isEditing = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = exception.message ?: "Failed to save profile"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save profile"
                )
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Refresh profile data
     */
    fun refresh() {
        loadUserProfile()
    }
}
