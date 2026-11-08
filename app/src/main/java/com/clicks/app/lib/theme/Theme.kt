package com.clicks.app.lib.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalClicksDarkTheme = compositionLocalOf { false }

private val ClicksDarkColorScheme = darkColorScheme(
    primary = Color(0xFF48C78E),
    onPrimary = Color(0xFF06251A),
    primaryContainer = Color(0xFF173D2D),
    onPrimaryContainer = Color(0xFFB7F1D2),
    background = Color(0xFF101A16),
    onBackground = Color(0xFFE7F0EB),
    surface = Color(0xFF15221C),
    onSurface = Color(0xFFE7F0EB),
    surfaceVariant = Color(0xFF1E3028),
    onSurfaceVariant = Color(0xFFB4C7BC),
    outline = Color(0xFF536B5E),
    outlineVariant = Color(0xFF344A3D),
    error = Error,
    onError = Color(0xFF2D0909)
)

private val ClicksLightColorScheme = lightColorScheme(
    primary = ClicksGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF0F7F3),
    onPrimaryContainer = Color(0xFF163E32),
    background = Color(0xFFF7F9F7),
    onBackground = Color(0xFF163E32),
    surface = Color.White,
    onSurface = Color(0xFF163E32),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF52645C),
    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFE5E7EB),
    error = Error,
    onError = Color.White
)

@Composable
fun ClicksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalClicksDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) ClicksDarkColorScheme else ClicksLightColorScheme,
            typography = ClicksTypography,
            content = content
        )
    }
}
