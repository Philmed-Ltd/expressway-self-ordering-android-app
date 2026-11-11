package com.clicks.app.feature.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import coil.compose.AsyncImage
import com.clicks.app.R
import com.clicks.app.feature.cart.data.MAX_CART_QUANTITY
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.ProductBgLight
import com.clicks.app.lib.theme.StarGold
import com.clicks.app.feature.menu.viewmodel.MenuViewModel
import com.clicks.app.feature.cart.viewmodel.CartViewModel
import com.clicks.app.feature.favorites.viewmodel.FavoritesViewModel
import com.clicks.app.lib.components.QuantityPill
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsScreen(
    productId: String = "1",
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onAddToCartClick: () -> Unit = {},
    onSimilarProductClick: (String) -> Unit = {},
) {
    val product = menuViewModel.products.firstOrNull { it.id == productId }

    if (product == null) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = menuViewModel.productsError
                        ?: stringResource(R.string.txt_no_products_found),
                    color = ClicksMuted
                )
                if (menuViewModel.isProductsLoading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else if (menuViewModel.productsError != null) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { menuViewModel.loadProducts(force = true) }) {
                        Text(stringResource(R.string.txt_retry), color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onBackClick) {
                    Text(stringResource(R.string.txt_back), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        return
    }

    var quantity by rememberSaveable(productId) { mutableIntStateOf(1) }
    val isFavorite = favoritesViewModel.isFavorite(productId)
    val cartCount = cartViewModel.cartItemCount
    val inCart = cartViewModel.isInCart(productId)
    val cartQty = cartViewModel.cartQuantityOf(productId)
    val shownQuantity = if (inCart) cartQty else quantity
    val addedToCartMessage = stringResource(R.string.txt_added_to_cart)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            if (!inCart) {
                                cartViewModel.addToCart(product, quantity)
                                onAddToCartClick()
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        addedToCartMessage,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onCartClick()
                                    }
                                }
                            } else {
                                onCartClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!inCart) ClicksGreen else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (!inCart) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (!inCart) {
                                stringResource(R.string.txt_add_to_cart_price, product.price * quantity)
                            } else {
                                pluralStringResource(R.plurals.txt_view_cart_count, cartQty, cartQty)
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.txt_back),
                        tint = ClicksText
                    )
                }

                Text(
                    text = stringResource(R.string.txt_details),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )

                // Cart Icon with red badge dot
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

            Spacer(Modifier.height(16.dp))

            // Product Image Banner with Favorite Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ProductBgLight)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = {
                        favoritesViewModel.toggleFavorite(productId, menuViewModel.products)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.txt_favorite),
                        tint = if (isFavorite) ClicksGreen else ClicksText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Product Name & Rating
            Text(
                text = product.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(8.dp))

            if (product.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(R.string.txt_rating),
                        tint = StarGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${product.rating}/5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClicksText
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.txt_reviews_count, product.reviewCount),
                        fontSize = 14.sp,
                        color = ClicksMuted
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                text = product.description.ifBlank {
                    stringResource(R.string.txt_product_description)
                },
                fontSize = 14.sp,
                color = ClicksMuted,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = stringResource(R.string.txt_price), fontSize = 12.sp, color = ClicksMuted)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Ksh " + "%.2f".format(product.price),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                }

                QuantityPill(
                    quantity = shownQuantity,
                    canDecrease = shownQuantity > 1,
                    canIncrease = shownQuantity < MAX_CART_QUANTITY,
                    onDecrease = {
                        if (inCart) {
                            if (shownQuantity > 1) {
                                cartViewModel.updateQuantity(productId, -1)
                            }
                        } else {
                            if (quantity > 1) {
                                quantity--
                            }
                        }
                    },
                    onIncrease = {
                        if (inCart) {
                            if (shownQuantity < MAX_CART_QUANTITY) {
                                cartViewModel.updateQuantity(productId, 1)
                            }
                        } else {
                            if (quantity < MAX_CART_QUANTITY) {
                                quantity++
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            // Similar To Section
            Text(
                text = stringResource(R.string.txt_similar_to),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                menuViewModel.allProducts.filter { it.id != product.id }.take(4).forEach { similar ->
                    Column(
                        modifier = Modifier
                            .width(140.dp)
                            .clickable { onSimilarProductClick(similar.id) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ProductBgLight)
                        ) {
                            AsyncImage(
                                model = similar.imageUrl,
                                contentDescription = similar.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = similar.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksText,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Ksh " + "%.2f".format(similar.price),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
