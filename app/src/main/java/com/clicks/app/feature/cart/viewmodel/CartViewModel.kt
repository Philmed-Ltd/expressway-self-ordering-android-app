package com.clicks.app.feature.cart.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.cart.data.CartLocalRepository
import com.clicks.app.feature.cart.data.MAX_CART_QUANTITY
import com.clicks.app.feature.cart.model.CartItem
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.network.ApiClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "CartViewModel"

class CartViewModel : ViewModel() {
    private val localRepository = CartLocalRepository(ApiClient.appContext)
    private val mutex = Mutex()
    private val ready = CompletableDeferred<Unit>()
    private val editsBeforeRestore = mutableSetOf<String>()
    private var clearedBeforeRestore = false

    val cartItems = mutableStateListOf<CartItem>()
    var isCartLoading by mutableStateOf(true)
        private set
    var cartPersistenceError by mutableStateOf<String?>(null)
        private set

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartSubtotal: Double
        get() = cartItems.sumOf { it.product.price * it.quantity }

    init {
        viewModelScope.launch {
            mutex.withLock {
                var restoreSucceeded = false
                try {
                    val savedItems = localRepository.loadCart()
                    if (!clearedBeforeRestore) {
                        val existingIds = cartItems.mapTo(mutableSetOf()) { it.product.id }
                        savedItems
                            .filter { it.quantity > 0 }
                            .filterNot {
                                it.product.id in existingIds ||
                                    it.product.id in editsBeforeRestore
                            }
                            .forEach { saved ->
                                cartItems.add(
                                    saved.copy(quantity = saved.quantity.coerceAtMost(MAX_CART_QUANTITY))
                                )
                            }
                    }
                    restoreSucceeded = true
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    Log.e(TAG, "Failed to restore cart from Room", exception)
                    cartPersistenceError = "Could not restore your saved cart."
                } finally {
                    isCartLoading = false
                    ready.complete(Unit)
                }

                if (restoreSucceeded) persistLocked()
            }
        }
    }

    fun isInCart(productId: String): Boolean =
        cartItems.any { it.product.id == productId }

    fun cartQuantityOf(productId: String): Int =
        cartItems.firstOrNull { it.product.id == productId }?.quantity ?: 0

    fun toggleCart(product: StoreProduct) {
        if (isInCart(product.id)) removeFromCart(product.id) else addToCart(product)
    }

    fun addToCart(product: StoreProduct, quantity: Int = 1) {
        if (quantity <= 0) return
        markEdited(product.id)
        val index = cartItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(
                quantity = (cartItems[index].quantity + quantity).coerceAtMost(MAX_CART_QUANTITY)
            )
        } else {
            cartItems.add(CartItem(product, quantity.coerceAtMost(MAX_CART_QUANTITY)))
        }
        persist()
    }

    fun updateQuantity(productId: String, delta: Int) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index < 0) return
        markEdited(productId)
        val newQuantity = cartItems[index].quantity + delta
        if (newQuantity <= 0) {
            cartItems.removeAt(index)
        } else {
            cartItems[index] = cartItems[index].copy(
                quantity = newQuantity.coerceAtMost(MAX_CART_QUANTITY)
            )
        }
        persist()
    }

    fun removeFromCart(productId: String) {
        markEdited(productId)
        cartItems.removeAll { it.product.id == productId }
        persist()
    }

    fun clearCart() {
        cartItems.forEach { markEdited(it.product.id) }
        cartItems.clear()
        if (!ready.isCompleted) clearedBeforeRestore = true
        persist()
    }

    fun clearSession() = clearCart()

    fun reorder(order: OrderItem): Boolean {
        if (order.items.isEmpty()) return false
        order.items.forEach { item ->
            val productId = item.product.id
            markEdited(productId)
            val index = cartItems.indexOfFirst { it.product.id == productId }
            if (index >= 0) {
                cartItems[index] = cartItems[index].copy(
                    quantity = (cartItems[index].quantity + item.quantity)
                        .coerceAtMost(MAX_CART_QUANTITY)
                )
            } else {
                cartItems.add(
                    CartItem(
                        product = item.product,
                        quantity = item.quantity.coerceAtMost(MAX_CART_QUANTITY)
                    )
                )
            }
        }
        persist()
        return cartItems.isNotEmpty()
    }

    private fun markEdited(productId: String) {
        if (!ready.isCompleted) editsBeforeRestore.add(productId)
    }

    private fun persist() {
        viewModelScope.launch {
            ready.await()
            mutex.withLock { persistLocked() }
        }
    }

    private suspend fun persistLocked() {
        try {
            localRepository.replaceCart(cartItems.toList())
            cartPersistenceError = null
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to persist cart to Room", exception)
            cartPersistenceError = "Could not save your cart. Please try again."
        }
    }
}
