package com.clicks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.clicks.app.network.ApiClient
import com.clicks.app.navigation.NavGraph
import com.clicks.app.feature.onboarding.splash.SplashState
import com.clicks.app.feature.onboarding.splash.SplashViewModel
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import com.clicks.app.lib.theme.ClicksTheme
import com.clicks.app.lib.theme.AppThemePreferences

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val authLocalSrc = AuthenticationLocalSrc(applicationContext)
        ApiClient.initialize(applicationContext) { authLocalSrc.getToken() }
        AppThemePreferences.initialize(applicationContext)

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.state.value == SplashState.Loading
        }

        enableEdgeToEdge()
        setContent {
            val darkTheme = AppThemePreferences.darkTheme(isSystemInDarkTheme())
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            ClicksTheme(darkTheme = darkTheme) {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
                        NavGraph()
                    }
                }
            }
        }
    }
}
