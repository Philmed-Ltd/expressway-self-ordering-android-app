package com.clicks.app.feature.checkout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.R

@Composable
fun EditPhoneDialog(
    initialPhone: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var tempPhone by remember { mutableStateOf(initialPhone) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.txt_edit_mpesa_number),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ClicksText
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.txt_enter_mpesa_phone),
                    fontSize = 14.sp,
                    color = ClicksMuted
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = tempPhone,
                    onValueChange = { newValue -> tempPhone = newValue },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ClicksText,
                        unfocusedTextColor = ClicksText,
                        focusedBorderColor = ClicksGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (tempPhone.isNotBlank()) {
                        onSave(tempPhone)
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.txt_save), fontWeight = FontWeight.Bold, color = ClicksGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.txt_cancel), color = ClicksMuted)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun ProcessingPaymentDialog(
    mpesaPhone: String,
    total: Double
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = ClicksGreen, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.txt_mpesa_stk_sent),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.txt_complete_mpesa_payment, mpesaPhone, total),
                    fontSize = 14.sp,
                    color = ClicksMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SuccessOrderDialog(
    transactionCode: String,
    onTrackOrder: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(ClicksGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = stringResource(R.string.txt_success),
                        tint = ClicksGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.txt_congratulations),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.txt_order_placed_success),
                    fontSize = 15.sp,
                    color = ClicksMuted,
                    textAlign = TextAlign.Center
                )

                if (transactionCode.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.txt_mpesa_reference, transactionCode),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClicksGreen
                    )
                }

                Spacer(Modifier.height(24.dp))

                ClicksButton(
                    text = stringResource(R.string.txt_track_order),
                    onClick = onTrackOrder,
                    height = 50.dp,
                    shape = RoundedCornerShape(25.dp)
                )
            }
        }
    }
}
