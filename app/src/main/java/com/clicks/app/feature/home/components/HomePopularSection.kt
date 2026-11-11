package com.clicks.app.feature.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.menu.ui.components.ProductCard
import com.clicks.app.feature.cart.viewmodel.CartViewModel
import com.clicks.app.feature.favorites.viewmodel.FavoritesViewModel

@Composable
fun HomePopularSection(
    products: List<StoreProduct>,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        products.chunked(2).forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        product = product,
                        isFavorite = favoritesViewModel.isFavorite(product.id),
                        isInCart = cartViewModel.isInCart(product.id),
                        onCartClick = { cartViewModel.toggleCart(product) },
                        onFavoriteClick = {
                            favoritesViewModel.toggleFavorite(product.id, products)
                        },
                        onClick = { onProductClick(product.id) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowProducts.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
