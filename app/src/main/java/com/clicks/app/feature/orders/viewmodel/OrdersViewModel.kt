package com.clicks.app.feature.orders.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.checkout.data.OrdersRepository
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.feature.orders.model.OrderLineItem
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {
    private val ordersRepository = OrdersRepository()

    val orders = mutableStateListOf<OrderItem>()
    var isOrdersLoading by mutableStateOf(false)
        private set
    var ordersError by mutableStateOf<String?>(null)
        private set

    fun loadOrders() {
        if (isOrdersLoading) return
        isOrdersLoading = true
        ordersError = null
        viewModelScope.launch {
            try {
                when (val result = ordersRepository.loadOrders()) {
                    is ApiResult.Success -> {
                        orders.clear()
                        orders.addAll(result.data)
                    }
                    is ApiResult.Error -> ordersError = result.message
                }
            } catch (exception: CancellationException) {
                throw exception
            } finally {
                isOrdersLoading = false
            }
        }
    }

    fun orderById(orderId: String): OrderItem? =
        orders.firstOrNull { it.orderId == orderId }

    fun addPlacedOrderPreview(
        orderId: String,
        productsAndQuantities: List<Pair<StoreProduct, Int>>,
        total: Double,
        address: String,
        latitude: String,
        longitude: String
    ) {
        val items = productsAndQuantities.map { (product, quantity) ->
            OrderLineItem(product, quantity)
        }
        orders.removeAll { it.orderId == orderId }
        orders.add(
            0,
            OrderItem(
                orderId = orderId,
                date = "Today",
                total = total,
                status = "Processing",
                itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.product.name}" },
                items = items,
                deliveryAddress = address,
                deliveryLatitude = latitude.toDoubleOrNull(),
                deliveryLongitude = longitude.toDoubleOrNull()
            )
        )
    }

    fun clearSession() {
        orders.clear()
        ordersError = null
    }
}
