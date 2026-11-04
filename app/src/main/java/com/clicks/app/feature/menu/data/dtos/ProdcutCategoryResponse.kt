package com.clicks.app.feature.menu.data.dtos

data class ProductCategoryDto(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val isDeleted: Boolean?
)