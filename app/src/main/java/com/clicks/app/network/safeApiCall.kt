package com.clicks.app.network

import com.clicks.app.network.dto.ApiResponse
import com.clicks.app.network.dto.EntityApiResponse

/**
 * Safe API call handler for EntityApiResponse<T> using Kotlin Coroutines (suspend fun).
 * Note: Instead of RxJava Observables (Observable<EntityApiResponse<T>>),
 * Kotlin Coroutines use suspend fun, which is more idiomatic, lightweight, and built directly into Android.
 */
suspend fun <T> safeEntityApiCall(
    call: suspend () -> EntityApiResponse<T>
): ApiResult<T> {
    return try {
        val response = call()
        val data = response.data

        if (response.status && data != null) {
            ApiResult.Success(data = data, message = response.message.orEmpty())
        } else {
            ApiResult.Error(
                message = response.message.orEmpty().ifBlank {
                    "The request could not be completed."
                },
                type = ApiErrorType.Server,
                code = response.statusCode
            )
        }
    } catch (e: Throwable) {
        val errorResponse = EntityApiResponse<T>().onErrorResponse(e)
        ApiResult.Error(
            message = errorResponse.message.orEmpty().ifBlank {
                e.localizedMessage ?: "Something went wrong. Please try again."
            },
            type = ApiErrorType.Unknown
        )
    }
}

/**
 * Safe API call handler for base ApiResponse without payload data.
 */
suspend fun safeApiCallNoData(
    call: suspend () -> ApiResponse
): ApiResult<Unit> {
    return try {
        val response = call()

        if (response.status) {
            ApiResult.Success(data = Unit, message = response.message.orEmpty())
        } else {
            ApiResult.Error(
                message = response.message.orEmpty().ifBlank {
                    "The request could not be completed."
                },
                type = ApiErrorType.Server,
                code = response.statusCode
            )
        }
    } catch (e: Throwable) {
        val errorResponse = ApiResponse().onErrorResponse(e)
        ApiResult.Error(
            message = errorResponse.message.orEmpty().ifBlank {
                e.localizedMessage ?: "Something went wrong. Please try again."
            },
            type = ApiErrorType.Unknown
        )
    }
}
