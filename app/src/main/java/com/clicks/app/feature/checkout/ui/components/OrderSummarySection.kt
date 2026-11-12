package com.clicks.app.feature.checkout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.semantics.Role
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
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var promoCode by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    role = Role.Button,
                    onClick = { onExpandedChange(!expanded) }
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.txt_order_summary),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!expanded) {
                    Text(
                        text = "KSh " + "%.2f".format(total),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = stringResource(
                        if (expanded) R.string.txt_collapse_order_summary
                        else R.string.txt_expand_order_summary
                    ),
                    tint = ClicksMuted
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryRow(stringResource(R.string.txt_sub_total), "KSh " + "%.2f".format(subtotal))
                    SummaryRow(stringResource(R.string.txt_vat), "KSh " + "%.2f".format(vat))
                    SummaryRow(stringResource(R.string.txt_shipping_fee), "KSh " + "%.2f".format(shippingFee))
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)
                    SummaryRow(stringResource(R.string.txt_total_amount), "KSh " + "%.2f".format(total), isBold = true)
                }

                Spacer(Modifier.height(20.dp))

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
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
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
