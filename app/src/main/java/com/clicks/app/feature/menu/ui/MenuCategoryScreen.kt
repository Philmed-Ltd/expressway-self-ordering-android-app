package com.clicks.app.feature.menu.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.clicks.app.R
import com.clicks.app.feature.menu.model.sampleProducts
import com.clicks.app.feature.menu.ui.components.ProductCard
import com.clicks.app.feature.menu.viewmodel.StoreViewModel
import com.clicks.app.lib.components.SearchBar
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.ProductCategoryBg

data class ProductCategory(
    val title: String,
    val subtitle: String,
    val subcategories: List<String>
)

@Composable
fun CategoryProductsScreen(
    categoryTitle: String = "All Meals & Dishes",
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    val categories = listOf(
        ProductCategory(
            title = "All Meals & Dishes",
            subtitle = "Gourmet burgers, pizzas & mains",
            subcategories = listOf("All", "Burgers", "Pizza", "Pasta", "Salads", "Sandwiches")
        ),
        ProductCategory(
            title = "Drinks & Smoothies",
            subtitle = "Fresh, iced, and hot drinks",
            subcategories = listOf("All", "Iced Coffees", "Teas", "Smoothies")
        ),
        ProductCategory(
            title = "Bakery & Desserts",
            subtitle = "Baked fresh daily",
            subcategories = listOf("All", "Croissants", "Desserts")
        )
    )

    val category = categories.find { it.title.equals(categoryTitle, ignoreCase = true) }
        ?: categories.first()
    val categoryLabel = when (category.title) {
        "Drinks & Smoothies" -> stringResource(R.string.txt_drinks_smoothies)
        "Bakery & Desserts" -> stringResource(R.string.txt_bakery_desserts)
        else -> stringResource(R.string.txt_all_meals_dishes)
    }
    val categorySubtitle = when (category.title) {
        "Drinks & Smoothies" -> stringResource(R.string.txt_fresh_iced_hot_drinks)
        "Bakery & Desserts" -> stringResource(R.string.txt_baked_fresh_daily)
        else -> stringResource(R.string.txt_gourmet_burgers_pizzas_mains)
    }
    @Composable
    fun subcategoryLabel(value: String) = when (value) {
        "All" -> stringResource(R.string.txt_all)
        "Burgers" -> stringResource(R.string.txt_burgers)
        "Pizza" -> stringResource(R.string.txt_pizza)
        "Pasta" -> stringResource(R.string.txt_pasta)
        "Salads" -> stringResource(R.string.txt_salads)
        "Sandwiches" -> stringResource(R.string.txt_sandwiches)
        "Iced Coffees" -> stringResource(R.string.txt_iced_coffees)
        "Teas" -> stringResource(R.string.txt_teas)
        "Smoothies" -> stringResource(R.string.txt_smoothies)
        "Croissants" -> stringResource(R.string.txt_croissants)
        "Desserts" -> stringResource(R.string.txt_desserts)
        else -> value
    }

    var selectedSubcategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Recommended") }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val cartCount = storeViewModel.cartItemCount

    val allProducts = sampleProducts.filter {
        when (category.title) {
            "All Meals & Dishes" -> it.category == "Burgers" || it.category == "Pizza" || it.category == "Mains"
            "Drinks & Smoothies" -> it.category == "Drinks"
            "Bakery & Desserts" -> it.category == "Bakery" || it.category == "Desserts"
            else -> true
        }
    }

    val filteredProducts = allProducts.filter { product ->
        val matchesSubcategory = selectedSubcategory == "All" || product.tag.equals(selectedSubcategory, ignoreCase = true) || product.category.equals(selectedSubcategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true)
        matchesSubcategory && matchesSearch
    }

    val displayedProducts = when (sortOption) {
        "Price: Low to High" -> filteredProducts.sortedBy { it.price }
        "Price: High to Low" -> filteredProducts.sortedByDescending { it.price }
        "Top Rated" -> filteredProducts.sortedByDescending { it.rating }
        else -> filteredProducts
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .background(Color.White)
        ) {
            // Header
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
                        text = categoryLabel,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Text(
                        text = categorySubtitle,
                        fontSize = 12.sp,
                        color = ClicksMuted
                    )
                }

                // Search Toggle Icon Button
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

                Spacer(Modifier.width(4.dp))

                Box(
                    modifier = Modifier.clickable { onCartClick() }
                ) {
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

            // Search bar (collapsible/toggleable)
            AnimatedVisibility(visible = isSearchOpen) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.txt_search_in_category, categoryLabel)
                )
            }

            // Subcategories horizontal pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 20.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                category.subcategories.forEach { subcategory ->
                    val selected = selectedSubcategory == subcategory

                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                selectedSubcategory = subcategory
                            },
                        shape = CircleShape,
                        color = if (selected) ClicksGreen else Color.White,
                        border = if (selected) null else
                            BorderStroke(
                                1.dp,
                                ProductCategoryBg
                            )
                    ) {
                        Text(
                            text = subcategoryLabel(subcategory),
                            modifier = Modifier.padding(
                                horizontal = 23.dp,
                                vertical = 13.dp
                            ),
                            fontSize = 13.sp,
                            color = if (selected) Color.White else ClicksText,
                            fontWeight = if (selected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }

            // Count and sort
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
                        modifier = Modifier.clickable {
                            sortMenuExpanded = true
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.txt_sort),
                            fontSize = 14.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Outlined.Sort,
                            contentDescription = stringResource(R.string.txt_sort_products),
                            tint = ClicksGreen,
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
                            "Price: High to Low" to R.string.txt_sort_price_high_to_low,
                            "Top Rated" to R.string.txt_sort_top_rated
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

            // Scrollable two-column product grid
            if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.txt_no_products_found),
                        color = ClicksMuted
                    )
                }
            } else {
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
                    items(
                        items = displayedProducts,
                        key = { it.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = storeViewModel.isFavorite(product.id),
                            isInCart = storeViewModel.isInCart(product.id),
                            onCartClick = {
                                storeViewModel.toggleCart(product.id)
                            },
                            onFavoriteClick = {
                                storeViewModel.toggleFavorite(product.id)
                            },
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}
