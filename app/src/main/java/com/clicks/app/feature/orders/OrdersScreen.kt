package com.clicks.app.feature.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalShipping
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
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.InfoBg
import com.clicks.app.lib.theme.InfoText
import com.clicks.app.lib.theme.SurfaceLight
import com.clicks.app.lib.theme.Warning
import com.clicks.app.lib.theme.WarningBg
import com.clicks.app.feature.menu.viewmodel.StoreViewModel

@Composable
fun OrdersScreen(
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {}
) {
    val orders = storeViewModel.orders

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
                    text = stringResource(R.string.txt_my_orders_count, orders.size),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = stringResource(R.string.txt_empty),
                            tint = ClicksMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.txt_no_orders_placed),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.txt_completed_orders_appear),
                            fontSize = 14.sp,
                            color = ClicksMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(orders, key = { it.orderId }) { order ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.orderId,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClicksText
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = when (order.status) {
                                            "Delivered" -> ClicksMint
                                            "Processing" -> WarningBg
                                            else -> InfoBg
                                        }
                                    ) {
                                        Text(
                                            text = when (order.status) {
                                                "Delivered" -> stringResource(R.string.txt_order_delivered)
                                                "Processing" -> stringResource(R.string.txt_order_processing)
                                                else -> order.status
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (order.status) {
                                                "Delivered" -> ClicksGreen
                                                "Processing" -> Warning
                                                else -> InfoText
                                            },
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = stringResource(R.string.txt_order_date, order.date),
                                    fontSize = 13.sp,
                                    color = ClicksMuted
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = stringResource(R.string.txt_order_items, order.itemsSummary),
                                    fontSize = 13.sp,
                                    color = ClicksText,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(Modifier.height(12.dp))

                                HorizontalDivider(color = BorderColor, thickness = 1.dp)

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.txt_total_amount),
                                        fontSize = 13.sp,
                                        color = ClicksMuted
                                    )
                                    Text(
                                        text = "$ " + "%.2f".format(order.total),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClicksGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
