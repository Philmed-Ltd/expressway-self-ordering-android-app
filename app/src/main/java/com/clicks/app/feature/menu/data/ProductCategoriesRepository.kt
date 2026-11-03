package com.clicks.app.feature.menu.data

import android.util.Log
import com.clicks.app.feature.home.components.ShopCategory
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ProductCategoriesRepository"

class ProductCategoriesRepository(
    private val api: ProductCategoriesApi = ApiClient.createService()
) {
    suspend fun loadCategories(): ApiResult<List<ShopCategory>> {
        return try {
            val response = api.listProductCategories()
            val categories = response.data ?: return ApiResult.Error(
                message = "The category response did not contain a data list.",
                type = ApiErrorType.Server
            )

            ApiResult.Success(
                data = categories
                    .asSequence()
                    .filterNot { it.isDeleted == true }
                    .map { category ->
                        ShopCategory(
                            name = category.name,
                            imageUrl = category.imageUrl,
                            categoryId = category.id
                        )
                    }
                    .toList()
            )
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to load product categories", exception)
            ApiResult.Error(
                message = "Unable to connect to the category service. Check the network and try again.",
                type = ApiErrorType.Network
            )
        } catch (exception: HttpException) {
            val requestUrl = exception.response()?.raw()?.request?.url
            Log.e(TAG, "Category service returned HTTP ${exception.code()} for $requestUrl", exception)
            ApiResult.Error(
                message = "Category service error (${exception.code()}). Please try again.",
                type = when (exception.code()) {
                    401 -> ApiErrorType.Unauthorized
                    403 -> ApiErrorType.Forbidden
                    404 -> ApiErrorType.NotFound
                    in 500..599 -> ApiErrorType.Server
                    else -> ApiErrorType.BadRequest
                },
                code = exception.code()
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to decode product categories", exception)
            ApiResult.Error(
                message = "Unable to load categories. Please try again.",
                type = ApiErrorType.Unknown
            )
        }
    }
}
