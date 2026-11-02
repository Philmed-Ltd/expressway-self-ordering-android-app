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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel,
    onCodeSent: (String, String, String, String) -> Unit, // passes countryCode, phoneNumber, verificationCodeId, authMode
    onBackToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState
    var authMode by rememberSaveable { mutableStateOf("LOGIN") } // "LOGIN" or "REGISTER"
    var loginMethod by rememberSaveable { mutableStateOf("PHONE") } // "PHONE" or "EMAIL"

    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var emailInput by rememberSaveable { mutableStateOf("") }
    var passwordInput by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
                            contentDescription = "Clicks Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Clicks",
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

                // Mode Tabs (Sign In vs Register)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TabButton(
                        text = "Sign In",
                        selected = authMode == "LOGIN",
                        onClick = { authMode = "LOGIN" },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Register",
                        selected = authMode == "REGISTER",
                        onClick = { authMode = "REGISTER" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Method Tabs if in LOGIN mode (Phone vs Email)
                if (authMode == "LOGIN") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { loginMethod = "PHONE" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (loginMethod == "PHONE") BrandGreen.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = if (loginMethod == "PHONE") BrandGreen else TextMuted
                            )
                        ) {
                            Text("Via Phone", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { loginMethod = "EMAIL" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (loginMethod == "EMAIL") BrandGreen.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = if (loginMethod == "EMAIL") BrandGreen else TextMuted
                            )
                        ) {
                            Text("Via Email", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = if (authMode == "REGISTER") "Enter Your Phone Number" else "Sign In via OTP",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (authMode == "REGISTER") {
                            "We'll send a verification code via SMS to verify your number."
                        } else {
                            if (loginMethod == "PHONE") "Enter your registered phone number & password to receive a login OTP."
                            else "Enter your registered email address & password to receive a login OTP."
                        },
                        fontSize = 13.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Input field: Phone or Email
                if (authMode == "REGISTER" || loginMethod == "PHONE") {
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
                                        text = { Text("${country.flag}  ${country.name} (${country.code})") },
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
                            placeholder = { Text("712345678") },
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
                } else {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("johndoe@example.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreen,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Password input for LOGIN mode
                if (authMode == "LOGIN") {
                    Spacer(modifier = Modifier.height(20.dp))
                    ClicksTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = "Password",
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
                }

                Spacer(modifier = Modifier.height(40.dp))

                ClicksButton(
                    text = if (authMode == "REGISTER") "SEND CODE" else "LOGIN",
                    isLoading = uiState.isLoading,
                    onClick = {
                        val usernameType = if (authMode == "REGISTER" || loginMethod == "PHONE") "PHONE_NUMBER" else "EMAIL"
                        val identifier = if (usernameType == "PHONE_NUMBER") "${selectedCountry.code}$phoneNumber" else emailInput

                        if (authMode == "REGISTER") {
                            viewModel.sendVerificationCode(
                                usernameType = usernameType,
                                identifier = identifier,
                                verificationCodeUse = "REGISTRATION",
                                onCodeSent = { codeId ->
                                    onCodeSent(selectedCountry.code, phoneNumber, codeId, "REGISTER")
                                }
                            )
                        } else {
                            viewModel.startLogin(
                                usernameType = usernameType,
                                identifier = identifier,
                                password = passwordInput,
                                onCodeSent = { codeId ->
                                    onCodeSent(selectedCountry.code, phoneNumber.ifBlank { emailInput }, codeId, "LOGIN")
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) TextDark else TextMuted
            )
        }
    }
}
