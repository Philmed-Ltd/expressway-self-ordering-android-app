package com.clicks.app.feature.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import androidx.compose.ui.res.stringResource

@Composable
internal fun TrackingCard(status: String) {
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

    OrderDetailsCard {
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
