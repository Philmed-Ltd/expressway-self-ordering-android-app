package com.clicks.app.feature.favorites.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.favorites.data.FavoritesLocalRepository
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.network.ApiClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel : ViewModel() {
    private val localRepository = FavoritesLocalRepository(ApiClient.appContext)
    private val mutex = Mutex()
    private val ready = CompletableDeferred<Unit>()
    private val editsBeforeRestore = mutableSetOf<String>()
    private var clearedBeforeRestore = false

    val favoriteProducts = mutableStateListOf<StoreProduct>()
    var isFavoritesLoading by mutableStateOf(true)
        private set
    var favoritePersistenceError by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            mutex.withLock {
                var restoreSucceeded = false
                try {
                    val savedFavorites = localRepository.loadFavorites()
                    if (!clearedBeforeRestore) {
                        savedFavorites
                            .filterNot { it.id in editsBeforeRestore }
                            .forEach { favoriteProducts.add(it) }
                    }
                    restoreSucceeded = true
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    Log.e(TAG, "Failed to restore favorites from Room", exception)
                    favoritePersistenceError = "Could not restore your saved favorites."
                } finally {
                    isFavoritesLoading = false
                    ready.complete(Unit)
                }

                if (restoreSucceeded) persistLocked()
            }
        }
    }

    fun isFavorite(productId: String): Boolean =
        favoriteProducts.any { it.id == productId }

    fun toggleFavorite(productId: String, products: List<StoreProduct>) {
        val index = favoriteProducts.indexOfFirst { it.id == productId }
        if (index >= 0) {
            favoriteProducts.removeAt(index)
        } else {
            val product = products.firstOrNull { it.id == productId } ?: return
            favoriteProducts.add(product)
        }
        if (!ready.isCompleted) editsBeforeRestore.add(productId)
        persist()
    }

    fun clearSession() {
        favoriteProducts.clear()
        if (!ready.isCompleted) clearedBeforeRestore = true
        persist()
    }

    fun refreshFavoriteSnapshots(updatedProducts: List<StoreProduct>) {
        if (favoriteProducts.isEmpty()) return
        val updatedById = updatedProducts.associateBy { it.id }
        favoriteProducts.indices.forEach { index ->
            updatedById[favoriteProducts[index].id]?.let {
                favoriteProducts[index] = it
            }
        }
        persist()
    }

    private fun persist() {
        viewModelScope.launch {
            ready.await()
            mutex.withLock { persistLocked() }
        }
    }

    private suspend fun persistLocked() {
        try {
            localRepository.replaceFavorites(favoriteProducts.toList())
            favoritePersistenceError = null
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to persist favorites to Room", exception)
            favoritePersistenceError = "Could not save your favorites. Please try again."
        }
    }
}
