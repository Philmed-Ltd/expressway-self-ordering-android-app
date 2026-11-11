package com.clicks.app.feature.orders.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.feature.orders.viewmodel.OrdersViewModel
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.InfoBg
import com.clicks.app.lib.theme.InfoText
import com.clicks.app.lib.theme.SurfaceLightAlt
import com.clicks.app.lib.theme.Warning
import com.clicks.app.lib.theme.WarningBg
import coil.compose.AsyncImage

@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel,
    onBackClick: () -> Unit = {},
    onOrderClick: (String) -> Unit = {}
) {
    LaunchedEffect(ordersViewModel) {
        ordersViewModel.loadOrders()
    }

    val orders = ordersViewModel.orders

    Scaffold(containerColor = SurfaceLightAlt) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLightAlt),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OrdersHeader(
                    orderCount = orders.size,
                    onBackClick = onBackClick
                )
            }

            if (ordersViewModel.isOrdersLoading && orders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            ordersViewModel.ordersError?.let { error ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = ordersViewModel::loadOrders) {
                            Text(stringResource(R.string.txt_retry))
                        }
                    }
                }
            }

            if (orders.isEmpty() && !ordersViewModel.isOrdersLoading && ordersViewModel.ordersError == null) {
                item {
                    EmptyOrdersState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            } else if (orders.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.txt_recent_orders),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText,
                        modifier = Modifier.padding(start = 22.dp, top = 4.dp)
                    )
                }
                items(orders, key = { it.orderId }) { order ->
                    OrderCard(
                        order = order,
                        onClick = { onOrderClick(order.orderId) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrdersHeader(orderCount: Int, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(ClicksGreen)
            .statusBarsPadding()
            .padding(start = 18.dp, end = 22.dp, top = 8.dp, bottom = 26.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.txt_back),
                tint = Color.White
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.txt_order_history_eyebrow),
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.3.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.txt_my_orders),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.txt_orders_header_subtitle),
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.sp
                )
            }
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.16f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = orderCount.toString(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColors = statusColors(order.status)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.txt_order_number_label),
                        color = ClicksMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = order.orderId,
                        color = ClicksText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = order.date,
                        color = ClicksMuted,
                        fontSize = 13.sp
                    )
                }
                StatusPill(label = order.status, background = statusColors.first, foreground = statusColors.second)
            }

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceLightAlt)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (order.items.isEmpty()) {
                    Text(
                        text = order.itemsSummary,
                        color = ClicksText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    order.items.take(2).forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.product.imageUrl,
                                contentDescription = item.product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ClicksMint)
                            )
                            Spacer(Modifier.width(11.dp))
                            Text(
                                text = item.product.name,
                                color = ClicksText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "×${item.quantity}",
                                color = ClicksMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (order.items.size > 2) {
                        Text(
                            text = stringResource(R.string.txt_more_order_items, order.items.size - 2),
                            color = ClicksMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.7f))
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.txt_order_total_label),
                        color = ClicksMuted,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "KSh " + "%.2f".format(order.total),
                        color = ClicksText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ClicksGreen.copy(alpha = 0.35f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(start = 14.dp, end = 10.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = stringResource(R.string.txt_view_details),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, background: Color, foreground: Color) {
    Surface(shape = CircleShape, color = background) {
        Text(
            text = label,
            color = foreground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun EmptyOrdersState(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(top = 18.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(ClicksMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = stringResource(R.string.txt_empty),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.txt_no_orders_placed),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = stringResource(R.string.txt_completed_orders_appear),
                fontSize = 14.sp,
                color = ClicksMuted
            )
        }
    }
}

@Composable
private fun statusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "delivered" -> ClicksMint to ClicksGreen
    "processing" -> WarningBg to Warning
    else -> InfoBg to InfoText
}
