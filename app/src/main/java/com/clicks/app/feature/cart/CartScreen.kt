package com.clicks.app.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.R
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.StarGold
import com.clicks.app.lib.theme.SurfaceLight
import com.clicks.app.lib.theme.SurfaceLightAlt
import com.clicks.app.feature.menu.viewmodel.CartItem
import com.clicks.app.feature.menu.viewmodel.StoreViewModel

@Composable
fun CartScreen(
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onProceedToBuy: () -> Unit = {},
    onStartShopping: () -> Unit
) {
    val cartItems = storeViewModel.cartItems
    val subtotal = storeViewModel.cartSubtotal
    val totalItems = storeViewModel.cartItemCount

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    shadowElevation = 12.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.txt_total_items, totalItems),
                                fontSize = 14.sp,
                                color = ClicksMuted
                            )
                            Text(
                                text = "$ " + "%.2f".format(subtotal),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClicksText
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = onProceedToBuy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ClicksGreen
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.txt_proceed_to_buy),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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

                Column {
                    Text(
                        text = stringResource(R.string.txt_shopping_cart),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Text(
                        text = stringResource(R.string.txt_items_in_cart, totalItems),
                        fontSize = 12.sp,
                        color = ClicksMuted
                    )
                }
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            storeViewModel.cartPersistenceError?.let { error ->
                Text(
                    text = error,
                    color = Error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            // Subtotal Banner
            if (cartItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceLight)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.txt_subtotal),
                            fontSize = 15.sp,
                            color = ClicksMuted
                        )
                        Text(
                            text = "$ " + "%.2f".format(subtotal),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(R.string.txt_eligible),
                            tint = ClicksGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.txt_free_delivery_eligible),
                            fontSize = 13.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                HorizontalDivider(color = BorderColor, thickness = 1.dp)
            }

            // Cart Items List
            if (storeViewModel.isCartLoading && cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ClicksGreen)
                }
            } else if (cartItems.isEmpty()) {
                com.clicks.app.lib.components.EmptyCartGraphic(
                    onStartShopping = onStartShopping
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onIncrement = { storeViewModel.updateQuantity(cartItem.product.id, 1) },
                            onDecrement = { storeViewModel.updateQuantity(cartItem.product.id, -1) },
                            onRemove = { storeViewModel.removeFromCart(cartItem.product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    val product = cartItem.product

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, SurfaceLightAlt, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ClicksMint)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$ " + "%.2f".format(product.price),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksGreen
            )
            Spacer(Modifier.height(4.dp))
            if (product.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(R.string.txt_rating),
                        tint = StarGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${product.rating} (${product.reviewCount} Reviews)",
                        fontSize = 11.sp,
                        color = ClicksMuted
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Quantity controls & Remove
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ClicksGreen)
                            .clickable { onDecrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = stringResource(R.string.txt_decrease),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${cartItem.quantity}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ClicksGreen)
                            .clickable(enabled = cartItem.quantity < MAX_CART_QUANTITY) {
                                onIncrement()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.txt_increase),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.clickable { onRemove() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(R.string.txt_remove),
                        tint = Error,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = stringResource(R.string.txt_remove),
                        fontSize = 12.sp,
                        color = Error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
