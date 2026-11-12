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
    val authToken: String? = null,
    val expiryTime: String? = null,
    val id: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val phoneCode: String? = null,
    val countryCode: String? = null,
    val emailVerified: Boolean? = null,
    val phoneNumberVerified: Boolean? = null,
    val userStatus: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    fun toAuthentication(): Authentication? {
        val parsedExpiryTime = Authentication.parseIsoDate(expiryTime) ?: return null
        if (parsedExpiryTime.time <= System.currentTimeMillis()) return null
        return Authentication().apply {
            id = 1
            this.authToken = this@CustomerRegisterData.authToken.orEmpty()
            this.expiryTime = parsedExpiryTime
        }
    }
}
