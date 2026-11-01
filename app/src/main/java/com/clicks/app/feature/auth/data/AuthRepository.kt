package com.clicks.app.feature.auth.data

import android.util.Log
import com.clicks.app.feature.auth.data.dto.CustomerRegisterData
import com.clicks.app.feature.auth.data.dto.CustomerRegisterRequest
import com.clicks.app.feature.auth.data.dto.InitLoginRequest
import com.clicks.app.feature.auth.data.dto.InitLoginResponseData
import com.clicks.app.feature.auth.data.dto.VerificationCodeData
import com.clicks.app.feature.auth.data.dto.VerificationCodeRequest
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import com.clicks.app.network.safeEntityApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

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

        if (result is ApiResult.Success) {
            try {
                withContext(Dispatchers.IO) {
                    val auth = Authentication().apply {
                        id = 1
                        authToken = result.data.authToken ?: "customer-session-${result.data.id}"
                        expiryTime = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
                    }
                    authLocalSrc.insertSync(auth)
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to save session token", exception)
            }
        }

        return result
    }

    fun logout() {
        authLocalSrc.delete()
    }
}
