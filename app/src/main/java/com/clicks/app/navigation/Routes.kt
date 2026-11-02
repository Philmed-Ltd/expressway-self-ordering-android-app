package com.clicks.app.navigation

import android.net.Uri

sealed class Routes(val route: String) {

    data object Onboarding : Routes("onboarding")

    data object PhoneAuth : Routes("phone_auth")

    data object PhoneVerification : Routes("phone_verification/{countryCode}/{phoneNumber}/{verificationCodeId}/{authMode}") {
        fun createRoute(countryCode: String, phoneNumber: String, verificationCodeId: String, authMode: String): String {
            val encodedCode = Uri.encode(countryCode)
            val encodedPhone = Uri.encode(phoneNumber)
            val encodedId = Uri.encode(verificationCodeId)
            val encodedMode = Uri.encode(authMode)
            return "phone_verification/$encodedCode/$encodedPhone/$encodedId/$encodedMode"
        }
    }

    data object Home : Routes("home")

    data object Products : Routes("products")

    data object ProductDetails : Routes("product_details/{productId}") {
        fun createRoute(productId: Int) = "product_details/$productId"
    }

    data object Cart : Routes("cart")

    data object Favorites : Routes("favorites")

    data object Checkout : Routes("checkout")

    data object Profile : Routes("profile")

    data object Orders : Routes("orders")

    data object LocationPicker : Routes("location_picker")
}
