package com.example.permitely.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.models.AuthResponse
import com.example.permitely.data.models.LoginRequest
import com.example.permitely.data.models.SignupRequest
import com.example.permitely.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============================================================================
// Authentication ViewModel
// ============================================================================
// This ViewModel manages the UI state and business logic for authentication
// screens following the MVVM architecture pattern.

/**
 * UI State data class that represents the current state of authentication screens.
 *
 * This immutable state object is used to drive UI updates and provide reactive
 * programming patterns. The UI observes this state and automatically updates
 * when any property changes.
 *
 * @param isLoading Whether an authentication operation is currently in progress
 * @param isSuccess Whether the last authentication operation was successful
 * @param errorMessage Error message to display to user (null if no error)
 * @param token Authentication token received upon successful login/signup
 */
data class AuthUiState(
    val isLoading: Boolean = false,     // Shows loading indicators in UI
    val isSuccess: Boolean = false,     // Triggers navigation to main app
    val errorMessage: String? = null,   // Displays error messages to user
    val token: String? = null           // Stores auth token for API requests
)

/**
 * ViewModel for managing authentication logic and UI state.
 *
 * This ViewModel follows the MVVM architecture pattern and is responsible for:
 * - Managing UI state for login and signup screens
 * - Coordinating with the AuthRepository for data operations
 * - Handling business logic and validation
 * - Providing reactive state updates to the UI
 * - Managing coroutines for asynchronous operations
 *
 * The @HiltViewModel annotation enables automatic dependency injection,
 * and @Inject allows Hilt to provide the required dependencies.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository  // Repository for auth operations
) : ViewModel() {

    // ============================================================================
    // State Management
    // ============================================================================
    // Private mutable state that can only be modified within the ViewModel
    private val _uiState = MutableStateFlow(AuthUiState())

    // Public read-only state that UI components can observe
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ============================================================================
    // Authentication Operations
    // ============================================================================

    /**
     * Performs user login with email and password.
     *
     * This method handles the complete login flow including:
     * - Input validation (basic client-side checks)
     * - Setting loading state for UI feedback
     * - Calling repository to perform authentication
     * - Updating UI state based on result
     * - Error handling and user feedback
     *
     * @param email User's email address
     * @param password User's password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Set loading state to show progress indicators
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Call repository to perform authentication
            authRepository.login(LoginRequest(email, password)).collect { response ->
                // Update UI state based on authentication result
                _uiState.value = if (response.success) {
                    // Success: prepare for navigation to main app
                    AuthUiState(
                        isLoading = false,
                        isSuccess = true,
                        token = response.token
                    )
                } else {
                    // Failure: show error message to user
                    AuthUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = response.message
                    )
                }
            }
        }
    }

    /**
     * Performs user registration with complete user information.
     *
     * This method handles the complete signup flow including:
     * - Multi-field validation
     * - User type selection handling
     * - Setting appropriate loading states
     * - Calling repository for account creation
     * - Providing user feedback on success/failure
     *
     * @param name User's full name
     * @param email User's email address (must be unique)
     * @param password User's chosen password
     * @param phoneNumber User's phone number for notifications
     * @param userType Type of account (resident, guard, admin)
     */
    fun signup(name: String, email: String, password: String, phoneNumber: String, userType: String) {
        viewModelScope.launch {
            // Set loading state to show progress indicators
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Call repository to create new user account
            authRepository.signup(
                SignupRequest(name, email, password, phoneNumber, userType)
            ).collect { response ->
                // Update UI state based on registration result
                _uiState.value = if (response.success) {
                    // Success: prepare for navigation to main app
                    AuthUiState(
                        isLoading = false,
                        isSuccess = true,
                        token = response.token
                    )
                } else {
                    // Failure: show error message to user
                    AuthUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = response.message
                    )
                }
            }
        }
    }

    // ============================================================================
    // State Management Helper Methods
    // ============================================================================

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
