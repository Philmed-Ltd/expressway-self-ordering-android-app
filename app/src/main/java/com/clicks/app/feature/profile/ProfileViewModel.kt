package com.clicks.app.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.clicks.app.feature.auth.data.AuthRepository
import com.clicks.app.feature.auth.data.dto.CustomerRegisterData
import com.clicks.app.lib.session.SessionManager

data class ProfileUiState(
    val isLoading: Boolean = false,
    val customer: CustomerRegisterData? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile() {
        // Profile state handled locally or via session
    }

    fun logout() {
        authRepository.logout()
        SessionManager.triggerSessionExpired()
    }

    fun clearSession() {
        uiState = ProfileUiState()
    }
}
