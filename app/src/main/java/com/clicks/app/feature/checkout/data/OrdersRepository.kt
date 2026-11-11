package com.clicks.app.feature.checkout.data

import android.util.Log
import com.clicks.app.feature.checkout.data.dto.CustomerSalesOrderView
import com.clicks.app.feature.checkout.data.dto.InitializePaymentRequest
import com.clicks.app.feature.checkout.data.dto.OrderRequest
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.feature.orders.model.OrderLineItem
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.CancellationException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "OrdersRepository"

class OrdersRepository(
    private val api: OrdersApi = ApiClient.createService()
) {
    suspend fun loadOrders(): ApiResult<List<OrderItem>> {
        return try {
            val response = api.listOrders()
            val data = response.data
            if (!response.status || data == null) {
                return ApiResult.Error(
                    message = response.message.orEmpty().ifBlank {
                        "Could not load your orders. Please try again."
                    },
                    type = ApiErrorType.Server,
                    code = response.statusCode
                )
            }

            ApiResult.Success(data.map(::toOrderItem), response.message.orEmpty())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to load customer orders", exception)
            ApiResult.Error(
                message = "Unable to connect to the order service. Check the network and try again.",
                type = ApiErrorType.Network
            )
        } catch (exception: HttpException) {
            Log.e(TAG, "Order service returned HTTP ${exception.code()}", exception)
            ApiResult.Error(
                message = "Order service error (${exception.code()}). Please try again.",
                type = httpErrorType(exception.code()),
                code = exception.code()
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to decode customer orders", exception)
            ApiResult.Error(
                message = "Unable to load your orders. Please try again.",
                type = ApiErrorType.Unknown
            )
        }
    }

    suspend fun placeOrder(request: OrderRequest): ApiResult<String> {
        val validationMessage = validate(request)
        if (validationMessage != null) {
            return ApiResult.Error(validationMessage, ApiErrorType.BadRequest)
        }

        return try {
            val response = api.registerOrder(request)
            val orderId = response.data?.orderId?.takeIf(String::isNotBlank)
            if (response.status && orderId != null) {
                ApiResult.Success(orderId, response.message.orEmpty())
            } else {
                ApiResult.Error(
                    message = response.message.orEmpty().ifBlank {
                        "The server did not return an order ID. Please try again."
                    },
                    type = ApiErrorType.Server,
                    code = response.statusCode
                )
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to place order", exception)
            ApiResult.Error(
                message = "Unable to connect to the order service. Check the network and try again.",
                type = ApiErrorType.Network
            )
        } catch (exception: HttpException) {
            Log.e(TAG, "Order service returned HTTP ${exception.code()}", exception)
            ApiResult.Error(
                message = "Order service error (${exception.code()}). Please try again.",
                type = httpErrorType(exception.code()),
                code = exception.code()
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to place order", exception)
            ApiResult.Error(
                message = "Unable to place your order. Please try again.",
                type = ApiErrorType.Unknown
            )
        }
    }

    suspend fun initializePayment(
        salesOrderId: String,
        phoneNumber: String,
        amount: Double
    ): ApiResult<Unit> {
        val normalizedPhone = normalizeKenyanPhoneNumber(phoneNumber)
            ?: return ApiResult.Error(
                message = "Enter a valid Kenyan M-Pesa phone number.",
                type = ApiErrorType.BadRequest
            )
        if (salesOrderId.isBlank()) {
            return ApiResult.Error(
                "The order ID is missing. Please place the order again.",
                ApiErrorType.BadRequest
            )
        }
        if (!amount.isFinite() || amount < 1.0) {
            return ApiResult.Error(
                message = "The payment amount must be at least 1.",
                type = ApiErrorType.BadRequest
            )
        }

        return try {
            val response = api.initializePayment(
                salesOrderId = salesOrderId,
                request = InitializePaymentRequest(
                    mpesaPhoneNumber = normalizedPhone,
                    amount = amount
                )
            )
            if (response.status) {
                ApiResult.Success(Unit, response.message.orEmpty())
            } else {
                val responseCode = response.statusCode.takeIf { it > 0 }
                    ?.let { " (HTTP $it)" }
                    .orEmpty()
                ApiResult.Error(
                    message = response.message.orEmpty()
                        .takeIf(String::isNotBlank)
                        ?.let { "Payment initialization was rejected$responseCode: $it" }
                        ?: "Payment initialization was rejected$responseCode. Please try again.",
                    type = ApiErrorType.Server,
                    code = response.statusCode
                )
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to initialize M-Pesa payment", exception)
            ApiResult.Error(
                message = "Unable to connect to the payment service. Check the network and try again.",
                type = ApiErrorType.Network
            )
        } catch (exception: HttpException) {
            Log.e(TAG, "Payment service returned HTTP ${exception.code()}", exception)
            ApiResult.Error(
                message = paymentHttpErrorMessage(exception),
                type = httpErrorType(exception.code()),
                code = exception.code()
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to initialize M-Pesa payment", exception)
            ApiResult.Error(
                message = "Unable to start your payment. Please try again.",
                type = ApiErrorType.Unknown
            )
        }
    }

    private fun validate(request: OrderRequest): String? {
        if (request.deliveryAddress.address.isBlank()) {
            return "A delivery address is required."
        }
        if (request.items.isEmpty()) {
            return "Add at least one item before placing your order."
        }
        if (request.items.any { it.productItemEntityId.isBlank() || it.quantity <= 0 }) {
            return "Each order item must have a valid product and a positive quantity."
        }
        return null
    }

    private fun toOrderItem(order: CustomerSalesOrderView): OrderItem {
        val lines = order.saleOrderItems.mapNotNull { item ->
            val quantity = item.quantity?.takeIf { it > 0 } ?: return@mapNotNull null
            val itemId = item.productItemEntityId ?: item.id ?: item.saleOrderItemId ?: return@mapNotNull null
            val itemTotal = item.lineTotal ?: 0.0
            OrderLineItem(
                product = StoreProduct(
                    id = itemId,
                    name = item.productItemName ?: "Product",
                    category = "",
                    price = itemTotal / quantity,
                    rating = null,
                    reviewCount = 0,
                    tag = null,
                    imageUrl = item.productItemUrl,
                    description = ""
                ),
                quantity = quantity
            )
        }
        val itemSummary = lines.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
            .ifBlank { "Order items" }
        val address = order.address

        return OrderItem(
            orderId = order.orderId ?: order.id.orEmpty(),
            date = order.createdAt ?: order.date.orEmpty(),
            total = order.total ?: lines.sumOf { it.product.price * it.quantity },
            status = order.status ?: "Processing",
            itemsSummary = itemSummary,
            items = lines,
            deliveryAddress = address?.address,
            deliveryLatitude = address?.latitude?.toDoubleOrNull(),
            deliveryLongitude = address?.longitude?.toDoubleOrNull()
        )
    }

    private fun normalizeKenyanPhoneNumber(input: String): String? {
        val digits = input.filter(Char::isDigit)
        val normalized = when {
            digits.startsWith("254") -> digits
            digits.startsWith("0") -> "254${digits.drop(1)}"
            digits.startsWith("7") || digits.startsWith("1") -> "254$digits"
            else -> return null
        }
        return normalized.takeIf {
            it.length == 12 && it.startsWith("254") &&
                (it[3] == '7' || it[3] == '1')
        }
    }

    private fun paymentHttpErrorMessage(exception: HttpException): String {
        val serverMessage = try {
            exception.response()?.errorBody()?.string()?.let { body ->
                if (body.isBlank()) null
                else {
                    val json = JSONObject(body)
                    json.optString("message").takeIf(String::isNotBlank)
                        ?: json.optString("error").takeIf(String::isNotBlank)
                }
            }
        } catch (exception: JSONException) {
            Log.w(TAG, "Payment service returned a non-JSON error body", exception)
            null
        } catch (exception: IOException) {
            Log.w(TAG, "Could not read payment service error body", exception)
            null
        }

        return serverMessage?.let {
            "Payment request failed (HTTP ${exception.code()}): $it"
        } ?: "Payment request failed (HTTP ${exception.code()}). Please check the order and try again."
    }

    private fun httpErrorType(code: Int): ApiErrorType = when (code) {
        401 -> ApiErrorType.Unauthorized
        403 -> ApiErrorType.Forbidden
        404 -> ApiErrorType.NotFound
        in 500..599 -> ApiErrorType.Server
        else -> ApiErrorType.BadRequest
    }
}
