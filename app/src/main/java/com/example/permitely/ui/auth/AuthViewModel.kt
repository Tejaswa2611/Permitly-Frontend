package com.example.permitely.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.LoginRequest
import com.example.permitely.data.models.SignupRequest
import com.example.permitely.data.repository.AuthRepository
import com.example.permitely.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for authentication screens
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel managing authentication with real backend integration
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Login with backend API integration
     */
    fun login(email: String, password: String) {
        // Client-side validation before API call - matching backend validation exactly
        if (!isValidEmailBackend(email)) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a valid email address")
            return
        }

        if (!isValidPasswordBackend(password)) {
            _uiState.value = AuthUiState(errorMessage = "Invalid password format")
            return
        }

        viewModelScope.launch {
            authRepository.login(LoginRequest(email, password)).collect { result ->
                _uiState.value = when (result) {
                    is AuthResult.Loading -> AuthUiState(isLoading = true)
                    is AuthResult.Success -> AuthUiState(isSuccess = true)
                    is AuthResult.Error -> AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    /**
     * Signup with backend API integration
     * Note: Using 'role' field as required by backend instead of 'userType'
     */
    fun signup(name: String, email: String, password: String, phoneNumber: String, userType: String) {
        // Client-side validation before API call - matching backend validation exactly
        if (name.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Name is required")
            return
        }

        if (!isValidEmailBackend(email)) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a valid email address")
            return
        }

        if (!isValidPasswordBackend(password)) {
            _uiState.value = AuthUiState(
                errorMessage = "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
            )
            return
        }

        if (phoneNumber.length < 10) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a valid phone number")
            return
        }

        viewModelScope.launch {
            authRepository.signup(
                SignupRequest(
                    name = name,
                    email = email,
                    password = password,
                    phoneNumber = phoneNumber,
                    role = userType // Backend expects 'role' field
                )
            ).collect { result ->
                _uiState.value = when (result) {
                    is AuthResult.Loading -> AuthUiState(isLoading = true)
                    is AuthResult.Success -> AuthUiState(isSuccess = true)
                    is AuthResult.Error -> AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    /**
     * Validates email according to backend requirements
     * Matches: const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
     */
    private fun isValidEmailBackend(email: String): Boolean {
        val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        return emailRegex.matches(email)
    }

    /**
     * Validates password according to backend requirements exactly
     */
    private fun isValidPasswordBackend(password: String): Boolean {
        // Password must be at least 8 characters long
        if (password.length < 8) {
            return false
        }

        // Must contain at least one uppercase letter
        if (!Regex("[A-Z]").containsMatchIn(password)) {
            return false
        }

        // Must contain at least one lowercase letter
        if (!Regex("[a-z]").containsMatchIn(password)) {
            return false
        }

        // Must contain at least one number
        if (!Regex("\\d").containsMatchIn(password)) {
            return false
        }

        // Must contain at least one special character
        if (!Regex("[!@#$%^&*(),.?\":{}|<>]").containsMatchIn(password)) {
            return false
        }

        return true
    }

    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                // Handle logout result if needed
            }
        }
    }

    /**
     * Clears any existing error message from the UI state.
     *
     * This method is typically called when:
     * - User starts typing in input fields
     * - User initiates a new authentication attempt
     * - UI needs to reset error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Resets the UI state to its initial values.
     *
     * This method is useful for:
     * - Cleaning up state after successful authentication
     * - Resetting state when navigating between screens
     * - Preparing for fresh authentication attempts
     */
    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
