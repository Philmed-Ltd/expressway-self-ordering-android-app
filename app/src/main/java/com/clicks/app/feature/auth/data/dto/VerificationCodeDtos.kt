package com.clicks.app.feature.auth.data.dto

data class VerificationCodeRequest(
    val usernameType: String = "PHONE_NUMBER",
    val username: String,
    val verificationCodeUse: String = "REGISTRATION"
)

data class VerificationCodeData(
    val id: String,
    val createdAt: String,
    val expiryTime: String,
    val isDeleted: Boolean,
    val updatedAt: String
)
