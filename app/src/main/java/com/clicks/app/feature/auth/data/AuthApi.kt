package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.CustomerRegisterData
import com.clicks.app.feature.auth.data.dto.CustomerRegisterRequest
import com.clicks.app.feature.auth.data.dto.InitLoginRequest
import com.clicks.app.feature.auth.data.dto.InitLoginResponseData
import com.clicks.app.feature.auth.data.dto.VerificationCodeData
import com.clicks.app.feature.auth.data.dto.VerificationCodeRequest
import com.clicks.app.feature.auth.data.dto.ResetPasswordRequest
import com.clicks.app.network.dto.EntityApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @POST("customer/verification-code/request")
    suspend fun requestVerificationCode(
        @Body request: VerificationCodeRequest
    ): EntityApiResponse<VerificationCodeData>

    @POST("customer/auth/register")
    suspend fun registerCustomer(
        @Body request: CustomerRegisterRequest
    ): EntityApiResponse<CustomerRegisterData>

    @POST("customer/auth/init-login")
    suspend fun initLogin(
        @Body request: InitLoginRequest
    ): EntityApiResponse<InitLoginResponseData>

    @POST("customer/auth/reset-password/{verificationCodeId}")
    suspend fun resetPassword(
        @Path("verificationCodeId") verificationCodeId: String,
        @Body request: ResetPasswordRequest
    ): EntityApiResponse<CustomerRegisterData>
}
