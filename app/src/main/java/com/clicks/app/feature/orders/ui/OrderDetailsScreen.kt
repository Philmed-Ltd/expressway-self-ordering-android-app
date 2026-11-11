package com.clicks.app.feature.orders.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.feature.orders.components.InfoCard
import com.clicks.app.feature.orders.components.ItemsCard
import com.clicks.app.feature.orders.components.OrderDetailsHeader
import com.clicks.app.feature.orders.components.TrackingCard
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.SurfaceLightAlt

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
