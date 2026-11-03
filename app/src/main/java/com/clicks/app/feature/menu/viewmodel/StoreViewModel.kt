package com.clicks.app.feature.menu.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.checkout.model.OrderRequest
import com.clicks.app.feature.cart.MAX_CART_QUANTITY
import com.clicks.app.feature.cart.data.CartLocalRepository
import com.clicks.app.feature.home.components.ShopCategory
import com.clicks.app.feature.menu.data.ProductCategoriesRepository
import com.clicks.app.feature.menu.data.ProductItemsRepository
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val CART_TAG = "StoreViewModel"
private const val ORDER_TAG = "OrderRequest"

data class CartItem(
    val product: StoreProduct,
    val quantity: Int = 1
)

data class OrderItem(
    val orderId: String,
    val date: String,
    val total: Double,
    val status: String,
    val itemsSummary: String
)

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    var isRead: Boolean = false
)

class StoreViewModel : ViewModel() {
    private val productItemsRepository = ProductItemsRepository()
    private val productCategoriesRepository = ProductCategoriesRepository()
    private val cartLocalRepository = CartLocalRepository(ApiClient.appContext)
    private val cartMutex = Mutex()
    private val cartReady = CompletableDeferred<Unit>()
    private val cartEditsBeforeRestore = mutableSetOf<String>()
    private var cartClearedBeforeRestore = false

    val products = mutableStateListOf<StoreProduct>()
    val categories = mutableStateListOf<ShopCategory>()
    var isCategoriesLoading by mutableStateOf(false)
        private set
    var categoriesError by mutableStateOf<String?>(null)
        private set
    private var hasLoadedCategories = false

    var isProductsLoading by mutableStateOf(false)
        private set
    var productsError by mutableStateOf<String?>(null)
        private set
    private var hasLoadedProducts = false

    var isCartLoading by mutableStateOf(true)
        private set
    var cartPersistenceError by mutableStateOf<String?>(null)
        private set

    val allProducts: List<StoreProduct>
        get() = products

    val favorites = mutableStateListOf<String>()
    val cartItems = mutableStateListOf<CartItem>()
    val orders = mutableStateListOf(
        OrderItem("ORD-78421", "Oct 24, 2023", 34.50, "Delivered", "Truffle Burger & Fries, Caramel Iced Latte"),
        OrderItem("ORD-78390", "Sep 15, 2023", 26.00, "In Transit", "Wood-Fired Margherita Pizza, Berry Smoothie")
    )

    init {
        viewModelScope.launch {
            cartMutex.withLock {
                var restoreSucceeded = false
                try {
                    val savedItems = cartLocalRepository.loadCart()
                    if (!cartClearedBeforeRestore) {
                        val existingIds = cartItems.mapTo(mutableSetOf()) { it.product.id }
                        savedItems
                            .filter { it.quantity > 0 }
                            .filterNot {
                                it.product.id in existingIds ||
                                    it.product.id in cartEditsBeforeRestore
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
                    Log.e(CART_TAG, "Failed to restore cart from Room", exception)
                    cartPersistenceError = "Could not restore your saved cart."
                } finally {
                    isCartLoading = false
                    cartReady.complete(Unit)
                }

                if (restoreSucceeded) {
                    persistCartLocked()
                }
            }
        }
    }

    fun clearSession() {
        favorites.clear()
        cartItems.clear()
        orders.clear()
        orders.addAll(
            listOf(
                OrderItem("ORD-78421", "Oct 24, 2023", 34.50, "Delivered", "Truffle Burger & Fries, Caramel Iced Latte"),
                OrderItem("ORD-78390", "Sep 15, 2023", 26.00, "In Transit", "Wood-Fired Margherita Pizza, Berry Smoothie")
            )
        )
        if (!cartReady.isCompleted) cartClearedBeforeRestore = true
        persistCart()
    }

    val notifications = mutableStateListOf(
        NotificationItem(1, "Order Delivered", "Your order #ORD-78421 has been successfully delivered.", "2h ago", false),
        NotificationItem(2, "Special Offer", "Flash sale! Enjoy 40% off on all new summer arrivals.", "1d ago", false),
        NotificationItem(3, "Order Dispatched", "Your order #ORD-78390 is out for delivery.", "2d ago", true)
    )

    val unreadNotificationCount: Int
        get() = notifications.count { !it.isRead }

    fun loadProducts(force: Boolean = false) {
        if (isProductsLoading || (hasLoadedProducts && !force)) return
        isProductsLoading = true
        productsError = null
        viewModelScope.launch {
            try {
                when (val result = productItemsRepository.loadProducts()) {
                    is ApiResult.Success -> {
                        products.clear()
                        products.addAll(result.data)
                        hasLoadedProducts = true
                    }
                    is ApiResult.Error -> productsError = result.message
                }
            } finally {
                isProductsLoading = false
            }
        }
    }

    fun loadCategories(force: Boolean = false) {
        if (isCategoriesLoading || (hasLoadedCategories && !force)) return
        isCategoriesLoading = true
        categoriesError = null
        viewModelScope.launch {
            try {
                when (val result = productCategoriesRepository.loadCategories()) {
                    is ApiResult.Success -> {
                        categories.clear()
                        categories.addAll(result.data)
                        hasLoadedCategories = true
                    }
                    is ApiResult.Error -> categoriesError = result.message
                }
            } finally {
                isCategoriesLoading = false
            }
        }
    }

    fun markAllNotificationsRead() {
        notifications.forEach { it.isRead = true }
    }

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartSubtotal: Double
        get() = cartItems.sumOf { it.product.price * it.quantity }

    fun isFavorite(productId: String): Boolean = favorites.contains(productId)

    fun toggleFavorite(productId: String) {
        if (favorites.contains(productId)) {
            favorites.remove(productId)
        } else {
            favorites.add(productId)
        }
    }

    fun isInCart(productId: String): Boolean = cartItems.any { it.product.id == productId }

    fun toggleCart(productId: String) {
        if (isInCart(productId))
            removeFromCart(productId) else addToCart(productId)
    }

    fun cartQuantityOf(productId: String): Int = cartItems.firstOrNull { it.product.id == productId }?.quantity ?: 0

    fun addToCart(productId: String, quantity: Int = 1) {
        val product = allProducts.firstOrNull { it.id == productId } ?: return
        if (quantity <= 0) return
        markCartEdited(productId)
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(
                quantity = (cartItems[index].quantity + quantity).coerceAtMost(MAX_CART_QUANTITY)
            )
        } else {
            cartItems.add(CartItem(product, quantity.coerceAtMost(MAX_CART_QUANTITY)))
        }
        persistCart()
    }

    fun updateQuantity(productId: String, delta: Int) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index < 0) return
        markCartEdited(productId)
        val newQuantity = cartItems[index].quantity + delta
        if (newQuantity <= 0) {
            cartItems.removeAt(index)
        } else {
            cartItems[index] = cartItems[index].copy(
                quantity = newQuantity.coerceAtMost(MAX_CART_QUANTITY)
            )
        }
        persistCart()
    }

    fun removeFromCart(productId: String) {
        markCartEdited(productId)
        cartItems.removeAll { it.product.id == productId }
        persistCart()
    }

    fun placeOrder(orderRequest: OrderRequest) {
        if (cartItems.isEmpty()) return
        val requestJson = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(orderRequest)
        Log.i(ORDER_TAG, "Simulated order POST request:\n$requestJson")

        val newOrder = OrderItem(
            orderId = "ORD-${(10000..99999).random()}",
            date = "Today",
            total = cartSubtotal + 80.0,
            status = "Processing",
            itemsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
        )
        orders.add(0, newOrder)
        cartItems.clear()
        if (!cartReady.isCompleted) cartClearedBeforeRestore = true
        persistCart()
    }

    private fun markCartEdited(productId: String) {
        if (!cartReady.isCompleted) cartEditsBeforeRestore.add(productId)
    }

    private fun persistCart() {
        viewModelScope.launch {
            cartReady.await()
            cartMutex.withLock {
                persistCartLocked()
            }
        }
    }

    private suspend fun persistCartLocked() {
        try {
            cartLocalRepository.replaceCart(cartItems.toList())
            cartPersistenceError = null
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Log.e(CART_TAG, "Failed to persist cart to Room", exception)
            cartPersistenceError = "Could not save your cart. Please try again."
        }
    }
}
