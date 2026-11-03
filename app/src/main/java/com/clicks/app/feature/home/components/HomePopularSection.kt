package com.clicks.app.feature.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.menu.ui.components.ProductCard
import com.clicks.app.feature.menu.viewmodel.StoreViewModel

@Composable
fun HomePopularSection(
    products: List<StoreProduct>,
    storeViewModel: StoreViewModel,
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
                        isFavorite = storeViewModel.isFavorite(product.id),
                        isInCart = storeViewModel.isInCart(product.id),
                        onCartClick = { storeViewModel.toggleCart(product.id) },
                        onFavoriteClick = { storeViewModel.toggleFavorite(product.id) },
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
