package com.clicks.app.feature.checkout.data.dto

data class OrderRequest(
    val deliveryAddress: OrderDeliveryAddress,
    val items: List<OrderRequestItem>
)

data class OrderDeliveryAddress(
    val address: String,
    val latitude: String,
    val longitude: String,
    val instructions: String
)

data class OrderRequestItem(
    val productItemEntityId: String,
    val quantity: Int
)

data class PlaceOrderResponse(
    val orderId: String? = null
)

data class InitializePaymentRequest(
    val mpesaPhoneNumber: String,
    val amount: Double
)
