package com.clicks.app.feature.profile

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import com.clicks.app.lib.theme.Error
import com.clicks.app.lib.theme.SurfaceLight

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val customer = uiState.customer

    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailAlertsEnabled by remember { mutableStateOf(true) }
    val addresses = remember {
        mutableStateListOf(
            "Home: 925 S Chugach St #APT 10, Alaska",
            "Work: 450 Financial Center, Suite 300"
        )
    }
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var newAddressInput by remember { mutableStateOf("") }

    var showAccountInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
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
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ClicksText
                    )
                }
                Text(
                    text = "My Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = ClicksText
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // User Info Header Card: loaded / loading / error
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ClicksMint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ClicksGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer?.name?.take(2)?.uppercase() ?: "CU",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer?.name ?: "Customer",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = customer?.phoneNumber ?: "",
                            fontSize = 13.sp,
                            color = ClicksMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Section: Orders
            ProfileSectionTitle("Orders & Purchases")
            Spacer(Modifier.height(8.dp))
            ProfileMenuItem(
                icon = Icons.Outlined.ShoppingBag,
                title = "My Orders",
                subtitle = "View active and past orders"
            ) { onOrdersClick() }

            Spacer(Modifier.height(20.dp))

            // Section: Address Book
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileSectionTitle("Address Book")
                Text(
                    text = "+ Add New",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksGreen,
                    modifier = Modifier.clickable { showAddAddressDialog = true }
                )
            }
            Spacer(Modifier.height(8.dp))
            addresses.forEach { address ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Address",
                            tint = ClicksGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = address,
                            fontSize = 13.sp,
                            color = ClicksText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Section: Personal Details
            ProfileSectionTitle("Personal Details")
            Spacer(Modifier.height(8.dp))
            ProfileMenuItem(
                icon = Icons.Outlined.PersonOutline,
                title = "Account Information",
                subtitle = "View your name and phone number"
            ) {
                showAccountInfoDialog = true
            }
            ProfileMenuItem(
                icon = Icons.Outlined.Lock,
                title = "Security & Password",
                subtitle = "Change password, 2FA"
            ) { }

            Spacer(Modifier.height(20.dp))

            // Section: Preferences & Notifications
            ProfileSectionTitle("Preferences")
            Spacer(Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Push",
                                tint = ClicksGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Push Notifications",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ClicksText
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ClicksGreen)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Email",
                                tint = ClicksGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Email Alerts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ClicksText
                            )
                        }
                        Switch(
                            checked = emailAlertsEnabled,
                            onCheckedChange = { emailAlertsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ClicksGreen)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Section: Help & Support
            ProfileSectionTitle("Support")
            Spacer(Modifier.height(8.dp))
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = "FAQs & Help Center",
                subtitle = "Find answers to common questions"
            ) { }

            Spacer(Modifier.height(32.dp))

            // Log Out Button
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error.copy(alpha = 0.1f),
                    contentColor = Error
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Log out",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add New Address", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newAddressInput,
                    onValueChange = { newAddressInput = it },
                    placeholder = { Text("e.g., Office: 123 Main St") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddressInput.isNotBlank()) {
                            addresses.add(newAddressInput.trim())
                            newAddressInput = ""
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClicksGreen)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel", color = ClicksMuted)
                }
            }
        )
    }

    // Account Information Dialog
    if (showAccountInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAccountInfoDialog = false },
            title = { Text("Account Information", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccountInfoRow(label = "Full Name", value = customer?.name ?: "Customer")
                    AccountInfoRow(label = "Phone Number", value = customer?.phoneNumber ?: "")
                    AccountInfoRow(label = "Status", value = customer?.userStatus ?: "ACTIVE")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAccountInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ClicksGreen)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun AccountInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = ClicksMuted
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = ClicksText
        )
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = ClicksText
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ClicksMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ClicksGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClicksText
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = ClicksMuted
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Navigate",
                tint = ClicksMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
