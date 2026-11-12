package com.clicks.app.feature.checkout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clicks.app.feature.checkout.ui.components.*
import com.clicks.app.R
import com.clicks.app.feature.checkout.data.dto.OrderDeliveryAddress
import com.clicks.app.feature.checkout.data.dto.OrderRequest
import com.clicks.app.feature.checkout.data.dto.OrderRequestItem
import com.clicks.app.feature.checkout.viewmodel.CheckoutViewModel
import com.clicks.app.feature.cart.viewmodel.CartViewModel
import com.clicks.app.feature.location.viewmodel.DeliveryLocationViewModel
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.feature.orders.viewmodel.OrdersViewModel
import com.clicks.app.network.ApiResult
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    deliveryLocationViewModel: DeliveryLocationViewModel,
    onPickLocation: () -> Unit = {},
    checkoutViewModel: CheckoutViewModel,
    cartViewModel: CartViewModel,
    ordersViewModel: OrdersViewModel,
    onBackClick: () -> Unit = {},
    onOrderPlaced: (String) -> Unit = {}
) {
    val subtotal = cartViewModel.cartSubtotal
    val displayedShippingFee = 0.0
    val vat = 0.0

    val coroutineScope = rememberCoroutineScope()

    val displayedSubtotal = checkoutViewModel.placedSubtotal ?: subtotal
    val displayedTotal = displayedSubtotal + displayedShippingFee + vat

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            if (checkoutViewModel.placedOrderId == null) Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    val isLocationSet = deliveryLocationViewModel.location != null
                    val canPlaceOrder = !checkoutViewModel.isPlacingOrder &&
                        cartViewModel.cartItems.isNotEmpty()

                    ClicksButton(
                        text = stringResource(
                            when {
                                checkoutViewModel.isPlacingOrder -> R.string.txt_placing_order
                                !isLocationSet -> R.string.txt_select_delivery_location
                                else -> R.string.txt_place_order
                            }
                        ),
                        onClick = {
                            if (!isLocationSet) {
                                onPickLocation()
                            } else {
                                coroutineScope.launch {
                                    val location = deliveryLocationViewModel.location
                                        ?: return@launch
                                    val cartSnapshot = cartViewModel.cartItems.toList()
                                    val request = OrderRequest(
                                        deliveryAddress = OrderDeliveryAddress(
                                            address = location.addressLine,
                                            latitude = location.latitude.toString(),
                                            longitude = location.longitude.toString(),
                                            instructions = ""
                                        ),
                                        items = cartSnapshot.map { cartItem ->
                                            OrderRequestItem(
                                                productItemEntityId = cartItem.product.id,
                                                quantity = cartItem.quantity
                                            )
                                        }
                                    )
                                    val result = checkoutViewModel.placeOrder(request, subtotal)
                                    when (result) {
                                        is ApiResult.Success -> {
                                            ordersViewModel.addPlacedOrderPreview(
                                                orderId = result.data,
                                                productsAndQuantities = cartSnapshot.map {
                                                    it.product to it.quantity
                                                },
                                                total = subtotal,
                                                address = location.addressLine,
                                                latitude = location.latitude.toString(),
                                                longitude = location.longitude.toString()
                                            )
                                            cartViewModel.clearCart()
                                        }
                                        is ApiResult.Error -> Unit
                                    }
                                }
                            }
                        },
                        enabled = canPlaceOrder,
                        height = 56.dp,
                        shape = RoundedCornerShape(28.dp),
                        containerColor = ClicksGreen,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            CheckoutTopBar(onBackClick = onBackClick)

            Spacer(Modifier.height(16.dp))

            DeliveryLocationPreview(
                location = deliveryLocationViewModel.location,
                onClick = onPickLocation
            )

            Spacer(Modifier.height(28.dp))

            OrderSummarySection(
                subtotal = displayedSubtotal,
                vat = vat,
                shippingFee = displayedShippingFee,
                total = displayedTotal,
                expanded = checkoutViewModel.isOrderSummaryExpanded,
                onExpandedChange = { checkoutViewModel.isOrderSummaryExpanded = it }
            )

            checkoutViewModel.orderError?.let { message ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (checkoutViewModel.isPlacingOrder) {
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            checkoutViewModel.placedOrderId?.let {

                Spacer(Modifier.height(20.dp))
                MpesaPaymentSection(
                    mpesaPhone = checkoutViewModel.mpesaPhone,
                    onMpesaPhoneChange = { checkoutViewModel.mpesaPhone = it },
                    isPaymentConfirmed = checkoutViewModel.isPaymentConfirmed,
                    transactionCode = checkoutViewModel.transactionCode,
                    total = displayedTotal,
                    onPhoneFieldFocus = { checkoutViewModel.isOrderSummaryExpanded = false },
                    isProcessing = checkoutViewModel.isProcessingPayment,
                    errorMessage = checkoutViewModel.paymentError,
                    onPayClicked = {
                        if (checkoutViewModel.placedOrderId == null) return@MpesaPaymentSection
                        coroutineScope.launch {
                            checkoutViewModel.initializePayment(displayedTotal)
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (checkoutViewModel.isProcessingPayment) {
        ProcessingPaymentDialog(
            mpesaPhone = checkoutViewModel.mpesaPhone,
            total = displayedTotal
        )
    }

    if (checkoutViewModel.showSuccessDialog) {
        SuccessOrderDialog(
            transactionCode = checkoutViewModel.transactionCode,
            onTrackOrder = {
                checkoutViewModel.showSuccessDialog = false
                checkoutViewModel.placedOrderId?.let(onOrderPlaced)
            }
        )
    }
}
