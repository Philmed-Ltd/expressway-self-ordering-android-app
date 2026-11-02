package com.clicks.app.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.feature.menu.model.sampleProducts
import com.clicks.app.feature.menu.ui.components.ProductCard
import com.clicks.app.feature.menu.viewmodel.StoreViewModel
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText

@Composable
fun FavoritesScreen(
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    val favoriteIds = storeViewModel.favorites
    val favoriteProducts = sampleProducts.filter { it.id in favoriteIds }

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
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
                Text(
                    text = stringResource(R.string.txt_my_favorites_count, favoriteProducts.size),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            if (favoriteProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.txt_empty),
                            tint = ClicksMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.txt_no_favorite_items),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.txt_favorites_empty_hint),
                            fontSize = 14.sp,
                            color = ClicksMuted
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = true,
                            isInCart = storeViewModel.isInCart(product.id),
                            onCartClick = { storeViewModel.toggleCart(product.id) },
                            onFavoriteClick = { storeViewModel.toggleFavorite(product.id) },
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}
