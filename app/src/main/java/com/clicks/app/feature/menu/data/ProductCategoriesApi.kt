package com.clicks.app.feature.menu.data

import retrofit2.http.GET

interface ProductCategoriesApi {
    @GET("customer/product-categories/list")
    suspend fun listProductCategories(): ProductCategoriesResponse
}

data class ProductCategoriesResponse(
    val data: List<ProductCategoryDto>?
)

data class ProductCategoryDto(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val isDeleted: Boolean?
)
