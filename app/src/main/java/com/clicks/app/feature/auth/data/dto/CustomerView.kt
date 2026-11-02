package com.clicks.app.feature.auth.data.dto

data class CustomerView(
    val id: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val isDeleted: Boolean?,
    val phoneNumber: String?,
    val phoneNumberVerified: Boolean?,
    val phoneCode: String?,
    val countryCode: String?,
    val name: String?,
    val email: String?,
    val emailVerified: Boolean?,
    val userStatus: String?
) {
    val firstName: String
        get() = name?.split(" ")?.firstOrNull() ?: ""

    val lastName: String
        get() = name?.split(" ")?.drop(1)?.joinToString(" ") ?: ""
}
