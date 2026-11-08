package com.clicks.app.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.clicks.app.R
import com.clicks.app.feature.home.components.*
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.menu.viewmodel.StoreViewModel
import com.clicks.app.feature.profile.ProfileViewModel
import com.clicks.app.lib.components.ClicksBottomBar
import com.clicks.app.lib.components.BottomTab
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
    onProductClick: (String) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onOrderNowClick: (String) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        storeViewModel.loadProducts()
        storeViewModel.loadCategories()
    }

    var searchText by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var showSearchResults by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

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
                        text = stringResource(R.string.txt_notifications),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    TextButton(
                        onClick = { storeViewModel.markAllNotificationsRead() }
                    ) {
                        Text(
                            text = stringResource(R.string.txt_mark_all_read),
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
                userName = profileViewModel.uiState.customer?.name ?: stringResource(R.string.txt_customer),
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
                            BottomTab.FAVORITES -> onFavoritesClick()
                            BottomTab.CART -> onCartClick()
                            BottomTab.PROFILE -> onProfileClick()
                            BottomTab.HOME -> Unit
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
                        if (!isSearchOpen) {
                            searchText = ""
                            showSearchResults = false
                        }
                    },
                    onNotificationClick = { showNotificationsSheet = true },
                    unreadNotificationCount = unreadNotifications
                )

                AnimatedVisibility(visible = isSearchOpen) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SearchBar(
                            query = searchText,
                            onQueryChange = {
                                searchText = it
                                showSearchResults = it.isNotBlank()
                            },
                            placeholder = stringResource(R.string.txt_search_products)
                        )

                        val matchingProducts = remember(searchText, storeViewModel.products.size) {
                            if (searchText.isBlank()) {
                                emptyList()
                            } else {
                                storeViewModel.products.filter { product ->
                                    product.matchesSearch(searchText)
                                }
                            }
                        }

                        if (showSearchResults && searchText.isNotBlank()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                shadowElevation = 8.dp,
                                tonalElevation = 2.dp
                            ) {
                                when {
                                    storeViewModel.isProductsLoading && matchingProducts.isEmpty() -> {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = ClicksGreen,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = stringResource(R.string.txt_search_loading),
                                                color = ClicksMuted,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    matchingProducts.isEmpty() -> {
                                        Text(
                                            text = stringResource(R.string.txt_no_search_results),
                                            color = ClicksMuted,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }

                                    else -> {
                                        LazyColumn(
                                            modifier = Modifier.heightIn(max = 360.dp)
                                        ) {
                                            items(
                                                items = matchingProducts.take(7),
                                                key = { it.id }
                                            ) { product ->
                                                ProductSearchResult(
                                                    product = product,
                                                    onClick = {
                                                        showSearchResults = false
                                                        searchText = ""
                                                        isSearchOpen = false
                                                        onProductClick(product.id)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                SectionHeading(
                    title = stringResource(R.string.txt_categories),
                    action = stringResource(R.string.txt_view_all),
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                HomeCategoriesSection(
                    categories = storeViewModel.categories,
                    onCategoryClick = onCategoryClick,
                    isLoading = storeViewModel.isCategoriesLoading,
                    error = storeViewModel.categoriesError
                ) { storeViewModel.loadCategories(force = true) }

                Spacer(Modifier.height(14.dp))

                val featuredProducts = storeViewModel.products.take(5)
                if (featuredProducts.isNotEmpty()) {
                    SectionHeading(
                        title = stringResource(R.string.txt_featured_meals),
                        action = stringResource(R.string.txt_view_all),
                        onActionClick = onViewAllClick,
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    HomePromoCarousel(
                        products = featuredProducts,
                        onOrderNowClick = onOrderNowClick
                    )
                }

                Spacer(Modifier.height(28.dp))

                SectionHeading(
                    title = stringResource(R.string.txt_popular),
                    action = stringResource(R.string.txt_view_all),
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                HomePopularSection(
                    products = storeViewModel.products.take(4),
                    storeViewModel = storeViewModel,
                    onProductClick = onProductClick
                )

                Spacer(Modifier.height(22.dp))
            }

            if (storeViewModel.isHomeOffline) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.txt_no_internet_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.txt_no_internet_message),
                            fontSize = 14.sp,
                            color = ClicksMuted
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                storeViewModel.loadProducts(force = true)
                                storeViewModel.loadCategories(force = true)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ClicksGreen)
                        ) {
                            Text(stringResource(R.string.txt_retry), color = Color.White)
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun ProductSearchResult(
    product: StoreProduct,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ClicksMint)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                color = ClicksText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            if (product.category.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = product.category,
                    color = ClicksMuted,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

private fun StoreProduct.matchesSearch(query: String): Boolean {
    val normalizedQuery = query.trim()
    return name.contains(normalizedQuery, ignoreCase = true) ||
        category.contains(normalizedQuery, ignoreCase = true) ||
        description.contains(normalizedQuery, ignoreCase = true)
}
