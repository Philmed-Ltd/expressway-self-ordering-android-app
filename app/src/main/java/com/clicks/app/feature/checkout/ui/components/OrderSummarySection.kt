package com.clicks.app.feature.checkout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.R
import com.clicks.app.lib.theme.*

@Composable
fun OrderSummarySection(
    subtotal: Double,
    vat: Double,
    shippingFee: Double,
    total: Double,
    modifier: Modifier = Modifier
) {
    var promoCode by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.txt_order_summary),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ClicksText
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryRow(stringResource(R.string.txt_sub_total), "$ " + "%.2f".format(subtotal))
            SummaryRow(stringResource(R.string.txt_vat), "$ " + "%.2f".format(vat))
            SummaryRow(stringResource(R.string.txt_shipping_fee), "$ " + "%.2f".format(shippingFee))
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            SummaryRow(stringResource(R.string.txt_total_amount), "$ " + "%.2f".format(total), isBold = true)
        }

        Spacer(Modifier.height(20.dp))

        // Promo Code Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promoCode,
                onValueChange = { promoCode = it },
                placeholder = { Text(stringResource(R.string.txt_enter_promo_code), fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ClicksText,
                    unfocusedTextColor = ClicksText,
                    focusedBorderColor = ClicksGreen,
                    unfocusedBorderColor = BorderColor
                )
            )

            ClicksButton(
                text = stringResource(R.string.txt_add),
                onClick = { /* Apply promo */ },
                modifier = Modifier.width(90.dp),
                height = 56.dp,
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) ClicksText else ClicksMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = ClicksText
        )
    }
}
