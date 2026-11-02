package com.clicks.app.feature.checkout.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.SurfaceLight
import com.clicks.app.R

@Composable
fun MpesaPaymentSection(
    mpesaPhone: String,
    isPaymentConfirmed: Boolean,
    transactionCode: String,
    total: Double,
    onEditPhone: () -> Unit,
    onPayClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.txt_payment),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            if (isPaymentConfirmed) {
                Surface(
                    shape = CircleShape,
                    color = ClicksGreen.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = stringResource(R.string.txt_paid),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPaymentConfirmed) ClicksGreen.copy(alpha = 0.08f) else SurfaceLight
            ),
            border = if (isPaymentConfirmed) BorderStroke(1.5.dp, ClicksGreen) else BorderStroke(1.dp, BorderColor),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ClicksGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhoneAndroid,
                                contentDescription = stringResource(R.string.txt_mpesa),
                                tint = ClicksGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.txt_mpesa),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClicksText
                            )
                            Text(
                                text = mpesaPhone,
                                fontSize = 13.sp,
                                color = ClicksMuted
                            )
                        }
                    }

                    TextButton(onClick = onEditPhone) {
                        Text(
                            text = stringResource(R.string.txt_edit),
                            fontSize = 13.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isPaymentConfirmed) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = ClicksGreen.copy(alpha = 0.2f), thickness = 1.dp)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.txt_transaction_reference, transactionCode),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksGreen
                        )
                        Text(
                            text = stringResource(R.string.txt_confirmed),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksGreen
                        )
                    }
                } else {
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = onPayClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClicksGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.txt_pay_via_mpesa, total),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
