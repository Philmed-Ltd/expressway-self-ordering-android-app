package com.clicks.app.feature.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.auth.data.AuthRepository
import com.clicks.app.feature.auth.data.dto.CustomerRegisterRequest
import com.clicks.app.feature.auth.data.dto.PhoneVerificationCodePayload
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

data class PhoneAuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isCodeSent: Boolean = false
)

class PhoneAuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val authLocalSrc: AuthenticationLocalSrc = AuthenticationLocalSrc(ApiClient.appContext)
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
            uiState.value = PhoneAuthUiState(errorMessage = "Please enter a valid $usernameType")
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
                            "OTP has been sent to $cleanIdentifier."
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
            uiState.value = PhoneAuthUiState(errorMessage = "Please enter a valid $usernameType")
            return
        }
        if (password.length < 8) {
            uiState.value = PhoneAuthUiState(errorMessage = "Password must be at least 8 characters")
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
                            "Login OTP has been sent to $cleanIdentifier."
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
            uiState.value = uiState.value.copy(errorMessage = "Please enter the 5-digit verification code")
            return
        }
        if (cleanFirst.length < 3) {
            uiState.value = uiState.value.copy(errorMessage = "First name must be at least 3 characters")
            return
        }
        if (cleanLast.length < 3) {
            uiState.value = uiState.value.copy(errorMessage = "Last name must be at least 3 characters")
            return
        }
        if (password.length < 8) {
            uiState.value = uiState.value.copy(errorMessage = "Password must be at least 8 characters")
            return
        }
        if (verificationCodeId.isBlank()) {
            uiState.value = uiState.value.copy(errorMessage = "Verification session expired. Please re-enter details.")
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
                    // Cache Authentication entity into Room DB
                    try {
                        withContext(Dispatchers.IO) {
                            val auth = Authentication().apply {
                                id = 1
                                authToken = result.data.authToken ?: "customer-token-${result.data.id}"
                                expiryTime = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
                            }
                            authLocalSrc.insertSync(auth)
                        }
                    } catch (e: Exception) {
                        // ignore or log
                    }

                    uiState.value = PhoneAuthUiState(
                        isLoading = false,
                        successMessage = result.message.ifBlank { "User registration success." }
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
            uiState.value = uiState.value.copy(errorMessage = "Please enter the 5-digit verification code")
            return
        }
        if (verificationCodeId.isBlank()) {
            uiState.value = uiState.value.copy(errorMessage = "Verification session expired. Please re-enter details.")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            delay(1000.milliseconds)
            try {
                withContext(Dispatchers.IO) {
                    val token = "login-session-$verificationCodeId"
                    val auth = Authentication().apply {
                        id = 1
                        authToken = token
                        expiryTime = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
                    }
                    authLocalSrc.insertSync(auth)
                }
                uiState.value = PhoneAuthUiState(
                    isLoading = false,
                    successMessage = "Sign in success!"
                )
                onSuccess()
            } catch (e: Exception) {
                uiState.value = PhoneAuthUiState(
                    isLoading = false,
                    errorMessage = "Login failed. Please try again."
                )
            }
        }
    }

    fun clearMessages() {
        uiState.value = uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
