package com.clicks.app.network.dto

class EntityApiResponse<T> : ApiResponse() {
    var data: T? = null

    override fun onErrorResponse(e: Throwable): EntityApiResponse<T> {
        val apiResponse = super.onErrorResponse(e)

        val response = EntityApiResponse<T>()
        response.status = apiResponse.status
        response.statusCode = apiResponse.statusCode
        response.message = apiResponse.message

        return response
    }
}
