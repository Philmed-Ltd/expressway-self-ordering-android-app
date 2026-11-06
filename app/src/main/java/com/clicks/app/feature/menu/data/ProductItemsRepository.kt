package com.clicks.app.feature.menu.data

import android.util.Log
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ProductItemsRepository"

class ProductItemsRepository(
    private val api: ProductItemsApi = ApiClient.createService()
) {
    suspend fun loadProducts(): ApiResult<List<StoreProduct>> {
        return try {
            val response = api.listProductItems()
            val items = response.data ?: return ApiResult.Error(
                message = "The product response did not contain a data list.",
                type = ApiErrorType.Server
            )

            ApiResult.Success(
                data = items
                    .asSequence()
                    .filter { !it.isDeleted && (it.productStatus == null || it.productStatus.equals("ACTIVE", ignoreCase = true)) }
                    .map { item ->
                        StoreProduct(
                            id = item.id,
                            name = item.name,
                            category = item.productCategoryName.orEmpty(),
                            categoryId = item.productCategoryEntityId,
                            price = item.price,
                            rating = null,
                            reviewCount = 0,
                            tag = item.productCategoryName,
                            imageUrl = item.imageUrl.orEmpty(),
                            description = item.description.orEmpty()
                        )
                    }
                    .toList()
            )
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to load product items", exception)
            ApiResult.Error(
                message = "Unable to connect to the product service. Check the network and try again.",
                type = ApiErrorType.Network
            )
        } catch (exception: HttpException) {
            val requestUrl = exception.response()?.raw()?.request?.url
            Log.e(TAG, "Product service returned HTTP ${exception.code()} for $requestUrl", exception)
            ApiResult.Error(
                message = "Product service error (${exception.code()}). Please try again.",
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
            Log.e(TAG, "Failed to decode product items", exception)
            ApiResult.Error(
                message = "Unable to load products. Please try again.",
                type = ApiErrorType.Unknown
            )
        }
    }
}
