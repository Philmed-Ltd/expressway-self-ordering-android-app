package com.clicks.app.feature.auth.data

import android.util.Log
import com.clicks.app.feature.auth.data.dto.CompleteLoginRequest
import com.clicks.app.feature.auth.data.dto.CustomerRegisterData
import com.clicks.app.feature.auth.data.dto.CustomerRegisterRequest
import com.clicks.app.feature.auth.data.dto.CustomerView
import com.clicks.app.feature.auth.data.dto.InitLoginRequest
import com.clicks.app.feature.auth.data.dto.InitLoginResponseData
import com.clicks.app.feature.auth.data.dto.VerificationCodeData
import com.clicks.app.feature.auth.data.dto.VerificationCodeRequest
import com.clicks.app.feature.auth.data.dto.ResetPasswordRequest
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import com.clicks.app.network.safeEntityApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AuthRepository"

class AuthRepository(
    private val authApi: AuthApi = ApiClient.createService<AuthApi>(),
    private val authLocalSrc: AuthenticationLocalSrc = AuthenticationLocalSrc(ApiClient.appContext)
) {

    suspend fun requestVerificationCode(
        usernameType: String,
        username: String,
        verificationCodeUse: String
    ): ApiResult<VerificationCodeData> {
        val request = VerificationCodeRequest(
            usernameType = usernameType,
            username = username.trim(),
            verificationCodeUse = verificationCodeUse
        )
        return safeEntityApiCall {
            authApi.requestVerificationCode(request)
        }
    }

    suspend fun initLogin(
        usernameType: String,
        username: String,
        password: String
    ): ApiResult<InitLoginResponseData> {
        val request = InitLoginRequest(
            usernameType = usernameType,
            username = username.trim(),
            password = password
        )
        return safeEntityApiCall {
            authApi.initLogin(request)
        }
    }

    suspend fun registerCustomer(
        request: CustomerRegisterRequest
    ): ApiResult<CustomerRegisterData> {
        val result = safeEntityApiCall {
            authApi.registerCustomer(request)
        }

        return persistAuthentication(result, "registration")
    }

    suspend fun completeLogin(
        verificationCodeId: String,
        otp: String
    ): ApiResult<CustomerRegisterData> {
        val result = safeEntityApiCall {
            authApi.completeLogin(verificationCodeId, CompleteLoginRequest(otp))
        }

        return persistAuthentication(result, "login")
    }

    suspend fun resetPassword(
        verificationCodeId: String,
        request: ResetPasswordRequest
    ): ApiResult<CustomerRegisterData> {
        val result = safeEntityApiCall {
            authApi.resetPassword(verificationCodeId, request)
        }

        return persistAuthentication(result, "password reset")
    }

    suspend fun fetchCustomer(): ApiResult<CustomerView> {
        return safeEntityApiCall {
            authApi.fetchCustomer()
        }
    }

    fun logout() {
        authLocalSrc.delete()
    }

    private suspend fun persistAuthentication(
        result: ApiResult<CustomerRegisterData>,
        operation: String
    ): ApiResult<CustomerRegisterData> {
        if (result !is ApiResult.Success) return result

        if (result.data.authToken.isNullOrBlank()) {
            return ApiResult.Error(
                message = "The server did not return an authentication token. Please try again.",
                type = ApiErrorType.Server
            )
        }

        return try {
            withContext(Dispatchers.IO) {
                authLocalSrc.insertSync(result.data.toAuthentication())
            }
            result
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to save session token after $operation", exception)
            ApiResult.Error(
                message = "Could not save your session. Please try signing in again.",
                type = ApiErrorType.Unknown
            )
        }
    }
}
