package com.clicks.app.lib.theme

import android.content.Context
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

object AppThemePreferences {
    private const val PREFERENCES_NAME = "clicks_app_appearance"
    private const val THEME_MODE_KEY = "theme_mode"

    private var preferences: android.content.SharedPreferences? = null

    var mode by mutableStateOf(AppThemeMode.SYSTEM)
        private set

    fun initialize(context: Context) {
        preferences = context.applicationContext.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        val savedMode = preferences?.getString(THEME_MODE_KEY, null)
        mode = AppThemeMode.entries.firstOrNull { it.name == savedMode } ?: AppThemeMode.SYSTEM
    }

    fun updateMode(mode: AppThemeMode) {
        this.mode = mode
        preferences?.edit { putString(THEME_MODE_KEY, mode.name) }
    }

    fun darkTheme(systemDarkTheme: Boolean): Boolean = when (mode) {
        AppThemeMode.SYSTEM -> systemDarkTheme
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
}
