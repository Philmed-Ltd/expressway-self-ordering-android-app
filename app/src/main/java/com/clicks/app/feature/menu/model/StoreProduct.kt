package com.clicks.app.feature.menu.model

data class StoreProduct(
    val id: String,
    val name: String,
    val category: String,
    val categoryId: String? = null,
    val price: Double,
    val rating: Double?,
    val reviewCount: Int,
    val tag: String?,
    val imageUrl: String?,
    val description: String = ""
)
