package com.clicks.app.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.feature.auth.viewmodel.ForgotPasswordViewModel
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.components.ToastBanner
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.BrandGreen
import com.clicks.app.lib.theme.TextDark
import com.clicks.app.lib.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPhoneScreen(
    viewModel: ForgotPasswordViewModel,
    onOtpSent: (String, String) -> Unit, // passes phoneNumber, verificationCodeId
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(commonCountryCodes.first()) }
    var expanded by remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        uiState.errorMessage?.let { error ->
            ToastBanner(message = error, isError = true, onDismiss = viewModel::clearMessages)
        }
        uiState.successMessage?.let { success ->
            ToastBanner(message = success, isError = false, onDismiss = viewModel::clearMessages)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            // Top Bar with Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.txt_back),
                        tint = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 })
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.txt_reset_password),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.txt_registered_phone_otp_explanation),
                        fontSize = 14.sp,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.width(130.dp)
                        ) {
                            OutlinedTextField(
                                value = "${selectedCountry.flag} ${selectedCountry.code}",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    unfocusedBorderColor = BorderColor,
                                    focusedTextColor = TextDark,
                                    unfocusedTextColor = TextDark
                                ),
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                commonCountryCodes.forEach { country ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                stringResource(
                                                    R.string.txt_country_picker_item,
                                                    country.flag,
                                                    stringResource(country.nameRes),
                                                    country.code
                                                )
                                            )
                                        },
                                        onClick = {
                                            selectedCountry = country
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it.filter(Char::isDigit) },
                            placeholder = { Text(stringResource(R.string.txt_phone_placeholder)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGreen,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    ClicksButton(
                        text = stringResource(R.string.txt_send_otp),
                        isLoading = uiState.isLoading,
                        onClick = {
                            val fullPhone = "${selectedCountry.code}$phoneNumber"
                            viewModel.requestOtp(
                                phoneNumber = fullPhone,
                                onCodeSent = { codeId ->
                                    onOtpSent(fullPhone, codeId)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
