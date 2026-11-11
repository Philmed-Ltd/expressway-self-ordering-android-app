package com.clicks.app.feature.favorites.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.clicks.app.feature.favorites.model.FavoriteProductEntity

@Dao
abstract class FavoriteProductDao {
    @Query("SELECT * FROM favorite_products")
    abstract fun getAll(): List<FavoriteProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(products: List<FavoriteProductEntity>): List<Long>

    @Query("DELETE FROM favorite_products")
    abstract fun deleteAll(): Int

    @Transaction
    open fun replaceAll(products: List<FavoriteProductEntity>) {
        deleteAll()
        if (products.isNotEmpty()) insertAll(products)
    }
}
