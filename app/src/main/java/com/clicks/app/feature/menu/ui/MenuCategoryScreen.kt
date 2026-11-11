package com.clicks.app.feature.menu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.clicks.app.R
import com.clicks.app.feature.menu.ui.components.ProductCard
import com.clicks.app.feature.menu.viewmodel.MenuViewModel
import com.clicks.app.feature.cart.viewmodel.CartViewModel
import com.clicks.app.feature.favorites.viewmodel.FavoritesViewModel
import com.clicks.app.lib.components.SearchBar
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.ProductCategoryBg

@Composable
fun CategoryProductsScreen(
    categoryKey: String? = null,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        menuViewModel.loadProducts()
        menuViewModel.loadCategories()
    }
    LaunchedEffect(menuViewModel.products.toList(), favoritesViewModel.isFavoritesLoading) {
        favoritesViewModel.refreshFavoriteSnapshots(menuViewModel.products)
    }

    var selectedCategory by remember(categoryKey) { mutableStateOf(categoryKey) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Recommended") }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val cartCount = cartViewModel.cartItemCount

    val categories = menuViewModel.categories
    val categoryTitle = selectedCategory?.let { key ->
        categories.firstOrNull {
            it.categoryId == key || it.name.equals(key, ignoreCase = true)
        }?.name ?: key
    } ?: stringResource(R.string.txt_all_meals_dishes)

    val filteredProducts = menuViewModel.allProducts.filter { product ->
        val selected = categories.firstOrNull {
            it.categoryId == selectedCategory || it.name.equals(selectedCategory, ignoreCase = true)
        }
        val matchesCategory = when {
            selectedCategory == null -> true
            selected?.categoryId != null -> product.categoryId == selected.categoryId
            else -> product.category.equals(categoryTitle, ignoreCase = true)
        }
        val matchesSearch = searchQuery.isBlank() ||
            product.name.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val displayedProducts = when (sortOption) {
        "Price: Low to High" -> filteredProducts.sortedBy { it.price }
        "Price: High to Low" -> filteredProducts.sortedByDescending { it.price }
        else -> filteredProducts
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.txt_back),
                        tint = ClicksText
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoryTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Text(
                        text = if (selectedCategory == null) {
                            stringResource(R.string.txt_gourmet_burgers_pizzas_mains)
                        } else {
                            categoryTitle
                        },
                        fontSize = 12.sp,
                        color = ClicksMuted
                    )
                }

                IconButton(onClick = {
                    isSearchOpen = !isSearchOpen
                    if (!isSearchOpen) searchQuery = ""
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.txt_search),
                        tint = ClicksText
                    )
                }

                Box(modifier = Modifier.clickable(onClick = onCartClick)) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = stringResource(R.string.txt_cart),
                            tint = ClicksText
                        )
                    }
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Error)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isSearchOpen) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.txt_search_in_category, categoryTitle)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 16.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val categoryFilters = listOf(null to stringResource(R.string.txt_all)) +
                    categories.map { (it.categoryId ?: it.name) to it.name }
                categoryFilters.forEach { (key, label) ->
                    val selected = selectedCategory == key
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { selectedCategory = key },
                        shape = CircleShape,
                        color = if (selected) ClicksGreen else ProductCategoryBg,
                        border = if (selected) null else BorderStroke(2.dp, ProductCategoryBg)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            fontSize = 13.sp,
                            color = if (selected) Color.White else ClicksText,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.txt_product_count, displayedProducts.size),
                    fontSize = 14.sp,
                    color = ClicksMuted
                )

                Box {
                    Row(
                        modifier = Modifier.clickable { sortMenuExpanded = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.txt_sort),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Sort,
                            contentDescription = stringResource(R.string.txt_sort_products),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        listOf(
                            "Recommended" to R.string.txt_sort_recommended,
                            "Price: Low to High" to R.string.txt_sort_price_low_high,
                            "Price: High to Low" to R.string.txt_sort_price_high_to_low
                        ).forEach { (option, labelResource) ->
                            DropdownMenuItem(
                                text = { Text(stringResource(labelResource)) },
                                onClick = {
                                    sortOption = option
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            when {
                menuViewModel.isProductsLoading && menuViewModel.allProducts.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                menuViewModel.productsError != null && menuViewModel.products.isEmpty() &&
                    menuViewModel.allProducts.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = menuViewModel.productsError.orEmpty(),
                            color = ClicksMuted
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { menuViewModel.loadProducts(force = true) }) {
                            Text(stringResource(R.string.txt_retry), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                displayedProducts.isEmpty() && menuViewModel.productsError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(menuViewModel.productsError.orEmpty(), color = ClicksMuted)
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { menuViewModel.loadProducts(force = true) }) {
                            Text(stringResource(R.string.txt_retry), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                displayedProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.txt_no_products_found),
                            color = ClicksMuted
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 4.dp,
                            bottom = 20.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(displayedProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = favoritesViewModel.isFavorite(product.id),
                                isInCart = cartViewModel.isInCart(product.id),
                                onCartClick = { cartViewModel.toggleCart(product) },
                                onFavoriteClick = {
                                    favoritesViewModel.toggleFavorite(product.id, menuViewModel.products)
                                },
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
