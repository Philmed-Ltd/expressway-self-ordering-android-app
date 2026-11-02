package com.clicks.app.feature.checkout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clicks.app.feature.checkout.ui.components.*
import com.clicks.app.R
import com.clicks.app.feature.location.ui.DeliveryLocationViewModel
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.feature.menu.viewmodel.StoreViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CheckoutScreen(
    deliveryLocationViewModel: DeliveryLocationViewModel,
    onPickLocation: () -> Unit = {},
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onOrderPlaced: () -> Unit = {}
) {
    val subtotal = storeViewModel.cartSubtotal
    val shippingFee = if (subtotal > 0) 80.0 else 0.0
    val vat = 0.0
    val total = subtotal + shippingFee + vat

    var mpesaPhone by remember { mutableStateOf("+254 712 345 678") }
    var showEditPhoneDialog by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var isPaymentConfirmed by remember { mutableStateOf(false) }
    var transactionCode by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isProcessingPayment) {
        if (isProcessingPayment) {
            delay(2500.milliseconds)
            transactionCode = "MP${(10000000..99999999).random()}"
            isPaymentConfirmed = true
            isProcessingPayment = false
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    val isLocationSet = deliveryLocationViewModel.location != null
                    val canPlaceOrder = isLocationSet && isPaymentConfirmed

                    ClicksButton(
                        text = stringResource(
                            if (!isLocationSet) R.string.txt_select_delivery_location else R.string.txt_place_order
                        ),
                        onClick = { showSuccessDialog = true },
                        enabled = canPlaceOrder,
                        height = 56.dp,
                        shape = RoundedCornerShape(28.dp),
                        containerColor = ClicksGreen,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = ClicksMuted
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            CheckoutTopBar(onBackClick = onBackClick)

            Spacer(Modifier.height(16.dp))

            DeliveryLocationPreview(
                location = deliveryLocationViewModel.location,
                onClick = onPickLocation
            )

            Spacer(Modifier.height(24.dp))

            MpesaPaymentSection(
                mpesaPhone = mpesaPhone,
                isPaymentConfirmed = isPaymentConfirmed,
                transactionCode = transactionCode,
                total = total,
                onEditPhone = { showEditPhoneDialog = true },
                onPayClicked = { isProcessingPayment = true }
            )

            Spacer(Modifier.height(28.dp))

            OrderSummarySection(
                subtotal = subtotal,
                vat = vat,
                shippingFee = shippingFee,
                total = total
            )

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showEditPhoneDialog) {
        EditPhoneDialog(
            initialPhone = mpesaPhone,
            onDismiss = { showEditPhoneDialog = false },
            onSave = { newPhone -> mpesaPhone = newPhone }
        )
    }

    if (isProcessingPayment) {
        ProcessingPaymentDialog(
            mpesaPhone = mpesaPhone,
            total = total
        )
    }

    if (showSuccessDialog) {
        SuccessOrderDialog(
            transactionCode = transactionCode,
            onTrackOrder = {
                showSuccessDialog = false
                storeViewModel.placeOrder()
                onOrderPlaced()
            }
        )
    }
}
