package com.clicks.app.feature.onboarding.ui

import androidx.compose.runtime.Composable

@Composable
fun OnboardingContainerScreen(
    onFinished: () -> Unit
) {
    OnboardingScreen(onFinished = onFinished)
}
