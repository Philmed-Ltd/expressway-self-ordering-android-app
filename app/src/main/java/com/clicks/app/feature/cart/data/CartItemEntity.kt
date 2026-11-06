package com.clicks.app.feature.cart.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.menu.viewmodel.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val category: String,
    val categoryId: String?,
    val price: Double,
    val rating: Double?,
    val reviewCount: Int,
    val tag: String?,
    val imageUrl: String?,
    val description: String,
    val quantity: Int
) {
    fun toCartItem() = CartItem(
        product = StoreProduct(
            id = productId,
            name = name,
            category = category,
            categoryId = categoryId,
            price = price,
            rating = rating,
            reviewCount = reviewCount,
            tag = tag,
            imageUrl = imageUrl,
            description = description
        ),
        quantity = quantity
    )

    companion object {
        fun from(cartItem: CartItem) = CartItemEntity(
            productId = cartItem.product.id,
            name = cartItem.product.name,
            category = cartItem.product.category,
            categoryId = cartItem.product.categoryId,
            price = cartItem.product.price,
            rating = cartItem.product.rating,
            reviewCount = cartItem.product.reviewCount,
            tag = cartItem.product.tag,
            imageUrl = cartItem.product.imageUrl,
            description = cartItem.product.description,
            quantity = cartItem.quantity
        )
    }
}
