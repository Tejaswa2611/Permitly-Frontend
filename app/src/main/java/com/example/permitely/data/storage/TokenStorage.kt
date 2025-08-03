package com.example.permitely.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

/**
 * Secure token storage using DataStore for managing authentication tokens
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    // Save authentication tokens
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
            preferences[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    // Save user information
    suspend fun saveUserInfo(id: String, name: String, email: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_ID] = id
            preferences[Keys.USER_NAME] = name
            preferences[Keys.USER_EMAIL] = email
            preferences[Keys.USER_ROLE] = role
        }
    }

    // Get access token
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.ACCESS_TOKEN]
        }
    }

    // Get refresh token
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.REFRESH_TOKEN]
        }
    }

    // Get user info
    fun getUserInfo(): Flow<UserInfo?> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[Keys.USER_ID]
            val name = preferences[Keys.USER_NAME]
            val email = preferences[Keys.USER_EMAIL]
            val role = preferences[Keys.USER_ROLE]

            if (id != null && name != null && email != null && role != null) {
                UserInfo(id, name, email, role)
            } else null
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.ACCESS_TOKEN] != null
        }
    }

    // Clear all stored data (logout)
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Data class for user info
    data class UserInfo(
        val id: String,
        val name: String,
        val email: String,
        val role: String
    )
}
