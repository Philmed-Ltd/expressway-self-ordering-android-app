package com.clicks.app.feature.menu.data

import com.clicks.app.feature.menu.data.dtos.ProductItemDto
import com.clicks.app.network.dto.EntityApiResponse
import retrofit2.http.GET

interface ProductItemsApi {
    @GET("customer/product-items/list")
    suspend fun listProductItems(): EntityApiResponse<List<ProductItemDto>>
}
