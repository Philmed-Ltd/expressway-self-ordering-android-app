package com.clicks.app.feature.checkout.model

data class OrderRequest(
    val orderId: String,
    val deliveryAddress: OrderDeliveryAddress,
    val items: List<OrderRequestItem>,
    val paymentMethod: String
)

data class OrderDeliveryAddress(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val instructions: String
)

data class OrderRequestItem(
    val productItemEntityId: String,
    val quantity: Int,
    val unitPrice: Double
)
