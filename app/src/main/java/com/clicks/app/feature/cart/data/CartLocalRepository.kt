package com.clicks.app.feature.cart.data

import android.content.Context
import com.clicks.app.feature.menu.viewmodel.CartItem
import com.clicks.app.lib.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartLocalRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).cartItemDao()

    suspend fun loadCart(): List<CartItem> = withContext(Dispatchers.IO) {
        dao.getAll().map { it.toCartItem() }
    }

    suspend fun replaceCart(items: List<CartItem>) {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
            if (items.isNotEmpty()) {
                dao.insertAll(items.map(CartItemEntity::from))
            }
        }
    }
}
