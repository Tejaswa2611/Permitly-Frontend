package com.example.permitely.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permitely.data.repository.AuthRepository
import com.example.permitely.data.storage.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the main/welcome screen
 * Manages user information and logout functionality
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userInfo = MutableStateFlow<TokenStorage.UserInfo?>(null)
    val userInfo: StateFlow<TokenStorage.UserInfo?> = _userInfo.asStateFlow()

    init {
        // Load user info when ViewModel is created
        loadUserInfo()
    }

    /**
     * Load user information from storage
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            authRepository.getUserInfo().collect { userInfo ->
                _userInfo.value = userInfo
            }
        }
    }

    /**
     * Logout user and clear all stored data
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                // Logout result is handled in the repository
                // UI will react to the cleared tokens automatically
            }
        }
    }
}
