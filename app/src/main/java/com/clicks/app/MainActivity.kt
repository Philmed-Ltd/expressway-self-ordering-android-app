package com.clicks.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.clicks.app.network.ApiClient
import com.clicks.app.navigation.NavGraph
import com.clicks.app.feature.onboarding.splash.SplashState
import com.clicks.app.feature.onboarding.splash.SplashViewModel
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import com.clicks.app.lib.theme.ClicksTheme

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.state.value == SplashState.Loading
        }

        enableEdgeToEdge()
        setContent {
            ClicksTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
                        NavGraph()
                    }
                }
            }
        }
    }
}
