package com.clicks.app.navigation

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.clicks.app.lib.session.SessionManager
import com.clicks.app.lib.session.SessionRepository
import com.clicks.app.feature.onboarding.OnboardingContainerScreen
import com.clicks.app.feature.auth.ui.PhoneAuthScreen
import com.clicks.app.feature.auth.viewmodel.PhoneAuthViewModel
import com.clicks.app.feature.auth.ui.PhoneVerificationScreen
import com.clicks.app.feature.auth.ui.ForgotPasswordPhoneScreen
import com.clicks.app.feature.auth.ui.ForgotPasswordResetScreen
import com.clicks.app.feature.auth.viewmodel.ForgotPasswordViewModel
import com.clicks.app.feature.home.ui.HomeScreen
import com.clicks.app.feature.menu.ui.CategoryProductsScreen
import com.clicks.app.feature.menu.ui.ProductDetailsScreen
import com.clicks.app.feature.cart.CartScreen
import com.clicks.app.feature.favorites.FavoritesScreen
import com.clicks.app.feature.checkout.ui.CheckoutScreen
import com.clicks.app.feature.location.ui.DeliveryLocationViewModel
import com.clicks.app.feature.location.ui.LocationPickerScreen
import com.clicks.app.feature.location.ui.LocationPickerViewModel
import com.clicks.app.feature.profile.ProfileScreen
import com.clicks.app.feature.profile.ProfileViewModel
import com.clicks.app.feature.orders.OrdersScreen
import com.clicks.app.feature.orders.OrderDetailsScreen
import com.clicks.app.feature.menu.viewmodel.StoreViewModel
import com.clicks.app.feature.location.ui.DeliveryLocation

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionRepository = remember { SessionRepository(context) }
    val storeViewModel: StoreViewModel = viewModel()
    val deliveryLocationViewModel: DeliveryLocationViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val startDestination = when {
        !sessionRepository.isOnboardingCompleted() -> Routes.Onboarding.route
        sessionRepository.isLoggedIn() -> Routes.Home.route
        else -> Routes.PhoneAuth.route
    }

    val goToAuth = {
        navController.navigate(Routes.PhoneAuth.route) {
            popUpTo(Routes.Onboarding.route) { inclusive = true }
        }
    }

    val performLogout = {
        storeViewModel.clearSession()
        profileViewModel.clearSession()
        sessionRepository.clearSession()
        goToAuth()
    }

    LaunchedEffect(Unit) {
        SessionManager.sessionExpiredEvent.collect {
            performLogout()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { it / 5 } },
        exitTransition = { fadeOut(animationSpec = tween(350)) + slideOutHorizontally(animationSpec = tween(350)) { -it / 5 } },
        popEnterTransition = { fadeIn(animationSpec = tween(350)) + slideInHorizontally(animationSpec = tween(350)) { -it / 5 } },
        popExitTransition = { fadeOut(animationSpec = tween(350)) + slideOutHorizontally(animationSpec = tween(350)) { it / 5 } }
    ) {
        composable(Routes.Onboarding.route) {
            OnboardingContainerScreen(
                onFinished = {
                    sessionRepository.setOnboardingCompleted(true)
                    val nextRoute = if (sessionRepository.isLoggedIn()) Routes.Home.route else Routes.PhoneAuth.route
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.PhoneAuth.route) {
            val phoneAuthViewModel: PhoneAuthViewModel = viewModel()
            PhoneAuthScreen(
                viewModel = phoneAuthViewModel,
                onCodeSent = { countryCode, phoneNumber, codeId, authMode ->
                    navController.navigate(Routes.PhoneVerification.createRoute(countryCode, phoneNumber, codeId, authMode))
                },
                onForgotPasswordClick = {
                    navController.navigate(Routes.ForgotPasswordPhone.route)
                }
            )
        }

        composable(Routes.ForgotPasswordPhone.route) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            ForgotPasswordPhoneScreen(
                viewModel = forgotPasswordViewModel,
                onOtpSent = { phoneNumber, verificationCodeId ->
                    navController.navigate(Routes.ForgotPasswordReset.createRoute(phoneNumber, verificationCodeId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ForgotPasswordReset.route) { backStackEntry ->
            val phoneNumber = Uri.decode(backStackEntry.arguments?.getString("phoneNumber") ?: "")
            val verificationCodeId = Uri.decode(backStackEntry.arguments?.getString("verificationCodeId") ?: "")
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            ForgotPasswordResetScreen(
                viewModel = forgotPasswordViewModel,
                phoneNumber = phoneNumber,
                verificationCodeId = verificationCodeId,
                onResetSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.PhoneAuth.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.PhoneVerification.route) { backStackEntry ->
            val countryCode = Uri.decode(backStackEntry.arguments?.getString("countryCode") ?: "+254")
            val phoneNumber = Uri.decode(backStackEntry.arguments?.getString("phoneNumber") ?: "")
            val verificationCodeId = Uri.decode(backStackEntry.arguments?.getString("verificationCodeId") ?: "")
            val authMode = Uri.decode(backStackEntry.arguments?.getString("authMode") ?: "REGISTER")
            val phoneAuthViewModel: PhoneAuthViewModel = viewModel()
            PhoneVerificationScreen(
                viewModel = phoneAuthViewModel,
                verificationCodeId = verificationCodeId,
                countryCode = countryCode,
                phoneNumber = phoneNumber,
                authMode = authMode,
                onRegisterSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.PhoneAuth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            LaunchedEffect(Unit) { profileViewModel.loadProfile() }
            HomeScreen(
                storeViewModel = storeViewModel,
                profileViewModel = profileViewModel,
                onCategoryClick = { categoryKey ->
                    navController.navigate(Routes.Products.createRoute(categoryKey))
                },
                onViewAllClick = {
                    navController.navigate(Routes.Products.createRoute())
                },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetails.createRoute(productId))
                },
                onOrderNowClick = { productId ->
                    storeViewModel.addToCart(productId)
                    navController.navigate(Routes.Cart.route)
                },
                onFavoritesClick = {
                    navController.navigate(Routes.Favorites.route)
                },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(Routes.Profile.route)
                },
                onOrdersClick = {
                    navController.navigate(Routes.Orders.route)
                },
                onLogoutClick = {
                    performLogout()
                },
            )
        }

        composable(
            route = Routes.Products.route,
            arguments = listOf(
                navArgument("categoryKey") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CategoryProductsScreen(
                categoryKey = backStackEntry.arguments?.getString("categoryKey"),
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetails.createRoute(productId))
                }
            )
        }

        composable(Routes.ProductDetails.route) { backStackEntry ->
            val productId = Uri.decode(backStackEntry.arguments?.getString("productId") ?: "")
            ProductDetailsScreen(
                productId = productId,
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
            ) { simId ->
                navController.navigate(Routes.ProductDetails.createRoute(simId))
            }
        }

        composable(Routes.Cart.route) {
            CartScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onProceedToBuy = {
                    navController.navigate(Routes.Checkout.route)
                },
                onStartShopping = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Checkout.route) {
            CheckoutScreen(
                storeViewModel = storeViewModel,
                deliveryLocationViewModel = deliveryLocationViewModel,
                onBackClick = { navController.popBackStack() },
                onPickLocation = { navController.navigate(Routes.LocationPicker.route) },
                onOrderPlaced = {
                    navController.navigate(Routes.Orders.route) {
                        popUpTo(Routes.Home.route) { inclusive = false }
                    }
                    navController.navigate(Routes.OrderDetails.createRoute(it))
                }
            )
        }

        composable(Routes.LocationPicker.route) {
            val pickerViewModel: LocationPickerViewModel = viewModel()
            LocationPickerScreen(
                viewModel = pickerViewModel,
                onConfirm = { deliveryLocationViewModel.confirm(it); navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.Favorites.route) {
            FavoritesScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetails.createRoute(productId))
                }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onOrdersClick = { navController.navigate(Routes.Orders.route) },
                onAddressClick = { navController.navigate(Routes.LocationPicker.route) },
                onLogoutClick = {
                    profileViewModel.logout()
                    performLogout()
                }
            )
        }

        composable(Routes.Orders.route) {
            OrdersScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(Routes.OrderDetails.createRoute(orderId))
                }
            )
        }

        composable(Routes.OrderDetails.route) { backStackEntry ->
            val orderId = Uri.decode(backStackEntry.arguments?.getString("orderId") ?: "")
            val order = storeViewModel.orderById(orderId)
            OrderDetailsScreen(
                order = order,
                onBackClick = { navController.popBackStack() },
                onReorder = { selectedOrder ->
                    if (storeViewModel.reorder(selectedOrder.orderId)) {
                        val address = selectedOrder.deliveryAddress
                        val latitude = selectedOrder.deliveryLatitude
                        val longitude = selectedOrder.deliveryLongitude
                        if (address != null && latitude != null && longitude != null) {
                            deliveryLocationViewModel.confirm(
                                DeliveryLocation(latitude, longitude, address)
                            )
                        }
                        navController.navigate(Routes.Checkout.route)
                    }
                }
            )
        }
    }
}
