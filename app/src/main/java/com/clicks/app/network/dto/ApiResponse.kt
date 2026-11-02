package com.clicks.app.network.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.util.*

val OBJECT_MAPPER = ObjectMapper()

@JsonIgnoreProperties(ignoreUnknown = true)
open class ApiResponse {
    var message: String? = null
    var status = false
    var statusCode = 0
    var time: Date = Date()

    var hasNext: Boolean = false

    open fun onErrorResponse(e: Throwable): ApiResponse {
        var apiResponse = ApiResponse()
        apiResponse.status = false
        when (e) {
            is IOException -> {
                apiResponse.message = "No internet connection"
            }
            is HttpException -> {
                val httpException: HttpException = e
                val responseBody: ResponseBody? = httpException.response()?.errorBody()
                try {
                    apiResponse = OBJECT_MAPPER.readValue(
                        responseBody!!.string(),
                        ApiResponse::class.java
                    )
                } catch (ignore: IOException) {}
            }
        }

        return apiResponse
    }
}
