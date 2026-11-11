package com.clicks.app.feature.checkout.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.clicks.app.feature.checkout.data.OrdersRepository
import com.clicks.app.feature.checkout.data.dto.OrderRequest
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult

class CheckoutViewModel : ViewModel() {
    private val ordersRepository = OrdersRepository()

    var mpesaPhone by mutableStateOf("")
    var isProcessingPayment by mutableStateOf(false)
        private set
    var isPaymentConfirmed by mutableStateOf(false)
        private set
    var transactionCode by mutableStateOf("")
        private set
    var showSuccessDialog by mutableStateOf(false)
    var isPlacingOrder by mutableStateOf(false)
        private set
    var paymentError by mutableStateOf<String?>(null)
        private set
    var isOrderSummaryExpanded by mutableStateOf(true)
    var placedOrderId by mutableStateOf<String?>(null)
        private set
    var placedSubtotal by mutableStateOf<Double?>(null)
        private set
    var orderError by mutableStateOf<String?>(null)
        private set

    suspend fun placeOrder(request: OrderRequest, subtotal: Double): ApiResult<String> {
        orderError = null
        isPlacingOrder = true
        return try {
            when (val result = ordersRepository.placeOrder(request)) {
                is ApiResult.Success -> {
                    placedOrderId = result.data
                    placedSubtotal = subtotal
                    result
                }
                is ApiResult.Error -> {
                    orderError = result.message
                    result
                }
            }
        } finally {
            isPlacingOrder = false
        }
    }

    suspend fun initializePayment(amount: Double): ApiResult<Unit> {
        val orderId = placedOrderId ?: return ApiResult.Error(
            "The order ID is missing. Please place the order again.",
            ApiErrorType.BadRequest
        )
        paymentError = null
        isProcessingPayment = true
        return try {
            when (
                val result = ordersRepository.initializePayment(
                    salesOrderId = orderId,
                    phoneNumber = mpesaPhone,
                    amount = amount
                )
            ) {
                is ApiResult.Success -> {
                    transactionCode = ""
                    isPaymentConfirmed = true
                    showSuccessDialog = true
                    result
                }
                is ApiResult.Error -> {
                    paymentError = result.message
                    result
                }
            }
        } finally {
            isProcessingPayment = false
        }
    }

    fun startNewCheckout() {
        mpesaPhone = ""
        isProcessingPayment = false
        isPaymentConfirmed = false
        transactionCode = ""
        showSuccessDialog = false
        isPlacingOrder = false
        paymentError = null
        isOrderSummaryExpanded = true
        placedOrderId = null
        placedSubtotal = null
        orderError = null
    }

    fun clearSession() {
        startNewCheckout()
    }
}
