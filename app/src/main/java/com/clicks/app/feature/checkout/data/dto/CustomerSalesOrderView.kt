package com.clicks.app.feature.checkout.data.dto

data class CustomerSalesOrderView(
    val id: String? = null,
    val orderId: String? = null,
    val address: SalesOrderAddress? = null,
    val saleOrderItems: List<SalesOrderItemView> = emptyList(),
    val total: Double? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val date: String? = null
)

data class SalesOrderAddress(
    val address: String? = null,
    val instructions: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
)

data class SalesOrderItemView(
    val id: String? = null,
    val saleOrderItemId: String? = null,
    val productItemEntityId: String? = null,
    val productItemName: String? = null,
    val quantity: Int? = null,
    val productItemUrl: String? = null,
    val lineTotal: Double? = null
)
