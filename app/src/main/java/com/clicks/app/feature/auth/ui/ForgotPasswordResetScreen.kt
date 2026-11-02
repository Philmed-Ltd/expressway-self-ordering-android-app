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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.feature.auth.viewmodel.ForgotPasswordViewModel
import com.clicks.app.lib.components.ClicksButton
import com.clicks.app.lib.components.ClicksTextField
import com.clicks.app.lib.components.ToastBanner
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.BrandGreen
import com.clicks.app.lib.theme.TextDark
import com.clicks.app.lib.theme.TextMuted

@Composable
fun ForgotPasswordResetScreen(
    viewModel: ForgotPasswordViewModel,
    phoneNumber: String,
    verificationCodeId: String,
    onResetSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState
    var otp by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
                        contentDescription = "Back",
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
                        text = "Create New Password",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter the 5-digit OTP sent to $phoneNumber and your new secure password.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it.filter(Char::isDigit).take(5) },
                        placeholder = { Text("12345") },
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

                    Spacer(modifier = Modifier.height(20.dp))

                    ClicksTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = "New Password",
                        placeholder = "••••••••",
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = BrandGreen
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    ClicksButton(
                        text = "RESET PASSWORD",
                        isLoading = uiState.isLoading,
                        onClick = {
                            viewModel.resetPassword(
                                verificationCodeId = verificationCodeId,
                                otp = otp,
                                newPassword = newPassword,
                                onSuccess = onResetSuccess
                            )
                        }
                    )
                }
            }
        }
    }
}
