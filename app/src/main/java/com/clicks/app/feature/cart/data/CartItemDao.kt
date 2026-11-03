package com.clicks.app.feature.cart.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items")
    fun getAll(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<CartItemEntity>): List<Long>

    @Query("DELETE FROM cart_items")
    fun deleteAll(): Int
}
