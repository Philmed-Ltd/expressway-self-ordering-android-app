package com.clicks.app.feature.menu.data.dtos


data class ProductItemDto(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val price: Double,
    val isDeleted: Boolean,
    val productCategoryEntityId: String?,
    val productCategoryName: String?,
    val productStatus: String?
)