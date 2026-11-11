package com.clicks.app.feature.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.feature.menu.viewmodel.OrderItem
import com.clicks.app.lib.components.ClicksButton
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

@Composable
fun OrderDetailsScreen(
    order: OrderItem?,
    onBackClick: () -> Unit,
    onReorder: (OrderItem) -> Unit
) {
    Scaffold(
        containerColor = SurfaceLightAlt,
        bottomBar = {
            if (order != null && order.items.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 12.dp
                ) {
                    ClicksButton(
                        text = stringResource(R.string.txt_reorder),
                        onClick = { onReorder(order) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 14.dp),
                        height = 56.dp,
                        shape = RoundedCornerShape(18.dp),
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SurfaceLightAlt),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.txt_order_not_found),
                        color = ClicksMuted
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onBackClick) {
                        Text(stringResource(R.string.txt_back), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SurfaceLightAlt)
            ) {
                OrderDetailsHeader(order = order, onBackClick = onBackClick)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TrackingCard(status = order.status)
                    ItemsCard(order = order)
                    order.deliveryAddress?.let { address ->
                        InfoCard(
                            title = stringResource(R.string.txt_delivery_address),
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            detail = address
                        )
                    }
                    order.paymentReference?.takeIf(String::isNotBlank)?.let { reference ->
                        InfoCard(
                            title = stringResource(R.string.txt_payment_reference),
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            detail = reference
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderDetailsHeader(order: OrderItem, onBackClick: () -> Unit) {
    val statusColor = statusColors(order.status)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(ClicksGreen)
            .statusBarsPadding()
            .padding(start = 18.dp, end = 22.dp, top = 4.dp, bottom = 22.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.txt_back),
                tint = Color.White
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.txt_order_details),
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp
        )
        Spacer(Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = order.orderId,
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f)
            ) {
                Text(
                    text = order.status,
                    color = statusColor.second,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(
            text = stringResource(R.string.txt_order_date, order.date),
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun TrackingCard(status: String) {
    val normalizedStatus = status.lowercase()
    val activeStep = when {
        normalizedStatus.contains("deliver") -> 2
        normalizedStatus.contains("transit") || normalizedStatus.contains("ship") -> 1
        else -> 0
    }
    val steps = listOf(
        stringResource(R.string.txt_order_processing),
        stringResource(R.string.txt_order_on_the_way),
        stringResource(R.string.txt_order_delivered)
    )

    PremiumCard {
        Text(
            text = stringResource(R.string.txt_order_progress),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ClicksText
        )
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            steps.forEachIndexed { index, step ->
                val complete = index <= activeStep
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = when (index) {
                        0 -> Alignment.Start
                        steps.lastIndex -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                thickness = 2.dp,
                                color = if (index <= activeStep) MaterialTheme.colorScheme.primary else BorderColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(if (complete) ClicksGreen else BorderColor)
                        )
                        if (index < steps.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                thickness = 2.dp,
                                color = if (index < activeStep) MaterialTheme.colorScheme.primary else BorderColor
                            )
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        fontWeight = if (complete) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (complete) ClicksText else ClicksMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemsCard(order: OrderItem) {
    PremiumCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.txt_ordered_items),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = stringResource(
                        R.string.txt_order_item_count,
                        order.items.sumOf { it.quantity }
                    ),
                    fontSize = 12.sp,
                    color = ClicksMuted
                )
            }
            Text(
                text = "$ " + "%.2f".format(order.total),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = BorderColor.copy(alpha = 0.75f))
        Spacer(Modifier.height(4.dp))

        if (order.items.isEmpty()) {
            Text(
                text = order.itemsSummary,
                color = ClicksMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            order.items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ClicksMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.quantity.toString().padStart(2, '0'),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.product.name,
                            color = ClicksText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "$ " + "%.2f".format(item.product.price) +
                                "  ·  " + stringResource(R.string.txt_order_item_quantity, item.quantity),
                            color = ClicksMuted,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "$ " + "%.2f".format(item.product.price * item.quantity),
                        color = ClicksText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (index < order.items.lastIndex) {
                    HorizontalDivider(color = BorderColor.copy(alpha = 0.55f))
                }
            }
        }
        HorizontalDivider(color = BorderColor.copy(alpha = 0.75f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.txt_order_total_label),
                color = ClicksMuted,
                fontSize = 13.sp
            )
            Text(
                text = "$ " + "%.2f".format(order.total),
                color = ClicksText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    icon: @Composable () -> Unit,
    detail: String
) {
    PremiumCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ClicksMint),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(Modifier.width(11.dp))
            Text(
                text = title,
                color = ClicksText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = detail,
            color = ClicksMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun PremiumCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            content = content
        )
    }
}

@Composable
private fun statusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "delivered" -> ClicksMint to ClicksGreen
    "processing" -> WarningBg to Warning
    else -> InfoBg to InfoText
}
