package com.clicks.app.feature.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.R
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText

@Composable
internal fun ItemsCard(order: OrderItem) {
    OrderDetailsCard {
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
                text = "KSh " + "%.2f".format(order.total),
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
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ClicksMint)
                    ) {
                        AsyncImage(
                            model = item.product.imageUrl,
                            contentDescription = item.product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
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
                            text = "KSh " + "%.2f".format(item.product.price) +
                                "  ·  " + stringResource(R.string.txt_order_item_quantity, item.quantity),
                            color = ClicksMuted,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "KSh " + "%.2f".format(item.product.price * item.quantity),
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
                text = "KSh " + "%.2f".format(order.total),
                color = ClicksText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
