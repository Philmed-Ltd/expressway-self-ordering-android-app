package com.clicks.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.feature.auth.data.dto.CustomerView
import com.clicks.app.R
import com.clicks.app.lib.theme.AppThemeMode
import com.clicks.app.lib.theme.AppThemePreferences
import com.clicks.app.lib.theme.BorderColor
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.SurfaceLight

private val ProfileColorScheme = lightColorScheme(
    primary = ClicksGreen,
    onPrimary = Color.White,
    primaryContainer = SurfaceLight,
    onPrimaryContainer = ClicksText,
    background = Color.White,
    onBackground = ClicksText,
    surface = Color.White,
    onSurface = ClicksText,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = ClicksMuted,
    outline = BorderColor,
    outlineVariant = BorderColor,
    error = Error,
    onError = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val customer = uiState.customer
    var showingAccountInfo by remember { mutableStateOf(false) }
    var showingThemeOptions by remember { mutableStateOf(false) }
    var showingSettings by remember { mutableStateOf(false) }
    var showingFaq by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = ProfileColorScheme) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (showingAccountInfo) R.string.txt_account_info else R.string.txt_my_profile
                        ),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showingAccountInfo) showingAccountInfo = false else onBackClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.txt_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ClicksGreen)
                }
            }

            !uiState.errorMessage.isNullOrBlank() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.txt_profile_load_error, uiState.errorMessage.orEmpty()),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = viewModel::loadProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = ClicksGreen)
                    ) {
                        Text(stringResource(R.string.txt_retry))
                    }
                }
            }

            showingAccountInfo -> {
                AccountInformation(
                    customer = customer,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                ProfileOverview(
                    customer = customer,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onAccountClick = { showingAccountInfo = true },
                    onAddressClick = onAddressClick,
                    onOrdersClick = onOrdersClick,
                    onSettingsClick = { showingSettings = true },
                    onThemeClick = { showingThemeOptions = true },
                    onFaqClick = { showingFaq = true },
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }

    if (showingThemeOptions) {
        AlertDialog(
            onDismissRequest = { showingThemeOptions = false },
            title = { Text(stringResource(R.string.txt_theme)) },
            text = {
                Column {
                    AppThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    AppThemePreferences.updateMode(mode)
                                    showingThemeOptions = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = AppThemePreferences.mode == mode,
                                onClick = {
                                    AppThemePreferences.updateMode(mode)
                                    showingThemeOptions = false
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(mode.labelRes))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showingThemeOptions = false }) {
                    Text(stringResource(R.string.txt_close))
                }
            }
        )
    }

    if (showingSettings) {
        AlertDialog(
            onDismissRequest = { showingSettings = false },
            title = { Text(stringResource(R.string.txt_settings)) },
            text = {
                Text(
                    stringResource(R.string.txt_profile_read_only_settings)
                )
            },
            confirmButton = {
                TextButton(onClick = { showingSettings = false }) {
                    Text(stringResource(R.string.txt_done))
                }
            }
        )
    }

    if (showingFaq) {
        AlertDialog(
            onDismissRequest = { showingFaq = false },
            title = { Text(stringResource(R.string.txt_faq_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FaqItem(
                        stringResource(R.string.txt_faq_track_order_question),
                        stringResource(R.string.txt_faq_track_order_answer)
                    )
                    FaqItem(
                        stringResource(R.string.txt_faq_address_question),
                        stringResource(R.string.txt_faq_address_answer)
                    )
                    FaqItem(
                        stringResource(R.string.txt_faq_theme_question),
                        stringResource(R.string.txt_faq_theme_answer)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showingFaq = false }) {
                    Text(stringResource(R.string.txt_close))
                }
            }
        )
    }
    }
}

@Composable
private fun ProfileOverview(
    customer: CustomerView?,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit,
    onAddressClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onThemeClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ProfileHeader(customer, onAccountClick)

        ProfileSection(title = stringResource(R.string.txt_your_account)) {
            ProfileMenuItem(
                icon = Icons.Outlined.AccountCircle,
                title = stringResource(R.string.txt_account_information),
                subtitle = stringResource(R.string.txt_account_info_subtitle),
                onClick = onAccountClick
            )
            ProfileMenuItem(
                icon = Icons.Outlined.LocationOn,
                title = stringResource(R.string.txt_addresses),
                subtitle = stringResource(R.string.txt_addresses_subtitle),
                onClick = onAddressClick
            )
            ProfileMenuItem(
                icon = Icons.Outlined.LocalShipping,
                title = stringResource(R.string.txt_orders_to_track),
                subtitle = stringResource(R.string.txt_orders_to_track_subtitle),
                onClick = onOrdersClick
            )
        }

        ProfileSection(title = stringResource(R.string.txt_preferences_support)) {
            ProfileMenuItem(
                icon = Icons.Outlined.Settings,
                title = stringResource(R.string.txt_settings),
                subtitle = stringResource(R.string.txt_settings_subtitle),
                onClick = onSettingsClick
            )
            ProfileMenuItem(
                icon = Icons.Outlined.Palette,
                title = stringResource(R.string.txt_theme),
                subtitle = stringResource(AppThemePreferences.mode.labelRes),
                onClick = onThemeClick
            )
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = stringResource(R.string.txt_faq),
                subtitle = stringResource(R.string.txt_faq_subtitle),
                onClick = onFaqClick
            )
        }

        TextButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = Error
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.txt_log_out),
                color = Error,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ProfileHeader(customer: CustomerView?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceLight)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = customer?.name?.take(2)?.uppercase().orEmpty().ifBlank {
                    stringResource(R.string.txt_user_initial)
                },
                color = ClicksGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = customer?.name?.takeIf(String::isNotBlank)
                    ?: stringResource(R.string.txt_your_profile),
                color = ClicksText,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = customer?.email?.takeIf(String::isNotBlank)
                    ?: customer?.phoneNumber.orEmpty().ifBlank {
                        stringResource(R.string.txt_view_account_details)
                    },
                color = ClicksMuted,
                fontSize = 13.sp
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = stringResource(R.string.txt_view_account_information),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .border(1.dp, BorderColor, RoundedCornerShape(18.dp)),
            content = content
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ClicksGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AccountInformation(customer: CustomerView?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(R.string.txt_your_account_details),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        ) {
            AccountDetail(stringResource(R.string.txt_full_name), customer?.name)
            AccountDetail(
                stringResource(R.string.txt_email),
                customer?.email,
                verified = customer?.emailVerified
            )
            AccountDetail(
                stringResource(R.string.txt_phone_number_label),
                formatPhoneNumber(customer?.phoneCode, customer?.phoneNumber),
                verified = customer?.phoneNumberVerified
            )
            AccountDetail(stringResource(R.string.txt_account_status), customer?.userStatus)
        }
    }
}

private fun formatPhoneNumber(phoneCode: String?, phoneNumber: String?): String? {
    val code = phoneCode?.trim().orEmpty()
    val number = phoneNumber?.trim().orEmpty()
    if (number.isBlank()) return code.ifBlank { null }
    if (code.isBlank()) return number

    val codeDigits = code.filter(Char::isDigit)
    val numberDigits = number.filter(Char::isDigit)
    return if (codeDigits.isNotEmpty() && numberDigits.startsWith(codeDigits)) {
        number
    } else {
        "$code $number"
    }
}

@Composable
private fun AccountDetail(label: String, value: String?, verified: Boolean? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value?.takeIf(String::isNotBlank)
                    ?: stringResource(R.string.txt_not_provided),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            verified?.let {
                Text(
                    text = stringResource(if (it) R.string.txt_verified else R.string.txt_not_verified),
                    color = if (it) ClicksGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Column {
        Text(question, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(3.dp))
        Text(answer, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}
