package com.clicks.app.feature.favorites.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.clicks.app.feature.menu.model.StoreProduct

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val categoryId: String?,
    val price: Double,
    val rating: Double?,
    val reviewCount: Int,
    val tag: String?,
    val imageUrl: String?,
    val description: String
) {
    fun toStoreProduct() = StoreProduct(
        id = id,
        name = name,
        category = category,
        categoryId = categoryId,
        price = price,
        rating = rating,
        reviewCount = reviewCount,
        tag = tag,
        imageUrl = imageUrl,
        description = description
    )

    companion object {
        fun from(product: StoreProduct) = FavoriteProductEntity(
            id = product.id,
            name = product.name,
            category = product.category,
            categoryId = product.categoryId,
            price = product.price,
            rating = product.rating,
            reviewCount = product.reviewCount,
            tag = product.tag,
            imageUrl = product.imageUrl,
            description = product.description
        )
    }
}
