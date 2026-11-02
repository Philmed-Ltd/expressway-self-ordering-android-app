package com.clicks.app.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.clicks.app.feature.home.components.*
import com.clicks.app.feature.menu.model.sampleProducts
import com.clicks.app.feature.menu.viewmodel.StoreViewModel
import com.clicks.app.feature.profile.ProfileViewModel
import com.clicks.app.lib.components.ClicksBottomBar
import com.clicks.app.lib.components.ClicksDrawerSheet
import com.clicks.app.lib.components.SearchBar
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    storeViewModel: StoreViewModel,
    profileViewModel: ProfileViewModel,
    onFavoritesClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Home") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showNotificationsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val unreadNotifications = storeViewModel.unreadNotificationCount

    if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    TextButton(
                        onClick = { storeViewModel.markAllNotificationsRead() }
                    ) {
                        Text(
                            text = "Mark all as read",
                            fontSize = 13.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                storeViewModel.notifications.forEach { notification ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notification.isRead) Color(0xFFF9FAFB) else ClicksMint
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (notification.isRead) Color(0xFFE5E7EB) else ClicksGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = if (notification.isRead) ClicksMuted else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notification.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClicksText
                                    )
                                    Text(
                                        text = notification.time,
                                        fontSize = 11.sp,
                                        color = ClicksMuted
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = notification.message,
                                    fontSize = 12.sp,
                                    color = ClicksMuted,
                                    lineHeight = 16.sp
                                )
                            }

                            if (!notification.isRead) {
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ClicksDrawerSheet(
                userName = profileViewModel.uiState.customer?.name ?: "Customer",
                userEmail = profileViewModel.uiState.customer?.phoneNumber ?: "",
                userInitials = profileViewModel.uiState.customer?.name?.take(2)?.uppercase() ?: "CU",
                isLoading = profileViewModel.uiState.isLoading,
                errorMessage = profileViewModel.uiState.errorMessage,
                onRetryLoad = profileViewModel::loadProfile,
                onProfileClick = onProfileClick,
                onOrdersClick = onOrdersClick,
                onFavoritesClick = onFavoritesClick,
                onCartClick = onCartClick,
                onLogoutClick = onLogoutClick,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                ClicksBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        when (tab) {
                            "Favorites" -> onFavoritesClick()
                            "Cart" -> onCartClick()
                            "Profile" -> onProfileClick()
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                // Header
                HomeHeader(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchToggle = {
                        isSearchOpen = !isSearchOpen
                        if (!isSearchOpen) searchText = ""
                    },
                    onNotificationClick = { showNotificationsSheet = true },
                    unreadNotificationCount = unreadNotifications
                )

                AnimatedVisibility(visible = isSearchOpen) {
                    SearchBar(
                        query = searchText,
                        onQueryChange = { searchText = it },
                        placeholder = "Search products..."
                    )
                }

                Spacer(Modifier.height(14.dp))

                SectionHeading(
                    title = "Categories",
                    action = "View all",
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                HomeCategoriesSection(
                    onCategoryClick = onCategoryClick
                )

                Spacer(Modifier.height(14.dp))

                SectionHeading(
                    title = "Combo Deals",
                    action = "View all",
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                HomePromoCarousel(
                    onDiscoverClick = onDiscoverClick
                )

                Spacer(Modifier.height(28.dp))

                SectionHeading(
                    title = "Popular",
                    action = "View all",
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                HomePopularSection(
                    products = sampleProducts.take(4),
                    storeViewModel = storeViewModel,
                    onProductClick = onProductClick
                )

                Spacer(Modifier.height(22.dp))
            }
        }
    }
}
