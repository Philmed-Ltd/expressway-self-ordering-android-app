package com.clicks.app.lib.session

import android.content.Context
import androidx.core.content.edit
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import java.util.Date

class SessionRepository(
    context: Context,
    private val authLocalSrc: AuthenticationLocalSrc = AuthenticationLocalSrc(context)
) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getExpiryTime(): Date? = authLocalSrc.getStoredAuthentication()?.expiryTime

    fun checkSessionExpiryNow() {
        SessionManager.checkSessionExpiryNow(authLocalSrc)
    }

    fun isLoggedIn(): Boolean = authLocalSrc.exists()

    fun clearSession() {
        SessionManager.cancelSessionExpiry()
        authLocalSrc.delete()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    private companion object {
        const val PREFS_NAME = "clicks_app_session"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
