package com.clicks.app.feature.auth.data.dto

data class InitLoginRequest(
    val usernameType: String, // "PHONE_NUMBER" or "EMAIL"
    val username: String,     // phone number or email
    val password: String
)

data class InitLoginResponseData(
    val id: String, // verificationCodeId
    val createdAt: String?,
    val expiryTime: String?
)
