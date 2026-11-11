package com.clicks.app.feature.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.auth.data.AuthRepository
import com.clicks.app.feature.auth.data.dto.CustomerView
import com.clicks.app.lib.session.SessionManager
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val customer: CustomerView? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = authRepository.fetchCustomer()) {
                is ApiResult.Success -> {
                    uiState = ProfileUiState(isLoading = false, customer = result.data)
                }
                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        SessionManager.triggerSessionExpired()
    }

    fun clearSession() {
        uiState = ProfileUiState()
    }
}
