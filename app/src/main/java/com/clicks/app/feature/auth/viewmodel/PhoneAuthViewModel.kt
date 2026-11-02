package com.clicks.app.feature.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.R
import com.clicks.app.feature.auth.data.AuthRepository
import com.clicks.app.feature.auth.data.dto.CustomerRegisterRequest
import com.clicks.app.feature.auth.data.dto.PhoneVerificationCodePayload
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.launch

data class PhoneAuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isCodeSent: Boolean = false
)

class PhoneAuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState = mutableStateOf(PhoneAuthUiState())
        private set

    fun sendVerificationCode(
        usernameType: String, // "PHONE_NUMBER" or "EMAIL"
        identifier: String,   // phone number or email address
        verificationCodeUse: String, // "REGISTRATION" or "LOGIN"
        onCodeSent: (String) -> Unit // passes verificationCodeId
    ) {
        if (uiState.value.isLoading) return

        val cleanIdentifier = identifier.trim()
        if (cleanIdentifier.isBlank()) {
            val label = if (usernameType == "PHONE_NUMBER") R.string.txt_phone_number else R.string.txt_email_address
            uiState.value = PhoneAuthUiState(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_valid_identifier, ApiClient.appContext.getString(label))
            )
            return
        }

        uiState.value = PhoneAuthUiState(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.requestVerificationCode(usernameType, cleanIdentifier, verificationCodeUse)) {
                is ApiResult.Success -> {
                    val codeId = result.data.id
                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        isCodeSent = true,
                        successMessage = result.message.ifBlank {
                            ApiClient.appContext.getString(R.string.txt_otp_sent_to, cleanIdentifier)
                        }
                    )
                    onCodeSent(codeId)
                }

                is ApiResult.Error -> {
                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun startLogin(
        usernameType: String,
        identifier: String,
        password: String,
        onCodeSent: (String) -> Unit
    ) {
        if (uiState.value.isLoading) return

        val cleanIdentifier = identifier.trim()
        if (cleanIdentifier.isBlank()) {
            val label = if (usernameType == "PHONE_NUMBER") R.string.txt_phone_number else R.string.txt_email_address
            uiState.value = PhoneAuthUiState(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_valid_identifier, ApiClient.appContext.getString(label))
            )
            return
        }
        if (password.length < 8) {
            uiState.value = PhoneAuthUiState(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_password_min_length)
            )
            return
        }

        uiState.value = PhoneAuthUiState(isLoading = true)

        viewModelScope.launch {
            when (val result = authRepository.initLogin(usernameType, cleanIdentifier, password)) {
                is ApiResult.Success -> {
                    val codeId = result.data.id
                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        isCodeSent = true,
                        successMessage = result.message.ifBlank {
                            ApiClient.appContext.getString(R.string.txt_login_otp_sent_to, cleanIdentifier)
                        }
                    )
                    onCodeSent(codeId)
                }

                is ApiResult.Error -> {
                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun verifyAndRegister(
        verificationCodeId: String,
        countryCode: String,
        isoCode: String,
        phoneNumber: String,
        code: String,
        firstName: String,
        lastName: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (uiState.value.isLoading) return

        val cleanCode = code.trim()
        val cleanFirst = firstName.trim()
        val cleanLast = lastName.trim()

        if (cleanCode.length != 5) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_enter_5_digit_code)
            )
            return
        }
        if (cleanFirst.length < 3) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_first_name_min_length)
            )
            return
        }
        if (cleanLast.length < 3) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_last_name_min_length)
            )
            return
        }
        if (password.length < 8) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_password_min_length)
            )
            return
        }
        if (verificationCodeId.isBlank()) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_verification_session_expired)
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        val request = CustomerRegisterRequest(
            name = "$cleanFirst $cleanLast",
            countryCode = isoCode,
            phoneCode = countryCode,
            phoneNumber = phoneNumber,
            phoneVerificationCode = PhoneVerificationCodePayload(
                verificationCodeId = verificationCodeId,
                otp = cleanCode
            ),
            password = password
        )

        viewModelScope.launch {
            when (val result = authRepository.registerCustomer(request)) {
                is ApiResult.Success -> {
                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        successMessage = result.message.ifBlank {
                            ApiClient.appContext.getString(R.string.txt_registration_success)
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

    fun completeLogin(
        verificationCodeId: String,
        code: String,
        onSuccess: () -> Unit
    ) {
        if (uiState.value.isLoading) return

        val cleanCode = code.trim()
        if (cleanCode.length != 5) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_enter_5_digit_code)
            )
            return
        }
        if (verificationCodeId.isBlank()) {
            uiState.value = uiState.value.copy(
                errorMessage = ApiClient.appContext.getString(R.string.txt_error_verification_session_expired)
            )
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.completeLogin(verificationCodeId, cleanCode)) {
                is ApiResult.Success -> {
                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        successMessage = result.message.ifBlank {
                            ApiClient.appContext.getString(R.string.txt_login_success)
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
