package com.clicks.app.feature.menu.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clicks.app.feature.home.components.ShopCategory
import com.clicks.app.feature.menu.data.ProductCategoriesRepository
import com.clicks.app.feature.menu.data.ProductItemsRepository
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.network.ApiErrorType
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val productItemsRepository = ProductItemsRepository()
    private val productCategoriesRepository = ProductCategoriesRepository()

    val products = mutableStateListOf<StoreProduct>()
    val categories = mutableStateListOf<ShopCategory>()

    var isCategoriesLoading by mutableStateOf(false)
        private set
    var categoriesError by mutableStateOf<String?>(null)
        private set
    var isCategoriesNetworkError by mutableStateOf(false)
        private set
    private var hasLoadedCategories = false

    var isProductsLoading by mutableStateOf(false)
        private set
    var productsError by mutableStateOf<String?>(null)
        private set
    var isProductsNetworkError by mutableStateOf(false)
        private set
    private var hasLoadedProducts = false

    val allProducts: List<StoreProduct>
        get() = products

    val isHomeOffline: Boolean
        get() = !isProductsLoading &&
            !isCategoriesLoading &&
            products.isEmpty() &&
            categories.isEmpty() &&
            isProductsNetworkError &&
            isCategoriesNetworkError

    fun loadProducts(force: Boolean = false) {
        if (isProductsLoading || (hasLoadedProducts && !force)) return
        isProductsLoading = true
        productsError = null
        isProductsNetworkError = false
        viewModelScope.launch {
            try {
                when (val result = productItemsRepository.loadProducts()) {
                    is ApiResult.Success -> {
                        products.clear()
                        products.addAll(result.data)
                        hasLoadedProducts = true
                    }
                    is ApiResult.Error -> {
                        productsError = result.message
                        isProductsNetworkError = result.type == ApiErrorType.Network
                    }
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
        isCategoriesNetworkError = false
        viewModelScope.launch {
            try {
                when (val result = productCategoriesRepository.loadCategories()) {
                    is ApiResult.Success -> {
                        categories.clear()
                        categories.addAll(result.data)
                        hasLoadedCategories = true
                    }
                    is ApiResult.Error -> {
                        categoriesError = result.message
                        isCategoriesNetworkError = result.type == ApiErrorType.Network
                    }
                }
            } finally {
                isCategoriesLoading = false
            }
        }
    }
}
