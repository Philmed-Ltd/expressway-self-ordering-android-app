package com.clicks.app.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.R
import com.clicks.app.feature.auth.viewmodel.PhoneAuthViewModel
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.components.ClicksTextField
import com.clicks.app.lib.components.ToastBanner
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.BrandGreen
import com.clicks.app.lib.theme.TextDark
import com.clicks.app.lib.theme.TextMuted
import com.clicks.app.lib.theme.WarmPeachBg

private enum class VerificationMode {
    REGISTER,
    LOGIN
}

@Composable
fun PhoneVerificationScreen(
    viewModel: PhoneAuthViewModel,
    verificationCodeId: String,
    countryCode: String,
    phoneNumber: String,
    authMode: String, // "REGISTER" or "LOGIN"
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState
    val isRegistration = authMode == VerificationMode.REGISTER.name
    var code by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image2),
                            contentDescription = stringResource(R.string.txt_clicks),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.txt_clicks),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(3.dp)
                            .background(WarmPeachBg, RoundedCornerShape(1.5.dp))
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(
                            if (isRegistration) R.string.txt_verify_complete_registration
                            else R.string.txt_enter_login_otp
                        ),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isRegistration) {
                            stringResource(
                                R.string.txt_register_otp_explanation,
                                countryCode,
                                phoneNumber
                            )
                        } else {
                            stringResource(R.string.txt_login_otp_explanation, phoneNumber)
                        },
                        fontSize = 13.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5-Digit Code Field
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.filter(Char::isDigit).take(5) },
                    label = { Text(stringResource(R.string.txt_verification_code)) },
                    placeholder = { Text(stringResource(R.string.txt_otp_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isRegistration) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // First Name & Last Name Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClicksTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = stringResource(R.string.txt_first_name),
                            placeholder = stringResource(R.string.txt_first_name_placeholder),
                            modifier = Modifier.weight(1f),
                            labelFontSize = 13
                        )

                        ClicksTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = stringResource(R.string.txt_last_name),
                            placeholder = stringResource(R.string.txt_last_name_placeholder),
                            modifier = Modifier.weight(1f),
                            labelFontSize = 13
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password Field
                    ClicksTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = stringResource(R.string.txt_password_min_length),
                        placeholder = stringResource(R.string.txt_password_placeholder)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                ClicksButton(
                    text = stringResource(
                        if (isRegistration) R.string.txt_register_action
                        else R.string.txt_verify_sign_in
                    ),
                    isLoading = uiState.isLoading,
                    onClick = {
                        if (isRegistration) {
                            viewModel.verifyAndRegister(
                                verificationCodeId = verificationCodeId,
                                countryCode = countryCode,
                                isoCode = "KE",
                                phoneNumber = "$countryCode$phoneNumber",
                                code = code,
                                firstName = firstName,
                                lastName = lastName,
                                password = password,
                                onSuccess = onRegisterSuccess
                            )
                        } else {
                            viewModel.completeLogin(
                                verificationCodeId = verificationCodeId,
                                code = code,
                                onSuccess = onRegisterSuccess
                            )
                        }
                    }
                )
            }
        }
    }
}
