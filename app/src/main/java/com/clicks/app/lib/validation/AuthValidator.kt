package com.clicks.app.lib.validation

/**
 * Pure Kotlin (no android.util.Patterns) so it can be unit tested on the JVM
 * and shared by Login, Register and Password Recovery.
 * Keep MIN_PASSWORD_LENGTH identical to what the backend enforces.
 */
object AuthValidators {

    const val MIN_PASSWORD_LENGTH = 8
    const val OTP_LENGTH = 5

    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun isValidCode(code: String): Boolean =
        code.length == OTP_LENGTH && code.all { it.isDigit() }
}
