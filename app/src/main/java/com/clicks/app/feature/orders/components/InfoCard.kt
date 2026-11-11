package com.clicks.app.feature.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText

@Composable
internal fun InfoCard(
    title: String,
    icon: @Composable () -> Unit,
    detail: String
) {
    OrderDetailsCard {
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
