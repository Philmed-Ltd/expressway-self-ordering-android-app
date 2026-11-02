package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.CustomerRegisterData
import com.clicks.app.feature.auth.data.dto.CustomerRegisterRequest
import com.clicks.app.feature.auth.data.dto.InitLoginRequest
import com.clicks.app.feature.auth.data.dto.InitLoginResponseData
import com.clicks.app.feature.auth.data.dto.VerificationCodeData
import com.clicks.app.feature.auth.data.dto.VerificationCodeRequest
import com.clicks.app.network.dto.EntityApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

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
}
