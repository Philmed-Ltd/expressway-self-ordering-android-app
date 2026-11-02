package com.clicks.app.feature.auth.data.dto

import com.clicks.app.feature.auth.model.Authentication

data class PhoneVerificationCodePayload(
    val verificationCodeId: String,
    val otp: String
)

data class CustomerRegisterRequest(
    val name: String,
    val countryCode: String,
    val phoneCode: String,
    val phoneNumber: String,
    val phoneVerificationCode: PhoneVerificationCodePayload,
    val password: String
)

data class CustomerRegisterData(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val phoneCode: String,
    val countryCode: String,
    val emailVerified: Boolean,
    val phoneNumberVerified: Boolean,
    val userStatus: String,
    val authToken: String? = null,
    val expiryTime: String? = null,
    val createdAt: String,
    val updatedAt: String
) {
    fun toAuthentication(): Authentication {
        return Authentication().apply {
            id = 1
            authToken = this@CustomerRegisterData.authToken ?: ""
            expiryTime = Authentication.parseIsoDate(this@CustomerRegisterData.expiryTime)
        }
    }
}
