package com.clicks.app.feature.orders.model

import com.clicks.app.feature.menu.model.StoreProduct

data class OrderItem(
    val orderId: String,
    val date: String,
    val total: Double,
    val status: String,
    val itemsSummary: String,
    val items: List<OrderLineItem> = emptyList(),
    val deliveryAddress: String? = null,
    val deliveryLatitude: Double? = null,
    val deliveryLongitude: Double? = null,
    val paymentReference: String? = null
)

data class OrderLineItem(
    val product: StoreProduct,
    val quantity: Int
)
