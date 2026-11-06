package com.clicks.app.feature.menu.data

import retrofit2.http.GET
import com.clicks.app.feature.menu.data.dtos.ProductCategoryDto
import com.clicks.app.network.dto.EntityApiResponse


interface ProductCategoriesApi {
    @GET("customer/product-categories/list")
    suspend fun listProductCategories(): EntityApiResponse<List<ProductCategoryDto>>
}
