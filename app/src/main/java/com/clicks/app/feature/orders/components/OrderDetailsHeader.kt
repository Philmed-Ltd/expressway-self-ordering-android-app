package com.clicks.app.feature.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.clicks.app.feature.orders.model.OrderItem
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.InfoBg
import com.clicks.app.lib.theme.InfoText
import com.clicks.app.lib.theme.Warning
import com.clicks.app.lib.theme.WarningBg

@Composable
internal fun OrderDetailsHeader(order: OrderItem, onBackClick: () -> Unit) {
    val statusColor = orderStatusColors(order.status)
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.95f)) {
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
private fun orderStatusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "delivered" -> ClicksMint to ClicksGreen
    "processing" -> WarningBg to Warning
    else -> InfoBg to InfoText
}
