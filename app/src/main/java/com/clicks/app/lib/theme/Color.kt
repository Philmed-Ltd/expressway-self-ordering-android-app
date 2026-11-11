package com.clicks.app.lib.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

// ==========================================
// Brand & Main Green Colors
// ==========================================
val ClicksSplashBg = Color(0xFF04764E)
val ClicksGreenPrimary = Color(0xFF04764E)
val ClicksGreenLight = Color(0xFF10B981)
val ClicksGreenDark = Color(0xFF035437)
val ClicksGreen = Color(0xFF07865B)
val ClicksGreenInactive = Color(0xFF3B6E5B)
val ClicksMint: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) {
        Color(0xFFF0F7F3)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
val ClicksCream = Color(0xFFF7F5E9)
val BrandGreen = Color(0xFF0B6638)

// ==========================================
// Typography & Text Colors
// ==========================================
val ClicksTextPrimary = Color(0xFFFFFFFF)
val ClicksTextSecondary = Color(0xFFA3D6C3)
val ClicksTextPlaceholder = Color(0xFF9CA3AF)

val ClicksText: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
val ClicksMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val TextDark: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
val TextMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val LabelColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

// ==========================================
// Dark Theme Surface & Border Colors
// ==========================================
val ClicksDarkBgTop = Color(0xFF032619)
val ClicksInputBg = Color(0xFF083827)
val ClicksInputBorder = Color(0xFF0E5C41)
val ClicksInputFocusedBorder = Color(0xFF04764E)

// ==========================================
// Light Theme Surface, Border & Background Colors
// ==========================================
val SurfaceLight: Color
    @Composable get() = MaterialTheme.colorScheme.surface
val SurfaceLightAlt: Color
    @Composable get() = MaterialTheme.colorScheme.background
val BorderColor: Color
    @Composable get() = MaterialTheme.colorScheme.outlineVariant
val ClicksImageBackground: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val ProductBgLight: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val ProductCategoryBg: Color
    @Composable get() = MaterialTheme.colorScheme.primaryContainer
val WarmPeachBg: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) {
        Color(0xFFF2C299)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
val WarmCreamBg: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) {
        Color(0xFFF7E2C6)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
// ==========================================
// Semantic & Feedback Colors
// ==========================================
val Success = Color(0xFF10B981)
val SuccessBg: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFFF0FDF4) else Color(0xFF173D2D)
val SuccessBorder: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFF86EFAC) else Color(0xFF3B8A62)
val SuccessText: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFF166534) else Color(0xFFB7F1D2)
val SuccessIcon: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFF16A34A) else Color(0xFF72D7A0)

val Warning = Color(0xFFD97706)
val WarningBg: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFFFEF3C7) else Color(0xFF493719)

val Error = Color(0xFFEF4444)
val ErrorBg: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFFFEF2F2) else Color(0xFF431E1E)

val ErrorBorder: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFFFCA5A5) else Color(0xFF994B4B)
val ErrorText: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFF991B1B) else Color(0xFFFFB4AB)
val ErrorIcon: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFFDC2626) else Color(0xFFFF8A80)
val InfoBg: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFFEFF6FF) else Color(0xFF1C334D)
val InfoText: Color
    @Composable get() = if (!LocalClicksDarkTheme.current) Color(0xFF2563EB) else Color(0xFFB4C9FF)

val StarGold = Color(0xFFFFB800)
