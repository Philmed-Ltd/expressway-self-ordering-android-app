package com.clicks.app.feature.favorites.data

import android.content.Context
import com.clicks.app.feature.favorites.model.FavoriteProductEntity
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.lib.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesLocalRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).favoriteProductDao()

    suspend fun loadFavorites(): List<StoreProduct> = withContext(Dispatchers.IO) {
        dao.getAll().map(FavoriteProductEntity::toStoreProduct)
    }

    suspend fun replaceFavorites(products: List<StoreProduct>) = withContext(Dispatchers.IO) {
        dao.replaceAll(products.map(FavoriteProductEntity::from))
    }
}
