package com.clicks.app.feature.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.R
import com.clicks.app.feature.auth.data.AuthRepository
import com.clicks.app.feature.auth.data.dto.ResetPasswordRequest
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isCodeSent: Boolean = false
)

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState = mutableStateOf(ForgotPasswordUiState())
        private set

    fun requestOtp(
        phoneNumber: String,
        onCodeSent: (String) -> Unit
    ) {
        if (uiState.value.isLoading) return
        val cleanPhone = phoneNumber.trim()
        if (cleanPhone.isBlank()) {
            uiState.value = ForgotPasswordUiState(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_valid_phone)
            )
            return
        }

        uiState.value = ForgotPasswordUiState(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.requestVerificationCode(
                usernameType = "PHONE_NUMBER",
                username = cleanPhone,
                verificationCodeUse = "AUTHENTICATION"
            )) {
                is ApiResult.Success -> {
                    val codeId = result.data.id
                    uiState.value = ForgotPasswordUiState(
                        isLoading = false,
                        isCodeSent = true,
                        successMessage = result.message.ifBlank {
                            ApiClient.appContext.getString(R.string.txt_otp_sent_to, cleanPhone)
                        }
                    )
                    onCodeSent(codeId)
                }
                is ApiResult.Error -> {
                    uiState.value = ForgotPasswordUiState(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun resetPassword(
        verificationCodeId: String,
        otp: String,
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        if (uiState.value.isLoading) return

        val cleanOtp = otp.trim()
        val cleanPass = newPassword.trim()

        if (cleanOtp.length != 5) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_enter_5_digit_otp)
            )
            return
        }
        if (cleanPass.length < 8) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_password_min_length)
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val request = ResetPasswordRequest(otp = cleanOtp, password = cleanPass)
            when (val result = authRepository.resetPassword(verificationCodeId, request)) {
                is ApiResult.Success -> {
                    uiState.value = ForgotPasswordUiState(
                        isLoading = false,
                        successMessage = result.message.ifBlank {
                            ApiClient.appContext.getString(R.string.txt_password_changed_success)
                        }
                    )
                    onSuccess()
                }
                is ApiResult.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        uiState.value = uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
