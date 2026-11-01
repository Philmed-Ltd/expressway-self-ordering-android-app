package com.clicks.app.lib.session

import android.content.Context
import androidx.core.content.edit
import com.clicks.app.feature.auth.model.Authentication
import com.clicks.app.feature.auth.service.AuthenticationLocalSrc
import java.util.Date

class SessionRepository(
    context: Context,
    private val authLocalSrc: AuthenticationLocalSrc = AuthenticationLocalSrc(context)
) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String, expiryTime: Date = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) {
        val auth = Authentication().apply {
            id = 1
            authToken = token
            this.expiryTime = expiryTime
        }
        authLocalSrc.insertSync(auth)
    }

    fun getToken(): String? = authLocalSrc.getToken()

    fun isLoggedIn(): Boolean = authLocalSrc.exists()

    fun clearSession() {
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
