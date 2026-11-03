package com.clicks.app.feature.menu.data

import retrofit2.http.GET

interface ProductItemsApi {
    @GET("customer/product-items/list")
    suspend fun listProductItems(): ProductItemsResponse
}

data class ProductItemsResponse(
    val data: List<ProductItemDto>?
)

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
