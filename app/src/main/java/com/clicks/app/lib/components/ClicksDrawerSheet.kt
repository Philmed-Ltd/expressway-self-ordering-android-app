package com.clicks.app.lib.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clicks.app.lib.theme.*
import com.clicks.app.R

@Composable
fun ClicksDrawerSheet(
    userName: String,
    userEmail: String,
    userInitials: String,
    isLoading: Boolean,
    errorMessage: String?,
    onRetryLoad: () -> Unit,
    onProfileClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onCartClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // User Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ClicksMint),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCloseDrawer()
                        onProfileClick()
                    }
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ClicksGreen, modifier = Modifier.size(24.dp))
                        }
                    }

                    !errorMessage.isNullOrBlank() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                fontSize = 12.sp,
                                color = Error
                            )
                            TextButton(onClick = onRetryLoad) {
                                Text(stringResource(R.string.txt_retry), color = ClicksGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ClicksGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userInitials,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClicksText,
                                    maxLines = 1
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = userEmail,
                                    fontSize = 12.sp,
                                    color = ClicksMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Drawer Menu Options
            DrawerItem(
                icon = Icons.Outlined.PersonOutline,
                title = stringResource(R.string.txt_my_profile),
                onClick = { onCloseDrawer(); onProfileClick() }
            )

            DrawerItem(
                icon = Icons.Outlined.ShoppingBag,
                title = stringResource(R.string.txt_my_orders),
                onClick = { onCloseDrawer(); onOrdersClick() }
            )

            DrawerItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = stringResource(R.string.txt_favorites),
                onClick = { onCloseDrawer(); onFavoritesClick() }
            )

            DrawerItem(
                icon = Icons.Outlined.ShoppingCart,
                title = stringResource(R.string.txt_cart),
                onClick = { onCloseDrawer(); onCartClick() }
            )

            Spacer(Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = {
                    onCloseDrawer()
                    onLogoutClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error.copy(alpha = 0.1f),
                    contentColor = Error
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = stringResource(R.string.txt_log_out),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.txt_log_out),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ClicksGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ClicksText,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = ClicksMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
