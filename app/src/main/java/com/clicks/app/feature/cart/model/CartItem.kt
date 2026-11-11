package com.clicks.app.feature.cart.model

import com.clicks.app.feature.menu.model.StoreProduct

data class CartItem(
    val product: StoreProduct,
    val quantity: Int = 1
)
